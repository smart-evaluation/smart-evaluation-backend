package com.smartexam.evaluation.service.impl;

import com.smartexam.catalogue.exception.ResourceNotFoundException;
import com.smartexam.evaluation.dto.AdminEvaluationAttemptResponse;
import com.smartexam.evaluation.dto.AdminEvaluationQuestionResponse;
import com.smartexam.evaluation.dto.PendingEvaluationResponse;
import com.smartexam.evaluation.entity.ManualEvaluation;
import com.smartexam.evaluation.repository.ManualEvaluationRepository;
import com.smartexam.evaluation.service.AdminEvaluationDashboardService;
import com.smartexam.examattempt.entity.EvaluationStatus;
import com.smartexam.examattempt.entity.ExamAttempt;
import com.smartexam.examattempt.entity.ExamAttemptAnswer;
import com.smartexam.examattempt.entity.ExamAttemptStatus;
import com.smartexam.examattempt.repository.ExamAttemptAnswerRepository;
import com.smartexam.examattempt.repository.ExamAttemptRepository;

import com.smartexam.exampaper.entity.ExamPaperQuestion;
import com.smartexam.exampaper.repository.ExamPaperQuestionRepository;
import com.smartexam.question.entity.Question;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminEvaluationDashboardServiceImpl
        implements AdminEvaluationDashboardService {

    private final ExamAttemptRepository examAttemptRepository;
    private final ManualEvaluationRepository manualEvaluationRepository;
    private final ExamPaperQuestionRepository examPaperQuestionRepository;
    private final ExamAttemptAnswerRepository examAttemptAnswerRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PendingEvaluationResponse> getPendingEvaluations() {

        return examAttemptRepository.findPendingManualEvaluations(
                        EvaluationStatus.MANUAL_REVIEW,
                        List.of(
                                ExamAttemptStatus.SUBMITTED,
                                ExamAttemptStatus.AUTO_SUBMITTED
                        )
                )
                .stream()
                .map(attempt -> new PendingEvaluationResponse(
                        attempt.getId(),
                        attempt.getStudent().getId(),
                        attempt.getExamPaper().getTitle(),
                        attempt.getSubmittedAt(),
                        attempt.getTotalMarks(),
                        attempt.getObtainedMarks(),
                        attempt.getEvaluationStatus().name()
                ))
                .toList();
    }


    @Override
    @Transactional(readOnly = true)
    public AdminEvaluationAttemptResponse getAttemptDetails(Long attemptId) {

        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Exam attempt not found"));

        List<ExamPaperQuestion> paperQuestions =
                examPaperQuestionRepository
                        .findByExamPaperIdOrderByQuestionOrderAsc(
                                attempt.getExamPaper().getId());

        List<ExamAttemptAnswer> answers =
                examAttemptAnswerRepository.findByAttemptId(attemptId);

        Map<Long, ExamAttemptAnswer> answerMap = answers.stream()
                .collect(Collectors.toMap(
                        answer -> answer.getQuestion().getId(),
                        Function.identity(),
                        (first, second) -> first
                ));

        Map<Long, ManualEvaluation> evaluationMap =
                manualEvaluationRepository.findByAttemptId(attemptId)
                        .stream()
                        .collect(Collectors.toMap(
                                evaluation -> evaluation.getQuestion().getId(),
                                Function.identity(),
                                (first, second) -> first
                        ));

        List<AdminEvaluationQuestionResponse> questionResponses =
                paperQuestions.stream()
                        .map(paperQuestion -> {

                            Question question = paperQuestion.getQuestion();

                            ExamAttemptAnswer answer =
                                    answerMap.get(question.getId());

                            ManualEvaluation evaluation =
                                    evaluationMap.get(question.getId());

                            String studentAnswer = null;

                            if (answer != null) {

                                if (answer.getAnswerText() != null
                                        && !answer.getAnswerText().isBlank()) {

                                    studentAnswer = answer.getAnswerText();

                                } else {

                                    studentAnswer = answer.getSelectedOptions()
                                            .stream()
                                            .map(selected ->
                                                    selected.getOption().getOptionText())
                                            .collect(Collectors.joining(", "));
                                }
                            }

                            return new AdminEvaluationQuestionResponse(
                                    question.getId(),
                                    paperQuestion.getQuestionOrder(),
                                    question.getQuestionText(),
                                    question.getQuestionType().name(),
                                    studentAnswer,
                                    paperQuestion.getMarks(),
                                    paperQuestion.getNegativeMarks(),
                                    evaluation != null
                                            ? evaluation.getAwardedMarks()
                                            : null,
                                    evaluation != null
                                            ? evaluation.getFeedback()
                                            : null,
                                    evaluation != null
                            );
                        })
                        .toList();

        return new AdminEvaluationAttemptResponse(
                attempt.getId(),
                attempt.getStudent().getId(),
                attempt.getStudent().getEmail(),
                attempt.getExamPaper().getTitle(),
                attempt.getStatus().name(),
                attempt.getEvaluationStatus().name(),
                attempt.getTotalMarks(),
                attempt.getObtainedMarks(),
                attempt.getPassed(),
                attempt.getSubmittedAt(),
                questionResponses
        );
    }
}