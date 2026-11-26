package com.smartexam.exampaper.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateExamPaperRequest(

        @NotNull
        @Positive
        Long examId,

        @NotNull
        @Positive
        Long examLevelId,

        @NotBlank
        @Size(max = 200)
        String title,

        String description,

        String instructions,

        @NotNull
        @Min(1)
        @Max(600)
        Integer durationMinutes,

        @NotNull
        @DecimalMin("0.00")
        BigDecimal passingMarks,

        @NotNull
        Boolean negativeMarkingEnabled,

        LocalDateTime startTime,

        LocalDateTime endTime

) {
}