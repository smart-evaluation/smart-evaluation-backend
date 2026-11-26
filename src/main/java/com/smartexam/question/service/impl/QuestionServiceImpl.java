package com.smartexam.question.service.impl;

import com.smartexam.catalogue.entity.ExamLevel;
import com.smartexam.catalogue.entity.Subject;
import com.smartexam.catalogue.entity.Topic;
import com.smartexam.catalogue.repository.ExamLevelRepository;
import com.smartexam.catalogue.repository.SubjectRepository;
import com.smartexam.catalogue.repository.TopicRepository;
import com.smartexam.question.dto.request.CreateQuestionRequest;
import com.smartexam.question.dto.request.QuestionOptionRequest;
import com.smartexam.question.dto.request.UpdateQuestionRequest;
import com.smartexam.question.dto.response.*;
import com.smartexam.question.entity.Question;
import com.smartexam.question.entity.QuestionOption;
import com.smartexam.question.enums.QuestionDifficulty;
import com.smartexam.question.enums.QuestionType;
import com.smartexam.question.exception.InvalidQuestionException;
import com.smartexam.question.exception.QuestionNotFoundException;
import com.smartexam.question.repository.QuestionRepository;
import com.smartexam.question.service.QuestionService;
import com.smartexam.user.entity.User;
import com.smartexam.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final ExamLevelRepository examLevelRepository;
    private final SubjectRepository subjectRepository;
    private final TopicRepository topicRepository;
    private final UserRepository userRepository;

    @Override
    public QuestionResponse createQuestion(
            CreateQuestionRequest request,
            String adminEmail
    ) {

        ExamLevel examLevel = examLevelRepository.findById(
                request.examLevelId()
        ).orElseThrow(() ->
                new InvalidQuestionException("Exam level not found")
        );

        Subject subject = subjectRepository.findById(
                request.subjectId()
        ).orElseThrow(() ->
                new InvalidQuestionException("Subject not found")
        );

        if (!subject.getExamLevel().getId().equals(examLevel.getId())) {
            throw new InvalidQuestionException(
                    "Subject does not belong to the selected exam level"
            );
        }

        Topic topic = null;

        if (request.topicId() != null) {

            topic = topicRepository.findById(request.topicId())
                    .orElseThrow(() ->
                            new InvalidQuestionException("Topic not found")
                    );

            if (!topic.getSubject().getId().equals(subject.getId())) {
                throw new InvalidQuestionException(
                        "Topic does not belong to the selected subject"
                );
            }
        }

        validateQuestion(request);

        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() ->
                        new InvalidQuestionException("Admin user not found")
                );

        Question question = Question.builder()
                .examLevel(examLevel)
                .subject(subject)
                .topic(topic)
                .questionText(request.questionText().trim())
                .questionType(request.questionType())
                .difficulty(request.difficulty())
                .marks(request.marks())
                .negativeMarks(request.negativeMarks())
                .explanation(request.explanation())
                .language(request.language().trim().toUpperCase())
                .createdBy(admin)
                .active(true)
                .build();

        for (QuestionOptionRequest optionRequest : request.options()) {

            QuestionOption option = QuestionOption.builder()
                    .optionText(optionRequest.optionText().trim())
                    .displayOrder(optionRequest.displayOrder())
                    .correct(optionRequest.correct())
                    .build();

            question.addOption(option);
        }

        Question savedQuestion = questionRepository.save(question);

        return mapToAdminResponse(savedQuestion);
    }

    private void validateQuestion(CreateQuestionRequest request) {

        List<QuestionOptionRequest> options = request.options();

        long correctCount = options.stream().filter(option -> Boolean.TRUE.equals(option.correct())).count();

        switch (request.questionType()) {

            case SINGLE_CHOICE -> {

                if (options.size() < 2) {
                    throw new InvalidQuestionException("Single choice question requires at least two options");
                }

                if (correctCount != 1) {
                    throw new InvalidQuestionException("Single choice question must have exactly one correct option");
                }
            }

            case MULTIPLE_CHOICE -> {

                if (options.size() < 2) {
                    throw new InvalidQuestionException("Multiple choice question requires at least two options");
                }

                if (correctCount < 1) {
                    throw new InvalidQuestionException("Multiple choice question requires at least one correct option");
                }
            }

            case TRUE_FALSE -> {

                if (options.size() != 2 || correctCount != 1) {
                    throw new InvalidQuestionException("True/False requires exactly two options and one correct answer");
                }
            }

            case NUMERICAL, DESCRIPTIVE -> {
                if (options != null && !options.isEmpty()) {
                    throw new InvalidQuestionException("Options are not allowed for Numerical or Descriptive questions");
                }
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public QuestionResponse getQuestionById(Long questionId) {

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() ->
                        new QuestionNotFoundException(
                                "Question not found: " + questionId
                        )
                );

        return mapToAdminResponse(question);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponse> getAllQuestions() {

        return questionRepository.findAll()
                .stream()
                .map(this::mapToAdminResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponse> getQuestionsBySubject(Long subjectId) {

        return questionRepository.findBySubjectIdAndActiveTrue(subjectId)
                .stream()
                .map(this::mapToAdminResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentQuestionResponse> getStudentQuestionsBySubject(
            Long subjectId
    ) {

        return questionRepository.findBySubjectIdAndActiveTrue(subjectId)
                .stream()
                .map(this::mapToStudentResponse)
                .toList();
    }

    private QuestionResponse mapToAdminResponse(Question question) {

        List<QuestionOptionResponse> options = question.getOptions()
                .stream()
                .map(option -> new QuestionOptionResponse(
                        option.getId(),
                        option.getOptionText(),
                        option.getDisplayOrder(),
                        option.getCorrect()
                ))
                .toList();

        return new QuestionResponse(
                question.getId(),
                question.getExamLevel().getId(),
                question.getSubject().getId(),
                question.getTopic() != null
                        ? question.getTopic().getId() : null,
                question.getQuestionText(),
                question.getQuestionType(),
                question.getDifficulty(),
                question.getMarks(),
                question.getNegativeMarks(),
                question.getExplanation(),
                question.getLanguage(),
                question.getActive(),
                question.getCreatedBy().getId(),
                options,
                question.getCreatedAt(),
                question.getUpdatedAt()
        );
    }

    private StudentQuestionResponse mapToStudentResponse(
            Question question
    ) {

        List<StudentQuestionOptionResponse> options =
                question.getOptions()
                        .stream()
                        .map(option ->
                                new StudentQuestionOptionResponse(
                                        option.getId(),
                                        option.getOptionText(),
                                        option.getDisplayOrder()
                                )
                        )
                        .toList();

        return new StudentQuestionResponse(
                question.getId(),
                question.getQuestionText(),
                question.getQuestionType(),
                question.getDifficulty(),
                question.getMarks(),
                options
        );
    }

    @Override
    public QuestionResponse updateQuestion(
            Long questionId,
            UpdateQuestionRequest request
    ) {

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() ->
                        new QuestionNotFoundException(
                                "Question not found: " + questionId
                        )
                );

        if (!Boolean.TRUE.equals(question.getActive())) {
            throw new InvalidQuestionException(
                    "Inactive questions cannot be updated"
            );
        }

        validateQuestion(request.questionType(), request.options());

        question.setQuestionText(request.questionText().trim());
        question.setQuestionType(request.questionType());
        question.setDifficulty(request.difficulty());
        question.setMarks(request.marks());
        question.setNegativeMarks(request.negativeMarks());
        question.setExplanation(request.explanation());
        question.setLanguage(request.language().trim().toUpperCase());

        question.getOptions().clear();

        request.options().forEach(optionRequest -> {

            QuestionOption option = QuestionOption.builder()
                    .optionText(optionRequest.optionText().trim())
                    .displayOrder(optionRequest.displayOrder())
                    .correct(optionRequest.correct())
                    .build();

            question.addOption(option);
        });

        Question saved = questionRepository.save(question);

        return mapToAdminResponse(saved);
    }

    @Override
    public void deactivateQuestion(Long questionId) {

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() ->
                        new QuestionNotFoundException(
                                "Question not found: " + questionId
                        )
                );

        question.setActive(false);

        questionRepository.save(question);
    }


    private void validateQuestion(
            QuestionType questionType,
            List<QuestionOptionRequest> options
    ) {

        // Handle null options safely
        List<QuestionOptionRequest> safeOptions =
                options == null ? List.of() : options;

        long correctCount = safeOptions.stream()
                .filter(option -> Boolean.TRUE.equals(option.correct()))
                .count();

        switch (questionType) {

            case SINGLE_CHOICE -> {
                if (safeOptions.size() < 2 || correctCount != 1) {
                    throw new InvalidQuestionException(
                            "Single choice requires at least two options and exactly one correct answer"
                    );
                }
            }

            case MULTIPLE_CHOICE -> {
                if (safeOptions.size() < 2 || correctCount < 1) {
                    throw new InvalidQuestionException(
                            "Multiple choice requires at least two options and one or more correct answers"
                    );
                }
            }

            case TRUE_FALSE -> {
                boolean hasTrue = safeOptions.stream()
                        .anyMatch(o -> o.optionText() != null
                                && o.optionText().trim().equalsIgnoreCase("True"));

                boolean hasFalse = safeOptions.stream()
                        .anyMatch(o -> o.optionText() != null
                                && o.optionText().trim().equalsIgnoreCase("False"));

                if (safeOptions.size() != 2 || correctCount != 1
                        || !hasTrue || !hasFalse) {
                    throw new InvalidQuestionException(
                            "True/False requires True and False options and exactly one correct answer"
                    );
                }
            }

            case NUMERICAL, DESCRIPTIVE -> {
                if (!safeOptions.isEmpty()) {
                    throw new InvalidQuestionException(
                            "Options are not allowed for Numerical or Descriptive questions"
                    );
                }
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuestionResponse> searchQuestions(
            Long subjectId,
            QuestionDifficulty difficulty,
            QuestionType questionType,
            Pageable pageable
    ) {

        Page<Question> questions;

        if (subjectId == null) {
            questions = questionRepository.findByActiveTrue(pageable);
        } else if (difficulty != null && questionType != null) {
            questions = questionRepository
                    .findBySubjectIdAndDifficultyAndQuestionTypeAndActiveTrue(
                            subjectId, difficulty, questionType, pageable
                    );
        } else if (difficulty != null) {
            questions = questionRepository
                    .findBySubjectIdAndDifficultyAndActiveTrue(
                            subjectId, difficulty, pageable
                    );
        } else if (questionType != null) {
            questions = questionRepository
                    .findBySubjectIdAndQuestionTypeAndActiveTrue(
                            subjectId, questionType, pageable
                    );
        } else {
            questions = questionRepository
                    .findBySubjectIdAndActiveTrue(subjectId, pageable);
        }

        return questions.map(this::mapToAdminResponse);
    }
}