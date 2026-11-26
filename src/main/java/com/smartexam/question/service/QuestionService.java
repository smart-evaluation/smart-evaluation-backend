package com.smartexam.question.service;

import com.smartexam.question.dto.request.CreateQuestionRequest;
import com.smartexam.question.dto.request.UpdateQuestionRequest;
import com.smartexam.question.dto.response.QuestionResponse;
import com.smartexam.question.dto.response.StudentQuestionResponse;
import com.smartexam.question.enums.QuestionDifficulty;
import com.smartexam.question.enums.QuestionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QuestionService {

    QuestionResponse createQuestion(
            CreateQuestionRequest request,
            String adminEmail
    );

    QuestionResponse getQuestionById(Long questionId);

    List<QuestionResponse> getAllQuestions();

    List<QuestionResponse> getQuestionsBySubject(Long subjectId);

    List<StudentQuestionResponse> getStudentQuestionsBySubject(
            Long subjectId
    );
    QuestionResponse updateQuestion(
            Long questionId,
            UpdateQuestionRequest request
    );

    void deactivateQuestion(Long questionId);

    Page<QuestionResponse> searchQuestions(
            Long subjectId,
            QuestionDifficulty difficulty,
            QuestionType questionType,
            Pageable pageable
    );

}