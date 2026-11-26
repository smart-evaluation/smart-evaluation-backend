package com.smartexam.examattempt.dto;

import jakarta.validation.constraints.NotNull;

public record MarkQuestionReviewRequest(
        @NotNull Boolean markedForReview
) {}