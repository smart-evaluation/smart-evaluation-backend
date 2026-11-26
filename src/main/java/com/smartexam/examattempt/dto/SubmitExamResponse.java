package com.smartexam.examattempt.dto;

import com.smartexam.examattempt.entity.ExamAttemptStatus;
import com.smartexam.examattempt.entity.EvaluationStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SubmitExamResponse(
        Long attemptId,
        BigDecimal totalMarks,
        BigDecimal obtainedMarks,
        BigDecimal passingMarks,
        Boolean passed,
        EvaluationStatus evaluationStatus,
        ExamAttemptStatus status,
        LocalDateTime submittedAt
) {
}