package com.smartexam.examattempt.dto;

import java.time.LocalDateTime;

public record AnswerAutosaveResponse(
        Long attemptId,
        Long questionId,
        boolean saved,
        LocalDateTime savedAt,
        String message
) {}