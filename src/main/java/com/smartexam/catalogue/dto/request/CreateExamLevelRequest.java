package com.smartexam.catalogue.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateExamLevelRequest(

        @NotNull(message = "Exam ID is required")
        @Positive
        Long examId,

        @NotBlank(message = "Level name is required")
        @Size(max = 100)
        String name,

        @NotBlank(message = "Level code is required")
        @Size(max = 50)
        String code,

        @Size(max = 500)
        String description,

        Integer displayOrder

) {
}