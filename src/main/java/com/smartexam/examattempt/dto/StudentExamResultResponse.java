package com.smartexam.examattempt.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record StudentExamResultResponse(
        Long attemptId,
        Long examPaperId,
        String examTitle,
        BigDecimal totalMarks,
        BigDecimal obtainedMarks,
        BigDecimal passingMarks,
        BigDecimal percentage,
        Boolean passed,
        String evaluationStatus,
        String attemptStatus,
        LocalDateTime startedAt,
        LocalDateTime submittedAt
) {
}