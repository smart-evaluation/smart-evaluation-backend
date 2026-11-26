package com.smartexam.question.controller;

import com.smartexam.common.response.ApiResponse;
import com.smartexam.question.dto.request.CreateQuestionRequest;
import com.smartexam.question.dto.request.UpdateQuestionRequest;
import com.smartexam.question.dto.response.QuestionResponse;
import com.smartexam.question.enums.QuestionDifficulty;
import com.smartexam.question.enums.QuestionType;
import com.smartexam.question.service.QuestionService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/questions")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminQuestionController {

    private final QuestionService questionService;

    @PostMapping
    public ResponseEntity<ApiResponse<QuestionResponse>> createQuestion(
            @Valid @RequestBody CreateQuestionRequest request,
            Authentication authentication
    ) {

        QuestionResponse response = questionService.createQuestion(
                request,
                authentication.getName()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Question created successfully",
                        response
                ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<QuestionResponse>>> getAllQuestions() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Questions retrieved successfully",
                        questionService.getAllQuestions()
                )
        );
    }

    @GetMapping("/{questionId}")
    public ResponseEntity<ApiResponse<QuestionResponse>> getQuestionById(
            @PathVariable Long questionId
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Question retrieved successfully",
                        questionService.getQuestionById(questionId)
                )
        );
    }

    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<ApiResponse<List<QuestionResponse>>> getQuestionsBySubject(
            @PathVariable Long subjectId
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Subject questions retrieved successfully",
                        questionService.getQuestionsBySubject(subjectId)
                )
        );
    }

    @PutMapping("/{questionId}")
    public ResponseEntity<ApiResponse<QuestionResponse>> updateQuestion(
            @PathVariable Long questionId,
            @Valid @RequestBody UpdateQuestionRequest request
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Question updated successfully",
                        questionService.updateQuestion(questionId, request)
                )
        );
    }

    @PatchMapping("/{questionId}/deactivate")
    public ResponseEntity<ApiResponse<Void>> deactivateQuestion(
            @PathVariable Long questionId
    ) {

        questionService.deactivateQuestion(questionId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Question deactivated successfully",
                        null
                )
        );
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<QuestionResponse>>> searchQuestions(

            @RequestParam(required = false) Long subjectId,

            @RequestParam(required = false) QuestionDifficulty difficulty,

            @RequestParam(required = false) QuestionType questionType,

            @RequestParam(defaultValue = "0") int page,

            @RequestParam(defaultValue = "10") int size

    ) {

        if (page < 0 || size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "Invalid pagination parameters"
            );
        }

        PageRequest pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Questions retrieved successfully",
                        questionService.searchQuestions(
                                subjectId,
                                difficulty,
                                questionType,
                                pageable
                        )
                )
        );
    }
}