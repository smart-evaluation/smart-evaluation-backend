package com.smartexam.catalogue.dto.response;

import java.time.LocalDateTime;

public record ExamCategoryResponse(

        Long id,
        String name,
        String code,
        String description,
        Boolean active,
        Integer displayOrder,
        LocalDateTime createdAt

) {
}