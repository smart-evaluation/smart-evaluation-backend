package com.smartexam.catalogue.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateExamRequest(

        @NotNull(message = "Category ID is required")
        @Positive(message = "Category ID must be positive")
        Long categoryId,

        @NotBlank(message = "Exam name is required")
        @Size(max = 200)
        String name,

        @NotBlank(message = "Exam code is required")
        @Size(max = 75)
        String code,

        @Size(max = 1000)
        String description,

        Integer displayOrder

) {
}