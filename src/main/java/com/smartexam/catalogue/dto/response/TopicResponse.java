package com.smartexam.catalogue.dto.response;

import java.time.LocalDateTime;

public record TopicResponse(

        Long id,
        Long subjectId,
        String subjectName,
        String name,
        String code,
        String description,
        Boolean active,
        Integer displayOrder,
        LocalDateTime createdAt

) {
}