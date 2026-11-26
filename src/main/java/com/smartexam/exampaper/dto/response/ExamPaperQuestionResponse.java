package com.smartexam.exampaper.dto.response;

import java.math.BigDecimal;

public record ExamPaperQuestionResponse(

        Long id,
        Long questionId,
        String questionText,
        Integer questionOrder,
        BigDecimal marks,
        BigDecimal negativeMarks

) {
}