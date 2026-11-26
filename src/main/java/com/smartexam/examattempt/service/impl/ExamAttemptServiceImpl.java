package com.smartexam.examattempt.service.impl;

import com.smartexam.catalogue.exception.ResourceNotFoundException;
import com.smartexam.examattempt.dto.*;
import com.smartexam.examattempt.entity.*;
import com.smartexam.examattempt.exception.ExamAttemptException;
import com.smartexam.examattempt.repository.ExamAttemptAnswerRepository;
import com.smartexam.examattempt.repository.ExamAttemptRepository;
import com.smartexam.examattempt.service.ExamAttemptService;
import com.smartexam.exampaper.entity.ExamPaper;
import com.smartexam.exampaper.entity.ExamPaperQuestion;
import com.smartexam.exampaper.enums.ExamPaperStatus;
import com.smartexam.exampaper.repository.ExamPaperQuestionRepository;
import com.smartexam.exampaper.repository.ExamPaperRepository;
import com.smartexam.question.entity.Question;
import com.smartexam.question.entity.QuestionOption;
import com.smartexam.question.enums.QuestionType;
import com.smartexam.question.repository.QuestionOptionRepository;
import com.smartexam.question.repository.QuestionRepository;
import com.smartexam.user.entity.User;
import com.smartexam.user.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ExamAttemptServiceImpl implements ExamAttemptService {

    private final ExamAttemptRepository attemptRepository;
    private final ExamPaperRepository examPaperRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final ExamAttemptAnswerRepository answerRepository;
    private final QuestionOptionRepository optionRepository;
    private final ExamPaperQuestionRepository examPaperQuestionRepository;

    public ExamAttemptServiceImpl(
            ExamAttemptRepository attemptRepository,
            ExamPaperRepository examPaperRepository,
            UserRepository userRepository, QuestionRepository questionRepository, ExamAttemptAnswerRepository answerRepository, QuestionOptionRepository optionRepository, ExamPaperQuestionRepository examPaperQuestionRepository) {

        this.attemptRepository = attemptRepository;
        this.examPaperRepository = examPaperRepository;
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.optionRepository = optionRepository;
        this.examPaperQuestionRepository = examPaperQuestionRepository;
    }

    @Override
    @Transactional
    public StartExamResponse startExam(
            Long examPaperId,
            String studentEmail) {

        // 1. Find logged-in student
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() ->
                        new ExamAttemptException("Student not found"));

        if (!Boolean.TRUE.equals(student.getEnabled())) {
            throw new ExamAttemptException(
                    "Your account is disabled");
        }

        // 2. Find exam paper
        ExamPaper paper = examPaperRepository.findById(examPaperId)
                .orElseThrow(() ->
                        new ExamAttemptException(
                                "Exam paper not found"));

        // 3. Verify publication status
        if (paper.getStatus() != ExamPaperStatus.PUBLISHED) {
            throw new ExamAttemptException(
                    "This examination is not published");
        }

        LocalDateTime now = LocalDateTime.now();

        // 4. Verify examination availability
        if (paper.getStartTime() != null
                && now.isBefore(paper.getStartTime())) {
            throw new ExamAttemptException(
                    "Examination has not started yet");
        }

        if (paper.getEndTime() != null
                && !now.isBefore(paper.getEndTime())) {
            throw new ExamAttemptException(
                    "Examination availability has ended");
        }

        // 5. Verify questions
        if (paper.getQuestions() == null
                || paper.getQuestions().isEmpty()) {
            throw new ExamAttemptException(
                    "No questions are assigned to this examination");
        }

        for (ExamPaperQuestion item : paper.getQuestions()) {
            if (!Boolean.TRUE.equals(item.getQuestion().getActive())) {
                throw new ExamAttemptException(
                        "This examination contains an inactive question");
            }
        }

        // 6. Prevent duplicate active attempts
        var existingAttempt =
                attemptRepository
                        .findByStudentIdAndExamPaperIdAndStatus(
                                student.getId(),
                                examPaperId,
                                ExamAttemptStatus.IN_PROGRESS
                        );

        if (existingAttempt.isPresent()) {
            ExamAttempt existing = existingAttempt.get();

            if (now.isBefore(existing.getExpiresAt())) {
                return toResponse(existing);
            }

            throw new ExamAttemptException(
                    "Your previous attempt has expired. "
                            + "It must be submitted before another attempt can start.");
        }

        // 7. Calculate server-controlled expiry
        LocalDateTime expiresAt =
                now.plusMinutes(paper.getDurationMinutes());

        if (paper.getEndTime() != null
                && expiresAt.isAfter(paper.getEndTime())) {
            expiresAt = paper.getEndTime();
        }

        // 8. Create attempt
        ExamAttempt attempt = new ExamAttempt();

        attempt.setExamPaper(paper);
        attempt.setStudent(student);
        attempt.setStartedAt(now);
        attempt.setExpiresAt(expiresAt);
        attempt.setStatus(ExamAttemptStatus.IN_PROGRESS);
        attempt.setTotalMarks(paper.getTotalMarks());

        ExamAttempt savedAttempt =
                attemptRepository.save(attempt);

        return toResponse(savedAttempt);
    }

    private StartExamResponse toResponse(ExamAttempt attempt) {

        ExamPaper paper = attempt.getExamPaper();

        return new StartExamResponse(
                attempt.getId(),
                paper.getId(),
                paper.getTitle(),
                paper.getDurationMinutes(),
                paper.getTotalMarks(),
                attempt.getStartedAt(),
                attempt.getExpiresAt(),
                attempt.getStatus()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ExamAttemptQuestionsResponse getAttemptQuestions(
            Long attemptId,
            String studentEmail) {

        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() ->
                        new ExamAttemptException("Student not found"));

        ExamAttempt attempt = attemptRepository
                .findByIdAndStudentId(attemptId, student.getId())
                .orElseThrow(() ->
                        new ExamAttemptException("Exam attempt not found"));

        if (attempt.getStatus() != ExamAttemptStatus.IN_PROGRESS) {
            throw new ExamAttemptException(
                    "This exam attempt is no longer active");
        }

        if (!LocalDateTime.now().isBefore(attempt.getExpiresAt())) {
            throw new ExamAttemptException(
                    "Exam time has expired");
        }

        ExamPaper paper = attempt.getExamPaper();

        // Fetch previously saved answers
        List<ExamAttemptAnswer> savedAnswers =
                answerRepository.findByAttemptId(attemptId);

        Map<Long, ExamAttemptAnswer> answerMap =
                savedAnswers.stream().collect(
                        Collectors.toMap(
                                answer -> answer.getQuestion().getId(),
                                answer -> answer
                        )
                );

        List<StudentExamQuestionResponse> questions =
                paper.getQuestions().stream()
                        .sorted(Comparator.comparing(
                                ExamPaperQuestion::getQuestionOrder))
                        .map(item -> {

                            Question question = item.getQuestion();

                            List<StudentExamQuestionOptionResponse> options =
                                    question.getOptions().stream()
                                            .sorted(Comparator.comparing(
                                                    QuestionOption::getDisplayOrder))
                                            .map(option ->
                                                    new StudentExamQuestionOptionResponse(
                                                            option.getId(),
                                                            option.getOptionText(),
                                                            option.getDisplayOrder()
                                                    ))
                                            .toList();

                            ExamAttemptAnswer savedAnswer =
                                    answerMap.get(question.getId());

                            List<Long> selectedOptionIds =
                                    savedAnswer == null
                                            ? List.of()
                                            : savedAnswer.getSelectedOptions()
                                            .stream()
                                            .map(selected ->
                                                    selected.getOption().getId())
                                            .toList();

                            String answerText =
                                    savedAnswer == null
                                            ? null
                                            : savedAnswer.getAnswerText();

                            boolean answered =
                                    !selectedOptionIds.isEmpty()
                                            || (answerText != null
                                            && !answerText.isBlank());

                            return new StudentExamQuestionResponse(
                                    question.getId(),
                                    question.getQuestionText(),
                                    question.getQuestionType().name(),
                                    question.getDifficulty().name(),
                                    item.getMarks(),
                                    options,
                                    selectedOptionIds,
                                    answerText,
                                    answered
                            );
                        })
                        .toList();

        return new ExamAttemptQuestionsResponse(
                attempt.getId(),
                paper.getTitle(),
                attempt.getStartedAt(),
                attempt.getExpiresAt(),
                questions
        );
    }


    @Override
    @Transactional
    public SaveAnswerResponse saveAnswer(
            Long attemptId,
            String studentEmail,
            SaveAnswerRequest request) {

        // 1. Find logged-in student
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() ->
                        new ExamAttemptException("Student not found"));

        // 2. Find attempt belonging to this student
        ExamAttempt attempt = attemptRepository
                .findByIdAndStudentId(attemptId, student.getId())
                .orElseThrow(() ->
                        new ExamAttemptException("Exam attempt not found"));

        LocalDateTime now = LocalDateTime.now();

        // 3. Validate attempt status
        if (attempt.getStatus() != ExamAttemptStatus.IN_PROGRESS) {
            throw new ExamAttemptException(
                    "This exam attempt is no longer active.");
        }

        // 4. Validate exam expiry
        if (!now.isBefore(attempt.getExpiresAt())) {
            throw new ExamAttemptException(
                    "Examination time has expired");
        }
        if (!attempt.getExpiresAt().isAfter(LocalDateTime.now())) {
            throw new IllegalStateException(
                    "Exam time has expired. Your exam will be submitted automatically."
            );
        }
        // 5. Validate question belongs to this exam
        ExamPaperQuestion paperQuestion =
                attempt.getExamPaper().getQuestions().stream()
                        .filter(item -> item.getQuestion().getId()
                                .equals(request.questionId()))
                        .findFirst()
                        .orElseThrow(() ->
                                new ExamAttemptException(
                                        "Question does not belong to this exam"));

        Question question = paperQuestion.getQuestion();

        // 6. Validate selected option IDs
        List<Long> selectedIds =
                request.selectedOptionIds() == null
                        ? List.of()
                        : request.selectedOptionIds();

        if (selectedIds.stream().anyMatch(Objects::isNull)) {
            throw new ExamAttemptException(
                    "Selected option IDs cannot contain null");
        }

        if (selectedIds.size() != selectedIds.stream().distinct().count()) {
            throw new ExamAttemptException(
                    "Duplicate selected options are not allowed");
        }

        // 7. Validate answer text and selected options
        String answerText = request.answerText();

        if (answerText != null && answerText.length() > 10000) {
            throw new ExamAttemptException(
                    "Answer text exceeds the maximum length");
        }

        if (answerText != null && !selectedIds.isEmpty()) {
            throw new ExamAttemptException(
                    "Submit either selected options or answer text, not both");
        }

        com.smartexam.question.enums.QuestionType questionType = question.getQuestionType();

        // 8. Validate according to question type
        if (questionType == QuestionType.NUMERICAL
                || questionType == QuestionType.DESCRIPTIVE) {

            if (!selectedIds.isEmpty()) {
                throw new ExamAttemptException(
                        "This question requires a text answer");
            }

        } else {

            if (answerText != null && !answerText.isBlank()) {
                throw new ExamAttemptException(
                        "This question requires option selection");
            }
        }

        // 9. Validate selected options
        List<QuestionOption> options =
                optionRepository.findAllById(selectedIds);

        if (options.size() != selectedIds.size()) {
            throw new ExamAttemptException(
                    "One or more selected options are invalid");
        }

        boolean invalidOption = options.stream()
                .anyMatch(option ->
                        !option.getQuestion().getId()
                                .equals(question.getId()));

        if (invalidOption) {
            throw new ExamAttemptException(
                    "Selected option does not belong to this question");
        }

        // 10. Single-choice and true/false validation
        if (questionType == QuestionType.SINGLE_CHOICE
                || questionType == QuestionType.TRUE_FALSE) {

            if (selectedIds.size() > 1) {
                throw new ExamAttemptException(
                        "Only one option can be selected");
            }
        }

        // 11. Find existing answer or create new answer
        ExamAttemptAnswer answer = answerRepository
                .findByAttemptIdAndQuestionId(
                        attemptId,
                        question.getId()
                )
                .orElseGet(() -> {
                    ExamAttemptAnswer newAnswer =
                            new ExamAttemptAnswer();

                    newAnswer.setAttempt(attempt);
                    newAnswer.setQuestion(question);

                    return newAnswer;
                });

        // 12. Update answer
        answer.setAnswerText(answerText);
        answer.clearSelectedOptions();

        for (QuestionOption option : options) {
            answer.addSelectedOption(
                    new ExamAttemptAnswerOption(answer, option)
            );
        }

        // 13. Save answer
        ExamAttemptAnswer saved =
                answerRepository.save(answer);

        // 14. Return response
        return new SaveAnswerResponse(
                attemptId,
                question.getId(),
                "Answer saved successfully",
                saved.getSavedAt()
        );
    }

    @Override
    @Transactional
    public SubmitExamResponse submitExam(
            Long attemptId,
            String studentEmail) {

        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() ->
                        new ExamAttemptException("Student not found"));

        ExamAttempt attempt = attemptRepository
                .findByIdAndStudentIdForUpdate(
                        attemptId,
                        student.getId()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException("Exam attempt not found"));

        // Idempotency: return the existing result if already submitted.
        if (attempt.getStatus() == ExamAttemptStatus.SUBMITTED
                || attempt.getStatus() == ExamAttemptStatus.AUTO_SUBMITTED) {
            return toSubmitResponse(attempt);
        }

        if (attempt.getStatus() != ExamAttemptStatus.IN_PROGRESS) {
            throw new ExamAttemptException(
                    "This examination cannot be submitted");
        }

        LocalDateTime now = LocalDateTime.now();

        boolean autoSubmitted = !now.isBefore(attempt.getExpiresAt());

        var paper = attempt.getExamPaper();

        List<ExamAttemptAnswer> answers =
                answerRepository.findByAttemptId(attemptId);

        Map<Long, ExamAttemptAnswer> answerMap =
                answers.stream().collect(Collectors.toMap(
                        answer -> answer.getQuestion().getId(),
                        answer -> answer
                ));

        BigDecimal obtainedMarks = BigDecimal.ZERO;
        boolean manualReviewRequired = false;

        for (ExamPaperQuestion paperQuestion : paper.getQuestions()) {

            Question question = paperQuestion.getQuestion();

            ExamAttemptAnswer answer = answerMap.get(question.getId());

            // Unanswered questions receive zero.
            if (answer == null) {
                continue;
            }

            QuestionType questionType = question.getQuestionType();

            if (questionType == QuestionType.NUMERICAL
                    || questionType == QuestionType.DESCRIPTIVE) {

                boolean hasTextAnswer =
                        answer.getAnswerText() != null
                                && !answer.getAnswerText().isBlank();

                if (hasTextAnswer) {
                    manualReviewRequired = true;
                }

                continue;
            }

            Set<Long> selectedOptionIds =
                    answer.getSelectedOptions().stream()
                            .map(selected ->
                                    selected.getOption().getId())
                            .collect(Collectors.toSet());

            Set<Long> correctOptionIds =
                    question.getOptions().stream()
                            .filter(QuestionOption::getCorrect)
                            .map(QuestionOption::getId)
                            .collect(Collectors.toSet());

            boolean correct = !selectedOptionIds.isEmpty()
                    && selectedOptionIds.equals(correctOptionIds);

            if (correct) {
                obtainedMarks = obtainedMarks.add(
                        paperQuestion.getMarks());
            } else if (paper.getNegativeMarkingEnabled()) {
                obtainedMarks = obtainedMarks.subtract(
                        paperQuestion.getNegativeMarks());
            }
        }

        // Do not allow a negative final score.
        obtainedMarks = obtainedMarks.max(BigDecimal.ZERO);

        EvaluationStatus evaluationStatus =
                manualReviewRequired
                        ? EvaluationStatus.MANUAL_REVIEW
                        : EvaluationStatus.COMPLETED;

        attempt.setObtainedMarks(obtainedMarks);
        attempt.setEvaluationStatus(evaluationStatus);

        // Passing result is finalized only after manual review.
        if (!manualReviewRequired) {
            attempt.setPassed(
                    obtainedMarks.compareTo(paper.getPassingMarks()) >= 0
            );
        } else {
            attempt.setPassed(null);
        }

        attempt.setSubmittedAt(now);

        attempt.setStatus(
                autoSubmitted
                        ? ExamAttemptStatus.AUTO_SUBMITTED
                        : ExamAttemptStatus.SUBMITTED
        );

        ExamAttempt savedAttempt = attemptRepository.save(attempt);

        return toSubmitResponse(savedAttempt);
    }

    private SubmitExamResponse toSubmitResponse(
            ExamAttempt attempt) {

        var paper = attempt.getExamPaper();

        return new SubmitExamResponse(
                attempt.getId(),
                paper.getTotalMarks(),
                attempt.getObtainedMarks(),
                paper.getPassingMarks(),
                attempt.getPassed(),
                attempt.getEvaluationStatus(),
                attempt.getStatus(),
                attempt.getSubmittedAt()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public StudentExamResultResponse getStudentResult(
            Long attemptId,
            String studentEmail) {

        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() ->
                        new ExamAttemptException("Student not found"));

        ExamAttempt attempt = attemptRepository
                .findByIdAndStudentId(attemptId, student.getId())
                .orElseThrow(() ->
                        new ExamAttemptException("Exam attempt not found"));

        if (attempt.getStatus() != ExamAttemptStatus.SUBMITTED
                && attempt.getStatus() != ExamAttemptStatus.AUTO_SUBMITTED) {

            throw new ExamAttemptException(
                    "Result is not available until examination submission");
        }

        var paper = attempt.getExamPaper();

        BigDecimal percentage = BigDecimal.ZERO;

        if (paper.getTotalMarks().compareTo(BigDecimal.ZERO) > 0) {
            percentage = attempt.getObtainedMarks()
                    .multiply(BigDecimal.valueOf(100))
                    .divide(
                            paper.getTotalMarks(),
                            2,
                            java.math.RoundingMode.HALF_UP
                    );
        }

        return new StudentExamResultResponse(
                attempt.getId(),
                paper.getId(),
                paper.getTitle(),
                paper.getTotalMarks(),
                attempt.getObtainedMarks(),
                paper.getPassingMarks(),
                percentage,
                attempt.getPassed(),
                attempt.getEvaluationStatus().name(),
                attempt.getStatus().name(),
                attempt.getStartedAt(),
                attempt.getSubmittedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<StudentExamAttemptSummaryResponse> getStudentAttempts(
            String studentEmail) {

        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Student not found"));

        List<ExamAttempt> attempts =
                attemptRepository
                        .findByStudentIdOrderByStartedAtDesc(student.getId());

        return attempts.stream()
                .map(attempt -> {
                    ExamPaper paper = attempt.getExamPaper();

                    return new StudentExamAttemptSummaryResponse(
                            attempt.getId(),
                            paper.getId(),
                            paper.getTitle(),
                            attempt.getStatus(),
                            attempt.getTotalMarks(),
                            attempt.getObtainedMarks(),
                            attempt.getPassed(),
                            attempt.getStartedAt(),
                            attempt.getSubmittedAt()
                    );
                })
                .toList();
    }

    @Transactional
    public void autoSubmitExpiredAttempt(Long attemptId) {

        ExamAttempt attempt = attemptRepository
                .findByIdForUpdate(attemptId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Exam attempt not found"));

        if (attempt.getStatus() != ExamAttemptStatus.IN_PROGRESS) {
            return;
        }

        if (attempt.getExpiresAt().isAfter(LocalDateTime.now())) {
            return;
        }

        // Reuse your existing evaluation logic.
        evaluateAndSubmit(attempt, ExamAttemptStatus.AUTO_SUBMITTED);
    }


    private void evaluateAndSubmit(
            ExamAttempt attempt,
            ExamAttemptStatus submissionStatus) {

        BigDecimal obtainedMarks = BigDecimal.ZERO;
        boolean requiresManualReview = false;

        ExamPaper paper = attempt.getExamPaper();

        List<ExamPaperQuestion> paperQuestions =
                examPaperQuestionRepository.findByExamPaperId(
                        paper.getId());

        List<ExamAttemptAnswer> answers =
                answerRepository.findByAttemptId(attempt.getId());

        Map<Long, ExamAttemptAnswer> answerMap = answers.stream()
                .collect(Collectors.toMap(
                        answer -> answer.getQuestion().getId(),
                        Function.identity()
                ));

        for (ExamPaperQuestion paperQuestion : paperQuestions) {

            Question question = paperQuestion.getQuestion();

            ExamAttemptAnswer answer =
                    answerMap.get(question.getId());

            // Unanswered questions receive zero marks.
            if (answer == null) {
                continue;
            }

            QuestionType questionType = question.getQuestionType();

            // Numerical and descriptive answers require manual evaluation.
            if (questionType == QuestionType.NUMERICAL
                    || questionType == QuestionType.DESCRIPTIVE) {

                boolean hasTextAnswer =
                        answer.getAnswerText() != null
                                && !answer.getAnswerText().isBlank();

                if (hasTextAnswer) {
                    requiresManualReview = true;
                }

                continue;
            }

            Set<Long> selectedOptionIds =
                    answer.getSelectedOptions().stream()
                            .map(selected ->
                                    selected.getOption().getId())
                            .collect(Collectors.toSet());

            Set<Long> correctOptionIds =
                    question.getOptions().stream()
                            .filter(QuestionOption::getCorrect)
                            .map(QuestionOption::getId)
                            .collect(Collectors.toSet());

            boolean isCorrect =
                    !selectedOptionIds.isEmpty()
                            && selectedOptionIds.equals(correctOptionIds);

            if (isCorrect) {

                obtainedMarks = obtainedMarks.add(
                        paperQuestion.getMarks());

            } else if (Boolean.TRUE.equals(
                    paper.getNegativeMarkingEnabled())) {

                obtainedMarks = obtainedMarks.subtract(
                        paperQuestion.getNegativeMarks());
            }
        }

        // Final score cannot be negative.
        obtainedMarks = obtainedMarks.max(BigDecimal.ZERO);

        EvaluationStatus evaluationStatus =
                requiresManualReview
                        ? EvaluationStatus.MANUAL_REVIEW
                        : EvaluationStatus.COMPLETED;

        attempt.setObtainedMarks(obtainedMarks);
        attempt.setEvaluationStatus(evaluationStatus);
        attempt.setStatus(submissionStatus);
        attempt.setSubmittedAt(LocalDateTime.now());

        // Finalize pass/fail only after manual evaluation is complete.
        if (!requiresManualReview) {

            attempt.setPassed(
                    obtainedMarks.compareTo(paper.getPassingMarks()) >= 0);

        } else {

            attempt.setPassed(null);
        }

        attemptRepository.save(attempt);
    }


    @Transactional(readOnly = true)
    public List<ExamAnswerReviewResponse> getAnswerReview(
            Long attemptId,
            String studentEmail) {

        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Student not found"));

        ExamAttempt attempt = attemptRepository
                .findByIdAndStudentId(attemptId, student.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Exam attempt not found"));

        List<ExamPaperQuestion> paperQuestions =
                examPaperQuestionRepository.findByExamPaperId(
                        attempt.getExamPaper().getId());

        List<ExamAttemptAnswer> answers =
                answerRepository.findByAttemptId(attemptId);

        Map<Long, ExamAttemptAnswer> answerMap = answers.stream()
                .collect(Collectors.toMap(
                        answer -> answer.getQuestion().getId(),
                        Function.identity()
                ));

        List<ExamAnswerReviewResponse> response = new ArrayList<>();

        int questionNumber = 1;

        for (ExamPaperQuestion paperQuestion : paperQuestions) {

            Long questionId = paperQuestion.getQuestion().getId();

            ExamAttemptAnswer answer = answerMap.get(questionId);

            QuestionAttemptStatus status;

            if (answer == null) {
                status = QuestionAttemptStatus.NOT_VISITED;
            } else {
                boolean hasSelectedOptions =
                        answer.getSelectedOptions() != null
                                && !answer.getSelectedOptions().isEmpty();

                boolean hasTextAnswer =
                        answer.getAnswerText() != null
                                && !answer.getAnswerText().isBlank();

                status = hasSelectedOptions || hasTextAnswer
                        ? QuestionAttemptStatus.ANSWERED
                        : QuestionAttemptStatus.NOT_ANSWERED;
            }

            response.add(new ExamAnswerReviewResponse(
                    questionId,
                    questionNumber++,
                    status
            ));
        }
        return response;
    }

    @Transactional(readOnly = true)
    public ExamAttemptResumeResponse resumeExam(
            Long attemptId,
            String studentEmail) {

        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Student not found"));

        ExamAttempt attempt = attemptRepository
                .findByIdAndStudentId(attemptId, student.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Exam attempt not found"));

        if (attempt.getStatus() != ExamAttemptStatus.IN_PROGRESS) {
            throw new IllegalStateException(
                    "This exam attempt cannot be resumed. Current status: "
                            + attempt.getStatus());
        }

        LocalDateTime now = LocalDateTime.now();

        if (!attempt.getExpiresAt().isAfter(now)) {
            throw new IllegalStateException(
                    "Exam time has expired. Please check your exam result.");
        }

        long remainingSeconds =
                Duration.between(now, attempt.getExpiresAt()).getSeconds();

        ExamPaper paper = attempt.getExamPaper();

        return new ExamAttemptResumeResponse(
                attempt.getId(),
                paper.getId(),
                paper.getTitle(),
                attempt.getStatus().name(),
                attempt.getStartedAt(),
                attempt.getExpiresAt(),
                remainingSeconds,
                "Exam attempt resumed successfully"
        );
    }

    @Transactional(readOnly = true)
    public ExamRemainingTimeResponse getRemainingTime(
            Long attemptId,
            String studentEmail) {

        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Student not found"));

        ExamAttempt attempt = attemptRepository
                .findByIdAndStudentId(attemptId, student.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Exam attempt not found"));

        if (attempt.getStatus() != ExamAttemptStatus.IN_PROGRESS) {
            throw new IllegalStateException(
                    "This exam attempt is no longer active."
            );
        }

        LocalDateTime now = LocalDateTime.now();

        long remainingSeconds = Math.max(
                0,
                Duration.between(now, attempt.getExpiresAt()).getSeconds()
        );

        return new ExamRemainingTimeResponse(
                attempt.getId(),
                remainingSeconds,
                attempt.getExpiresAt(),
                attempt.getStatus().name()
        );
    }

    @Transactional
    public void markQuestionVisited(
            Long attemptId,
            Long questionId,
            String studentEmail) {

        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Student not found"));

        ExamAttempt attempt = attemptRepository
                .findByIdAndStudentId(attemptId, student.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Exam attempt not found"));

        validateActiveAttempt(attempt);

        boolean questionBelongsToPaper =
                examPaperQuestionRepository
                        .existsByExamPaperIdAndQuestionId(
                                attempt.getExamPaper().getId(),
                                questionId
                        );

        if (!questionBelongsToPaper) {
            throw new ResourceNotFoundException(
                    "Question does not belong to this exam"
            );
        }

        ExamAttemptAnswer answer = answerRepository
                .findByAttemptIdAndQuestionId(attemptId, questionId)
                .orElseGet(() -> {
                    ExamAttemptAnswer newAnswer = new ExamAttemptAnswer();
                    newAnswer.setAttempt(attempt);

                    Question question = questionRepository.findById(questionId)
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Question not found"));

                    newAnswer.setQuestion(question);
                    return newAnswer;
                });

        answer.setVisited(true);

        answerRepository.save(answer);
    }
    private void validateActiveAttempt(ExamAttempt attempt) {

        if (attempt.getStatus() != ExamAttemptStatus.IN_PROGRESS) {
            throw new IllegalStateException(
                    "This exam attempt is no longer active."
            );
        }

        if (!attempt.getExpiresAt().isAfter(LocalDateTime.now())) {
            throw new IllegalStateException(
                    "Exam time has expired."
            );
        }
    }
    @Transactional(readOnly = true)
    public List<ExamQuestionNavigationResponse> getQuestionNavigation(
            Long attemptId,
            String studentEmail) {

        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Student not found"));

        ExamAttempt attempt = attemptRepository
                .findByIdAndStudentId(attemptId, student.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Exam attempt not found"));

        List<ExamPaperQuestion> paperQuestions =
                examPaperQuestionRepository.findByExamPaperId(
                        attempt.getExamPaper().getId());

        List<ExamAttemptAnswer> answers =
                answerRepository.findByAttemptId(attemptId);

        Map<Long, ExamAttemptAnswer> answerMap = answers.stream()
                .collect(Collectors.toMap(
                        answer -> answer.getQuestion().getId(),
                        Function.identity()
                ));

        List<ExamQuestionNavigationResponse> response = new ArrayList<>();

        int questionNumber = 1;

        for (ExamPaperQuestion paperQuestion : paperQuestions) {

            Long questionId = paperQuestion.getQuestion().getId();

            ExamAttemptAnswer answer = answerMap.get(questionId);

            boolean visited = answer != null
                    && Boolean.TRUE.equals(answer.getVisited());

            boolean markedForReview = answer != null
                    && Boolean.TRUE.equals(answer.getMarkedForReview());

            boolean hasSelectedOptions = answer != null
                    && answer.getSelectedOptions() != null
                    && !answer.getSelectedOptions().isEmpty();

            boolean hasTextAnswer = answer != null
                    && answer.getAnswerText() != null
                    && !answer.getAnswerText().isBlank();

            boolean answered = hasSelectedOptions || hasTextAnswer;

            QuestionNavigationStatus status;

            if (!visited) {
                status = QuestionNavigationStatus.NOT_VISITED;
            } else if (answered && markedForReview) {
                status = QuestionNavigationStatus.ANSWERED_AND_MARKED_FOR_REVIEW;
            } else if (answered) {
                status = QuestionNavigationStatus.ANSWERED;
            } else if (markedForReview) {
                status = QuestionNavigationStatus.MARKED_FOR_REVIEW;
            } else {
                status = QuestionNavigationStatus.NOT_ANSWERED;
            }

            response.add(new ExamQuestionNavigationResponse(
                    questionId,
                    questionNumber++,
                    status,
                    answered,
                    visited,
                    markedForReview
            ));
        }

        return response;
    }

    @Transactional
    public void markQuestionForReview(
            Long attemptId,
            Long questionId,
            String studentEmail,
            Boolean markedForReview) {

        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Student not found"));

        ExamAttempt attempt = attemptRepository
                .findByIdAndStudentId(attemptId, student.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Exam attempt not found"));

        if (attempt.getStatus() != ExamAttemptStatus.IN_PROGRESS) {
            throw new IllegalStateException(
                    "This exam attempt is no longer active.");
        }

        if (!attempt.getExpiresAt().isAfter(LocalDateTime.now())) {
            throw new IllegalStateException(
                    "Exam time has expired.");
        }

        ExamAttemptAnswer answer = answerRepository
                .findByAttemptIdAndQuestionId(attemptId, questionId)
                .orElseGet(() -> {
                    ExamAttemptAnswer newAnswer = new ExamAttemptAnswer();
                    newAnswer.setAttempt(attempt);
                    newAnswer.setQuestion(
                            questionRepository.findById(questionId)
                                    .orElseThrow(() ->
                                            new ResourceNotFoundException(
                                                    "Question not found"))
                    );
                    return newAnswer;
                });

        answer.setMarkedForReview(markedForReview);

        answerRepository.save(answer);
    }

    @Transactional(readOnly = true)
    public ExamSubmissionSummaryResponse getSubmissionSummary(
            Long attemptId,
            String studentEmail) {

        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Student not found"));

        ExamAttempt attempt = attemptRepository
                .findByIdAndStudentId(attemptId, student.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Exam attempt not found"));

        if (attempt.getStatus() != ExamAttemptStatus.IN_PROGRESS) {
            throw new IllegalStateException(
                    "This exam attempt is no longer active.");
        }

        List<ExamQuestionNavigationResponse> questions =
                getQuestionNavigation(attemptId, studentEmail);

        int total = questions.size();

        int answered = (int) questions.stream()
                .filter(q -> Boolean.TRUE.equals(q.answered()))
                .count();

        int marked = (int) questions.stream()
                .filter(q -> Boolean.TRUE.equals(q.markedForReview()))
                .count();

        int notVisited = (int) questions.stream()
                .filter(q -> !Boolean.TRUE.equals(q.visited()))
                .count();

        int unanswered = total - answered;

        return new ExamSubmissionSummaryResponse(
                attemptId,
                total,
                answered,
                unanswered,
                marked,
                notVisited,
                true
        );
    }
}