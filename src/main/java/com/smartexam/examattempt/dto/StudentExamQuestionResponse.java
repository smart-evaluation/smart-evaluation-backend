package com.smartexam.examattempt.dto;

import java.math.BigDecimal;
import java.util.List;

public record StudentExamQuestionResponse(

        Long questionId,
        String questionText,
        String questionType,
        String difficulty,
        BigDecimal marks,

        List<StudentExamQuestionOptionResponse> options,

        List<Long> selectedOptionIds,
        String answerText,
        boolean answered
) {
}