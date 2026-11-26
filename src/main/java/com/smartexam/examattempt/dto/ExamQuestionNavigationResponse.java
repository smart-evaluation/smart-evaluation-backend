package com.smartexam.examattempt.dto;

import com.smartexam.examattempt.entity.QuestionNavigationStatus;

public record ExamQuestionNavigationResponse(
        Long questionId,
        Integer questionNumber,
        QuestionNavigationStatus status,
        Boolean answered,
        Boolean visited,
        Boolean markedForReview
) {
}