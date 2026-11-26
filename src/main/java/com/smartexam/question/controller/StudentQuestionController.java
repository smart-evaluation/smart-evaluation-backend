package com.smartexam.question.controller;

import com.smartexam.common.response.ApiResponse;
import com.smartexam.question.dto.response.StudentQuestionResponse;
import com.smartexam.question.service.QuestionService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/questions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
public class StudentQuestionController {

    private final QuestionService questionService;

    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<ApiResponse<List<StudentQuestionResponse>>> getQuestionsBySubject(
            @PathVariable Long subjectId
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Questions retrieved successfully",
                        questionService.getStudentQuestionsBySubject(subjectId)
                )
        );
    }
}