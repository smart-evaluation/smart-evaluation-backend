package com.smartexam.exampaper.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ExamPaperQuestionRequest(

        @NotNull
        @Positive
        Long questionId,

        @NotNull
        @PositiveOrZero
        Integer questionOrder,

        @NotNull
        @DecimalMin("0.01")
        BigDecimal marks,

        @NotNull
        @DecimalMin("0.00")
        BigDecimal negativeMarks

) {
}