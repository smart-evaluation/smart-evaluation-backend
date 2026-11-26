package com.smartexam.examattempt.dto;

import com.smartexam.examattempt.entity.ExamAttemptStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record StartExamResponse(
        Long attemptId,
        Long examPaperId,
        String examTitle,
        Integer durationMinutes,
        BigDecimal totalMarks,
        LocalDateTime startedAt,
        LocalDateTime expiresAt,
        ExamAttemptStatus status
) {
}