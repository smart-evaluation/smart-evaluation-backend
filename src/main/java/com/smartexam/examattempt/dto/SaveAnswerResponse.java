package com.smartexam.examattempt.dto;

import java.time.LocalDateTime;

public record SaveAnswerResponse(
        Long attemptId,
        Long questionId,
        String message,
        LocalDateTime savedAt
) {
}