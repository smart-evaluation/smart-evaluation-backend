package com.smartexam.examattempt.dto;

import java.time.LocalDateTime;

public record ExamAttemptResumeResponse(
        Long attemptId,
        Long examPaperId,
        String examTitle,
        String status,
        LocalDateTime startedAt,
        LocalDateTime expiresAt,
        long remainingSeconds,
        String message
) {}