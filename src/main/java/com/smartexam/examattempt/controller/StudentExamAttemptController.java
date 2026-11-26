package com.smartexam.examattempt.controller;

import com.smartexam.common.response.ApiResponse;
import com.smartexam.examattempt.dto.*;
import com.smartexam.examattempt.service.ExamAttemptService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/student")
@PreAuthorize("hasRole('STUDENT')")
public class StudentExamAttemptController {

    private final ExamAttemptService examAttemptService;

    public StudentExamAttemptController(
            ExamAttemptService examAttemptService) {
        this.examAttemptService = examAttemptService;
    }

    @PostMapping("/exam-papers/{examPaperId}/start")
    public ResponseEntity<ApiResponse<StartExamResponse>> startExam(
            @PathVariable Long examPaperId,
            Authentication authentication) {

        StartExamResponse response =
                examAttemptService.startExam(
                        examPaperId,
                        authentication.getName()
                );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Examination started successfully",
                        response
                ));
    }

    @GetMapping("/exam-attempts/{attemptId}/questions")
    public ResponseEntity<ApiResponse<ExamAttemptQuestionsResponse>>
    getAttemptQuestions(
            @PathVariable Long attemptId,
            Authentication authentication) {

        ExamAttemptQuestionsResponse response =
                examAttemptService.getAttemptQuestions(
                        attemptId,
                        authentication.getName()
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Exam questions retrieved successfully",
                        response
                )
        );
    }

    @PutMapping("/exam-attempts/{attemptId}/answers")
    public ResponseEntity<ApiResponse<SaveAnswerResponse>> saveAnswer(
            @PathVariable Long attemptId,
            @Valid @RequestBody SaveAnswerRequest request,
            Authentication authentication) {

        SaveAnswerResponse response =
                examAttemptService.saveAnswer(
                        attemptId,
                        authentication.getName(),
                        request
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Answer saved successfully",
                        response
                )
        );
    }

    @PostMapping("/exam-attempts/{attemptId}/submit")
    public ResponseEntity<ApiResponse<SubmitExamResponse>> submitExam(
            @PathVariable Long attemptId,
            Authentication authentication) {

        SubmitExamResponse response =
                examAttemptService.submitExam(
                        attemptId,
                        authentication.getName()
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Examination submitted successfully",
                        response
                )
        );
    }

    @GetMapping("/exam-attempts/{attemptId}/result")
    public ResponseEntity<ApiResponse<StudentExamResultResponse>>
    getStudentResult(
            @PathVariable Long attemptId,
            Authentication authentication) {

        StudentExamResultResponse response =
                examAttemptService.getStudentResult(
                        attemptId,
                        authentication.getName()
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Exam result retrieved successfully",
                        response
                )
        );
    }

    @GetMapping("/exam-attempts")
    public ResponseEntity<ApiResponse<List<StudentExamAttemptSummaryResponse>>>
    getStudentAttempts(Authentication authentication) {

        List<StudentExamAttemptSummaryResponse> response =
                examAttemptService.getStudentAttempts(
                        authentication.getName());

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Exam attempts retrieved successfully",
                        response
                )
        );
    }
    @GetMapping("/exam-attempts/{attemptId}/review")
    public ResponseEntity<ApiResponse<List<ExamAnswerReviewResponse>>>
    getAnswerReview(
            @PathVariable Long attemptId,
            Authentication authentication) {

        List<ExamAnswerReviewResponse> response =
                examAttemptService.getAnswerReview(
                        attemptId,
                        authentication.getName());

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Exam answer review retrieved successfully",
                        response
                )
        );
    }

    @GetMapping("/exam-attempts/{attemptId}/resume")
    public ResponseEntity<ApiResponse<ExamAttemptResumeResponse>> resumeExam(
            @PathVariable Long attemptId,
            Authentication authentication) {

        ExamAttemptResumeResponse response =
                examAttemptService.resumeExam(
                        attemptId,
                        authentication.getName()
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Exam attempt resumed successfully",
                        response
                )
        );
    }

    @GetMapping("/exam-attempts/{attemptId}/time")
    public ResponseEntity<ApiResponse<ExamRemainingTimeResponse>>
    getRemainingTime(
            @PathVariable Long attemptId,
            Authentication authentication) {

        ExamRemainingTimeResponse response =
                examAttemptService.getRemainingTime(
                        attemptId,
                        authentication.getName()
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Remaining exam time retrieved successfully",
                        response
                )
        );
    }

    @PatchMapping("/exam-attempts/{attemptId}/questions/{questionId}/visited")
    public ResponseEntity<ApiResponse<Void>> markQuestionVisited(
            @PathVariable Long attemptId,
            @PathVariable Long questionId,
            Authentication authentication) {

        examAttemptService.markQuestionVisited(
                attemptId,
                questionId,
                authentication.getName()
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Question marked as visited",
                        null
                )
        );
    }

    @GetMapping("/exam-attempts/{attemptId}/navigation")
    public ResponseEntity<ApiResponse<List<ExamQuestionNavigationResponse>>>
    getQuestionNavigation(
            @PathVariable Long attemptId,
            Authentication authentication) {

        List<ExamQuestionNavigationResponse> response =
                examAttemptService.getQuestionNavigation(
                        attemptId,
                        authentication.getName()
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Exam question navigation retrieved successfully",
                        response
                )
        );
    }

    @PatchMapping("/exam-attempts/{attemptId}/questions/{questionId}/review")
    public ResponseEntity<ApiResponse<Void>> markQuestionForReview(
            @PathVariable Long attemptId,
            @PathVariable Long questionId,
            @Valid @RequestBody MarkQuestionReviewRequest request,
            Authentication authentication) {

        examAttemptService.markQuestionForReview(
                attemptId,
                questionId,
                authentication.getName(),
                request.markedForReview()
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Question review status updated successfully",
                        null
                )
        );
    }

    @GetMapping("/exam-attempts/{attemptId}/submission-summary")
    public ResponseEntity<ApiResponse<ExamSubmissionSummaryResponse>>
    getSubmissionSummary(
            @PathVariable Long attemptId,
            Authentication authentication) {

        ExamSubmissionSummaryResponse response =
                examAttemptService.getSubmissionSummary(
                        attemptId,
                        authentication.getName()
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Exam submission summary retrieved successfully",
                        response
                )
        );
    }
}