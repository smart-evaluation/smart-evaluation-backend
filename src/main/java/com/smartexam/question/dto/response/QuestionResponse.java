package com.smartexam.question.dto.response;

import com.smartexam.question.enums.QuestionDifficulty;
import com.smartexam.question.enums.QuestionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record QuestionResponse(

        Long id,
        Long examLevelId,
        Long subjectId,
        Long topicId,

        String questionText,

        QuestionType questionType,
        QuestionDifficulty difficulty,

        BigDecimal marks,
        BigDecimal negativeMarks,

        String explanation,
        String language,

        Boolean active,

        Long createdById,

        List<QuestionOptionResponse> options,

        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
}