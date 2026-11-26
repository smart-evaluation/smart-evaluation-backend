package com.smartexam.evaluation.service;

import com.smartexam.evaluation.dto.AdminEvaluationAttemptResponse;
import com.smartexam.evaluation.dto.PendingEvaluationResponse;

import java.util.List;

public interface AdminEvaluationDashboardService {

    List<PendingEvaluationResponse> getPendingEvaluations();

    AdminEvaluationAttemptResponse getAttemptDetails(Long attemptId);
}