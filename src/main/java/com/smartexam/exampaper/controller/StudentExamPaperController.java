package com.smartexam.exampaper.controller;

import com.smartexam.common.response.ApiResponse;
import com.smartexam.exampaper.dto.response.StudentExamPaperResponse;
import com.smartexam.exampaper.service.ExamPaperService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/student/exam-papers")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
public class StudentExamPaperController {

    private final ExamPaperService examPaperService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<StudentExamPaperResponse>>>
    getPublishedExams() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Published examinations retrieved successfully",
                        examPaperService.getPublishedExamPapers()
                )
        );
    }
}