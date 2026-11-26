package com.smartexam.exampaper.service.impl;

import com.smartexam.catalogue.entity.Exam;
import com.smartexam.catalogue.entity.ExamLevel;
import com.smartexam.catalogue.repository.ExamLevelRepository;
import com.smartexam.catalogue.repository.ExamRepository;

import com.smartexam.exampaper.dto.request.*;
import com.smartexam.exampaper.dto.response.*;
import com.smartexam.exampaper.entity.*;
import com.smartexam.exampaper.enums.ExamPaperStatus;
import com.smartexam.exampaper.exception.ExamPaperException;
import com.smartexam.exampaper.repository.*;
import com.smartexam.exampaper.service.ExamPaperService;

import com.smartexam.question.entity.Question;
import com.smartexam.question.repository.QuestionRepository;

import com.smartexam.user.entity.User;
import com.smartexam.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class ExamPaperServiceImpl implements ExamPaperService {

    private final ExamPaperRepository examPaperRepository;
    private final ExamPaperQuestionRepository paperQuestionRepository;

    private final ExamRepository examRepository;
    private final ExamLevelRepository examLevelRepository;

    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    @Override
    public ExamPaperResponse createExamPaper(
            CreateExamPaperRequest request,
            String adminEmail
    ) {

        Exam exam = examRepository.findById(request.examId())
                .orElseThrow(() ->
                        new ExamPaperException("Exam not found")
                );

        ExamLevel level = examLevelRepository.findById(
                request.examLevelId()
        ).orElseThrow(() ->
                new ExamPaperException("Exam level not found")
        );

        if (!level.getExam().getId().equals(exam.getId())) {
            throw new ExamPaperException(
                    "Selected level does not belong to selected exam"
            );
        }

        if (request.startTime() != null
                && request.endTime() != null
                && !request.endTime().isAfter(request.startTime())) {

            throw new ExamPaperException(
                    "End time must be after start time"
            );
        }

        if (request.passingMarks().compareTo(BigDecimal.ZERO) < 0) {
            throw new ExamPaperException(
                    "Passing marks cannot be negative"
            );
        }

        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() ->
                        new ExamPaperException("Admin user not found")
                );

        ExamPaper paper = ExamPaper.builder()
                .exam(exam)
                .examLevel(level)
                .title(request.title().trim())
                .description(request.description())
                .instructions(request.instructions())
                .durationMinutes(request.durationMinutes())
                .totalMarks(BigDecimal.ZERO)
                .passingMarks(request.passingMarks())
                .negativeMarkingEnabled(request.negativeMarkingEnabled())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .status(ExamPaperStatus.DRAFT)
                .createdBy(admin)
                .build();

        return mapToResponse(examPaperRepository.save(paper));
    }

    @Override
    public ExamPaperResponse assignQuestions(
            Long examPaperId,
            AssignQuestionsRequest request
    ) {

        ExamPaper paper = examPaperRepository.findById(examPaperId)
                .orElseThrow(() ->
                        new ExamPaperException("Exam paper not found")
                );

        if (paper.getStatus() != ExamPaperStatus.DRAFT) {
            throw new ExamPaperException(
                    "Questions can only be assigned to draft papers"
            );
        }

        Set<Long> questionIds = new HashSet<>();
        Set<Integer> questionOrders = new HashSet<>();

        for (ExamPaperQuestionRequest item : request.questions()) {

            if (!questionIds.add(item.questionId())) {
                throw new ExamPaperException(
                        "Duplicate question ID: " + item.questionId()
                );
            }

            if (!questionOrders.add(item.questionOrder())) {
                throw new ExamPaperException(
                        "Duplicate question order: " + item.questionOrder()
                );
            }

            if (Boolean.FALSE.equals(paper.getNegativeMarkingEnabled())
                    && item.negativeMarks().compareTo(BigDecimal.ZERO) > 0) {

                throw new ExamPaperException(
                        "Negative marks must be zero when negative marking is disabled"
                );
            }

            Question question = questionRepository.findById(
                    item.questionId()
            ).orElseThrow(() ->
                    new ExamPaperException(
                            "Question not found: " + item.questionId()
                    )
            );

            if (!Boolean.TRUE.equals(question.getActive())) {
                throw new ExamPaperException(
                        "Inactive question cannot be assigned: "
                                + item.questionId()
                );
            }

            if (!question.getExamLevel().getId()
                    .equals(paper.getExamLevel().getId())) {

                throw new ExamPaperException(
                        "Question does not belong to the exam paper level"
                );
            }

            ExamPaperQuestion paperQuestion = ExamPaperQuestion.builder()
                    .question(question)
                    .questionOrder(item.questionOrder())
                    .marks(item.marks())
                    .negativeMarks(item.negativeMarks())
                    .build();

            paper.addQuestion(paperQuestion);
        }

        BigDecimal totalMarks = paper.getQuestions()
                .stream()
                .map(ExamPaperQuestion::getMarks)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (paper.getPassingMarks().compareTo(totalMarks) > 0) {
            throw new ExamPaperException(
                    "Passing marks cannot exceed total marks"
            );
        }

        paper.setTotalMarks(totalMarks);

        return mapToResponse(examPaperRepository.save(paper));
    }

    @Override
    @Transactional(readOnly = true)
    public ExamPaperResponse getExamPaperById(Long examPaperId) {

        ExamPaper paper = examPaperRepository.findById(examPaperId)
                .orElseThrow(() ->
                        new ExamPaperException("Exam paper not found")
                );

        return mapToResponse(paper);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamPaperResponse> getAllExamPapers() {

        return examPaperRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private ExamPaperResponse mapToResponse(ExamPaper paper) {

        List<ExamPaperQuestionResponse> questions =
                paper.getQuestions()
                        .stream()
                        .sorted((a, b) ->
                                a.getQuestionOrder()
                                        .compareTo(b.getQuestionOrder()))
                        .map(q -> new ExamPaperQuestionResponse(
                                q.getId(),
                                q.getQuestion().getId(),
                                q.getQuestion().getQuestionText(),
                                q.getQuestionOrder(),
                                q.getMarks(),
                                q.getNegativeMarks()
                        ))
                        .toList();

        return new ExamPaperResponse(
                paper.getId(),
                paper.getExam().getId(),
                paper.getExam().getName(),
                paper.getExamLevel().getId(),
                paper.getExamLevel().getName(),
                paper.getTitle(),
                paper.getDescription(),
                paper.getInstructions(),
                paper.getDurationMinutes(),
                paper.getTotalMarks(),
                paper.getPassingMarks(),
                paper.getNegativeMarkingEnabled(),
                paper.getStartTime(),
                paper.getEndTime(),
                paper.getStatus(),
                questions,
                paper.getCreatedAt()
        );
    }

    @Override
    public void publishExamPaper(Long examPaperId) {

        ExamPaper paper = examPaperRepository.findById(examPaperId)
                .orElseThrow(() ->
                        new ExamPaperException("Exam paper not found")
                );

        if (paper.getStatus() != ExamPaperStatus.DRAFT) {
            throw new ExamPaperException(
                    "Only draft examination papers can be published"
            );
        }

        if (paper.getQuestions() == null || paper.getQuestions().isEmpty()) {
            throw new ExamPaperException(
                    "At least one question is required before publishing"
            );
        }

        BigDecimal calculatedTotal = paper.getQuestions()
                .stream()
                .map(ExamPaperQuestion::getMarks)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (calculatedTotal.compareTo(paper.getTotalMarks()) != 0) {
            throw new ExamPaperException(
                    "Exam paper total marks do not match assigned questions"
            );
        }

        if (paper.getPassingMarks().compareTo(calculatedTotal) > 0) {
            throw new ExamPaperException(
                    "Passing marks cannot exceed total marks"
            );
        }

        if (paper.getStartTime() != null && paper.getEndTime() != null
                && !paper.getEndTime().isAfter(paper.getStartTime())) {
            throw new ExamPaperException(
                    "Exam end time must be after start time"
            );
        }

        for (ExamPaperQuestion mapping : paper.getQuestions()) {

            Question question = mapping.getQuestion();

            if (!Boolean.TRUE.equals(question.getActive())) {
                throw new ExamPaperException(
                        "An assigned question is inactive: " + question.getId()
                );
            }

            if (!question.getExamLevel().getId()
                    .equals(paper.getExamLevel().getId())) {
                throw new ExamPaperException(
                        "An assigned question belongs to another exam level"
                );
            }
        }

        paper.setStatus(ExamPaperStatus.PUBLISHED);

        examPaperRepository.save(paper);
    }

    @Override
    public void closeExamPaper(Long examPaperId) {

        ExamPaper paper = examPaperRepository.findById(examPaperId)
                .orElseThrow(() ->
                        new ExamPaperException("Exam paper not found")
                );

        if (paper.getStatus() != ExamPaperStatus.PUBLISHED) {
            throw new ExamPaperException(
                    "Only published examination papers can be closed"
            );
        }

        paper.setStatus(ExamPaperStatus.CLOSED);

        examPaperRepository.save(paper);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentExamPaperResponse> getPublishedExamPapers() {

        return examPaperRepository
                .findByStatusOrderByCreatedAtDesc(
                        ExamPaperStatus.PUBLISHED
                )
                .stream()
                .map(paper -> new StudentExamPaperResponse(
                        paper.getId(),
                        paper.getTitle(),
                        paper.getDescription(),
                        paper.getDurationMinutes(),
                        paper.getTotalMarks(),
                        paper.getPassingMarks(),
                        paper.getStartTime(),
                        paper.getEndTime(),
                        paper.getStatus().name()
                ))
                .toList();
    }
}