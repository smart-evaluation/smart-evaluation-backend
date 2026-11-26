package com.smartexam.examattempt.dto;

import com.smartexam.examattempt.entity.QuestionAttemptStatus;

public record ExamAnswerReviewResponse(
        Long questionId,
        Integer questionNumber,
        QuestionAttemptStatus status
) {}