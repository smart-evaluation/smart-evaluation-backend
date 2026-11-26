
package com.smartexam.evaluation.service.impl;

import com.smartexam.common.exception.ResourceNotFoundException;
import com.smartexam.evaluation.dto.ManualEvaluationRequest;
import com.smartexam.evaluation.entity.ManualEvaluation;
import com.smartexam.evaluation.repository.ManualEvaluationRepository;
import com.smartexam.evaluation.service.ManualEvaluationService;
import com.smartexam.examattempt.entity.*;
import com.smartexam.examattempt.repository.ExamAttemptAnswerRepository;
import com.smartexam.examattempt.repository.ExamAttemptRepository;
import com.smartexam.exampaper.entity.ExamPaper;
import com.smartexam.exampaper.entity.ExamPaperQuestion;
import com.smartexam.exampaper.repository.ExamPaperQuestionRepository;
import com.smartexam.question.entity.Question;
import com.smartexam.question.enums.QuestionType;
import com.smartexam.question.repository.QuestionRepository;
import com.smartexam.user.entity.User;
import com.smartexam.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManualEvaluationServiceImpl
        implements ManualEvaluationService {

    private final ManualEvaluationRepository manualEvaluationRepository;
    private final ExamAttemptRepository examAttemptRepository;
    private final ExamAttemptAnswerRepository examAttemptAnswerRepository;
    private final ExamPaperQuestionRepository examPaperQuestionRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void evaluateAnswer(
            Long attemptId,
            Long questionId,
            String adminEmail,
            ManualEvaluationRequest request) {

        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Admin not found"));

        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Exam attempt not found"));

        if (attempt.getStatus() != ExamAttemptStatus.SUBMITTED
                && attempt.getStatus() != ExamAttemptStatus.AUTO_SUBMITTED) {
            throw new IllegalArgumentException(
                    "Only submitted attempts can be evaluated");
        }

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Question not found"));

        ExamPaperQuestion paperQuestion =
                examPaperQuestionRepository
                        .findByExamPaperIdAndQuestionId(
                                attempt.getExamPaper().getId(),
                                questionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Question does not belong to this exam paper"));

        QuestionType questionType = question.getQuestionType();

        if (questionType != QuestionType.NUMERICAL
                && questionType != QuestionType.DESCRIPTIVE) {
            throw new IllegalArgumentException(
                    "Manual evaluation is allowed only for numerical and descriptive questions");
        }

        ExamAttemptAnswer answer =
                examAttemptAnswerRepository
                        .findByAttemptIdAndQuestionId(attemptId, questionId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Student has not submitted an answer for this question"));

        if (answer.getAnswerText() == null
                || answer.getAnswerText().isBlank()) {
            throw new IllegalArgumentException(
                    "Cannot evaluate an unanswered question");
        }

        BigDecimal maximumMarks = paperQuestion.getMarks();
        BigDecimal awardedMarks = request.awardedMarks();

        if (awardedMarks.compareTo(BigDecimal.ZERO) < 0
                || awardedMarks.compareTo(maximumMarks) > 0) {
            throw new IllegalArgumentException(
                    "Awarded marks must be between 0 and " + maximumMarks);
        }

        ManualEvaluation evaluation =
                manualEvaluationRepository
                        .findByAttemptIdAndQuestionId(attemptId, questionId)
                        .orElseGet(ManualEvaluation::new);

        evaluation.setAttempt(attempt);
        evaluation.setQuestion(question);
        evaluation.setEvaluatedBy(admin);
        evaluation.setAwardedMarks(awardedMarks);
        evaluation.setMaximumMarks(maximumMarks);
        evaluation.setFeedback(request.feedback());
        evaluation.setEvaluatedAt(LocalDateTime.now());

        manualEvaluationRepository.save(evaluation);

        // Recalculate the complete exam result.
        recalculateFinalResult(attempt);
    }

    private void recalculateFinalResult(ExamAttempt attempt) {

        ExamPaper examPaper = attempt.getExamPaper();

        List<ExamPaperQuestion> paperQuestions =
                examPaperQuestionRepository
                        .findByExamPaperIdOrderByQuestionOrderAsc(
                                examPaper.getId());

        List<ExamAttemptAnswer> answers =
                examAttemptAnswerRepository
                        .findByAttemptId(attempt.getId());

        Map<Long, ExamAttemptAnswer> answerMap = answers.stream()
                .collect(Collectors.toMap(
                        answer -> answer.getQuestion().getId(),
                        Function.identity(),
                        (first, second) -> first));

        Map<Long, ManualEvaluation> evaluationMap =
                manualEvaluationRepository.findByAttemptId(attempt.getId())
                        .stream()
                        .collect(Collectors.toMap(
                                evaluation -> evaluation.getQuestion().getId(),
                                Function.identity(),
                                (first, second) -> first));

        BigDecimal totalObtainedMarks = BigDecimal.ZERO;
        boolean pendingManualEvaluation = false;

        for (ExamPaperQuestion paperQuestion : paperQuestions) {

            Question question = paperQuestion.getQuestion();

            ExamAttemptAnswer answer =
                    answerMap.get(question.getId());

            if (answer == null) {
                continue;
            }

            QuestionType questionType = question.getQuestionType();

            // Numerical and descriptive questions.
            if (questionType == QuestionType.NUMERICAL
                    || questionType == QuestionType.DESCRIPTIVE) {

                boolean answered = answer.getAnswerText() != null
                        && !answer.getAnswerText().isBlank();

                if (!answered) {
                    continue;
                }

                ManualEvaluation evaluation =
                        evaluationMap.get(question.getId());

                if (evaluation == null) {
                    pendingManualEvaluation = true;
                } else {
                    totalObtainedMarks =
                            totalObtainedMarks.add(
                                    evaluation.getAwardedMarks());
                }

                continue;
            }

            // Objective questions.
            Set<Long> selectedOptionIds =
                    answer.getSelectedOptions()
                            .stream()
                            .map(selected -> selected.getOption().getId())
                            .collect(Collectors.toSet());

            // An unanswered objective question receives zero marks.
            if (selectedOptionIds.isEmpty()) {
                continue;
            }

            Set<Long> correctOptionIds =
                    question.getOptions()
                            .stream()
                            .filter(option ->
                                    Boolean.TRUE.equals(option.getCorrect()))
                            .map(option -> option.getId())
                            .collect(Collectors.toSet());

            boolean correctAnswer =
                    !correctOptionIds.isEmpty()
                            && selectedOptionIds.equals(correctOptionIds);

            if (correctAnswer) {
                totalObtainedMarks =
                        totalObtainedMarks.add(paperQuestion.getMarks());
            } else if (Boolean.TRUE.equals(
                    examPaper.getNegativeMarkingEnabled())) {

                totalObtainedMarks =
                        totalObtainedMarks.subtract(
                                paperQuestion.getNegativeMarks());
            }
        }

        // The total exam score cannot be negative.
        if (totalObtainedMarks.compareTo(BigDecimal.ZERO) < 0) {
            totalObtainedMarks = BigDecimal.ZERO;
        }

        attempt.setObtainedMarks(totalObtainedMarks);

        if (pendingManualEvaluation) {

            attempt.setEvaluationStatus(
                    EvaluationStatus.MANUAL_REVIEW);

            attempt.setPassed(null);

        } else {

            attempt.setEvaluationStatus(
                    EvaluationStatus.COMPLETED);

            attempt.setPassed(
                    totalObtainedMarks.compareTo(
                            examPaper.getPassingMarks()) >= 0);
        }

        examAttemptRepository.save(attempt);
    }
}
