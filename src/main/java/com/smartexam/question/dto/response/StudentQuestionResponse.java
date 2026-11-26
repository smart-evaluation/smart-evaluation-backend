package com.smartexam.question.dto.response;

import com.smartexam.question.enums.QuestionDifficulty;
import com.smartexam.question.enums.QuestionType;

import java.math.BigDecimal;
import java.util.List;

public record StudentQuestionResponse(

        Long id,
        String questionText,

        QuestionType questionType,
        QuestionDifficulty difficulty,

        BigDecimal marks,

        List<StudentQuestionOptionResponse> options

) {
}