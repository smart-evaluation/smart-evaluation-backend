package com.smartexam.catalogue.dto.response;

import java.time.LocalDateTime;

public record ExamLevelResponse(

        Long id,
        Long examId,
        String examName,
        String name,
        String code,
        String description,
        Boolean active,
        Integer displayOrder,
        LocalDateTime createdAt

) {
}