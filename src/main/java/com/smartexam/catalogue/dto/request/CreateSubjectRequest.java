package com.smartexam.catalogue.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateSubjectRequest(

        @NotNull(message = "Exam level ID is required")
        @Positive
        Long examLevelId,

        @NotBlank(message = "Subject name is required")
        @Size(max = 150)
        String name,

        @NotBlank(message = "Subject code is required")
        @Size(max = 75)
        String code,

        @Size(max = 500)
        String description,

        Integer displayOrder

) {
}