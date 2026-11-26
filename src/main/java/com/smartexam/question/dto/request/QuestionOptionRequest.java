package com.smartexam.question.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record QuestionOptionRequest(

        @NotBlank(message = "Option text is required")
        @Size(max = 2000, message = "Option text cannot exceed 2000 characters")
        String optionText,

        @NotNull(message = "Display order is required")
        @PositiveOrZero(message = "Display order cannot be negative")
        Integer displayOrder,

        @NotNull(message = "Correct flag is required")
        Boolean correct

) {
}