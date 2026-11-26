package com.smartexam.exampaper.dto.response;

import com.smartexam.exampaper.enums.ExamPaperStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ExamPaperResponse(

        Long id,
        Long examId,
        String examName,

        Long examLevelId,
        String examLevelName,

        String title,
        String description,
        String instructions,

        Integer durationMinutes,

        BigDecimal totalMarks,
        BigDecimal passingMarks,

        Boolean negativeMarkingEnabled,

        LocalDateTime startTime,
        LocalDateTime endTime,

        ExamPaperStatus status,

        List<ExamPaperQuestionResponse> questions,

        LocalDateTime createdAt

) {
}