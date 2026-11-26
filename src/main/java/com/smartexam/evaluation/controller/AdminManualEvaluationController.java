
package com.smartexam.evaluation.controller;

import com.smartexam.common.response.ApiResponse;
import com.smartexam.evaluation.dto.AdminEvaluationAttemptResponse;
import com.smartexam.evaluation.dto.ManualEvaluationRequest;
import com.smartexam.evaluation.dto.PendingEvaluationResponse;
import com.smartexam.evaluation.service.AdminEvaluationDashboardService;
import com.smartexam.evaluation.service.ManualEvaluationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/manual-evaluations")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminManualEvaluationController {

    private final ManualEvaluationService manualEvaluationService;
    private final AdminEvaluationDashboardService adminEvaluationDashboardService;

    @PostMapping("/{attemptId}/questions/{questionId}")
    public ResponseEntity<ApiResponse<Void>> evaluateAnswer(
            @PathVariable Long attemptId,
            @PathVariable Long questionId,
            @Valid @RequestBody ManualEvaluationRequest request,
            Authentication authentication) {

        String adminEmail = authentication.getName();

        manualEvaluationService.evaluateAnswer(
                attemptId,
                questionId,
                adminEmail,
                request
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Answer evaluated successfully",
                        null
                )
        );
    }

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<PendingEvaluationResponse>>>
    getPendingEvaluations() {

        List<PendingEvaluationResponse> result =
                adminEvaluationDashboardService.getPendingEvaluations();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Pending evaluations fetched successfully",
                        result
                )
        );
    }

    @GetMapping("/{attemptId}")
    public ResponseEntity<ApiResponse<AdminEvaluationAttemptResponse>>
    getAttemptDetails(@PathVariable Long attemptId) {

        AdminEvaluationAttemptResponse result =
                adminEvaluationDashboardService.getAttemptDetails(attemptId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Attempt details fetched successfully",
                        result
                )
        );
    }
}
