package com.smartexam.exampaper.controller;

import com.smartexam.common.response.ApiResponse;
import com.smartexam.exampaper.dto.request.*;
import com.smartexam.exampaper.dto.response.ExamPaperResponse;
import com.smartexam.exampaper.service.ExamPaperService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/exam-papers")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminExamPaperController {

    private final ExamPaperService examPaperService;

    @PostMapping
    public ResponseEntity<ApiResponse<ExamPaperResponse>> createExamPaper(
            @Valid @RequestBody CreateExamPaperRequest request,
            Authentication authentication
    ) {

        ExamPaperResponse response = examPaperService.createExamPaper(
                request,
                authentication.getName()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Exam paper created successfully",
                        response
                ));
    }

    @PostMapping("/{examPaperId}/questions")
    public ResponseEntity<ApiResponse<ExamPaperResponse>> assignQuestions(
            @PathVariable Long examPaperId,
            @Valid @RequestBody AssignQuestionsRequest request
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Questions assigned successfully",
                        examPaperService.assignQuestions(
                                examPaperId,
                                request
                        )
                )
        );
    }

    @GetMapping("/{examPaperId}")
    public ResponseEntity<ApiResponse<ExamPaperResponse>> getExamPaper(
            @PathVariable Long examPaperId
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Exam paper retrieved successfully",
                        examPaperService.getExamPaperById(examPaperId)
                )
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ExamPaperResponse>>> getAllExamPapers() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Exam papers retrieved successfully",
                        examPaperService.getAllExamPapers()
                )
        );
    }

    @PatchMapping("/{examPaperId}/publish")
    public ResponseEntity<ApiResponse<Void>> publishExamPaper(
            @PathVariable Long examPaperId
    ) {

        examPaperService.publishExamPaper(examPaperId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Exam paper published successfully",
                        null
                )
        );
    }

    @PatchMapping("/{examPaperId}/close")
    public ResponseEntity<ApiResponse<Void>> closeExamPaper(
            @PathVariable Long examPaperId
    ) {

        examPaperService.closeExamPaper(examPaperId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Exam paper closed successfully",
                        null
                )
        );
    }
}