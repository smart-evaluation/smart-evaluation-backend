package com.smartexam.evaluation.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PendingEvaluationResponse(
        Long attemptId,
        Long studentId,
        String examPaperTitle,
        LocalDateTime submittedAt,
        BigDecimal totalMarks,
        BigDecimal obtainedMarks,
        String evaluationStatus
) {
}