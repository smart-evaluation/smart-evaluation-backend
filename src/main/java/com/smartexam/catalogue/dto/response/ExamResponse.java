package com.smartexam.catalogue.dto.response;

import java.time.LocalDateTime;

public record ExamResponse(

        Long id,
        Long categoryId,
        String categoryName,
        String name,
        String code,
        String description,
        Boolean active,
        Integer displayOrder,
        LocalDateTime createdAt

) {
}