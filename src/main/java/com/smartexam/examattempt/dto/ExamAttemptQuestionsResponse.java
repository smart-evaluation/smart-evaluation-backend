package com.smartexam.examattempt.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ExamAttemptQuestionsResponse(
        Long attemptId,
        String examTitle,
        LocalDateTime startedAt,
        LocalDateTime expiresAt,
        List<StudentExamQuestionResponse> questions
) {
}