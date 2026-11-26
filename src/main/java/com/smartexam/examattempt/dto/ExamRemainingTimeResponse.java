package com.smartexam.examattempt.dto;

import java.time.LocalDateTime;

public record ExamRemainingTimeResponse(
        Long attemptId,
        long remainingSeconds,
        LocalDateTime expiresAt,
        String status
) {}