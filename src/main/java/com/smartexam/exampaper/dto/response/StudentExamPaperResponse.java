package com.smartexam.exampaper.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record StudentExamPaperResponse(

        Long id,
        String title,
        String description,
        Integer durationMinutes,
        BigDecimal totalMarks,
        BigDecimal passingMarks,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String status

) {
}