package com.smartexam.catalogue.dto.response;

import java.time.LocalDateTime;

public record SubjectResponse(

        Long id,
        Long examLevelId,
        String examLevelName,
        String name,
        String code,
        String description,
        Boolean active,
        Integer displayOrder,
        LocalDateTime createdAt

) {
}