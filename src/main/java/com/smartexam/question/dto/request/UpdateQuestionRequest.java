package com.smartexam.question.dto.request;

import com.smartexam.question.enums.QuestionDifficulty;
import com.smartexam.question.enums.QuestionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public record UpdateQuestionRequest(

        @NotBlank
        @Size(max = 10000)
        String questionText,

        @NotNull
        QuestionType questionType,

        @NotNull
        QuestionDifficulty difficulty,

        @NotNull
        @DecimalMin("0.01")
        @Digits(integer = 6, fraction = 2)
        BigDecimal marks,

        @NotNull
        @DecimalMin("0.00")
        @Digits(integer = 6, fraction = 2)
        BigDecimal negativeMarks,

        @Size(max = 10000)
        String explanation,

        @NotBlank
        @Size(max = 20)
        String language,

        @NotEmpty
        @Size(max = 10)
        List<@Valid QuestionOptionRequest> options

) {
}