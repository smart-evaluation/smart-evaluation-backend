
package com.smartexam.evaluation.dto;

import java.math.BigDecimal;

public record AdminEvaluationQuestionResponse(
        Long questionId,
        Integer questionOrder,
        String questionText,
        String questionType,
        String studentAnswer,
        BigDecimal maximumMarks,
        BigDecimal negativeMarks,
        BigDecimal awardedMarks,
        String feedback,
        boolean evaluated
) {
}
