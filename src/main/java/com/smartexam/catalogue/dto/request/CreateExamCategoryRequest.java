package com.smartexam.catalogue.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateExamCategoryRequest(

        @NotBlank(message = "Category name is required")
        @Size(max = 150)
        String name,

        @NotBlank(message = "Category code is required")
        @Size(max = 50)
        String code,

        @Size(max = 500)
        String description,

        Integer displayOrder

) {
}