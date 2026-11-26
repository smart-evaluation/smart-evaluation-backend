package com.smartexam.examattempt.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.smartexam.examattempt.entity.ExamAttemptStatus;

public record StudentExamAttemptSummaryResponse(
        Long attemptId,
        Long examPaperId,
        String examTitle,
        ExamAttemptStatus status,
        BigDecimal totalMarks,
        BigDecimal obtainedMarks,
        Boolean passed,
        LocalDateTime startedAt,
        LocalDateTime submittedAt
) {}