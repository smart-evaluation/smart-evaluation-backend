package com.smartexam.evaluation.service;

import com.smartexam.evaluation.dto.ManualEvaluationRequest;

public interface ManualEvaluationService {

    void evaluateAnswer(
            Long attemptId,
            Long questionId,
            String adminEmail,
            ManualEvaluationRequest request
    );
}