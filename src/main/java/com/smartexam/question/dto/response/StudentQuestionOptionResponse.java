package com.smartexam.question.dto.response;

public record StudentQuestionOptionResponse(

        Long id,
        String optionText,
        Integer displayOrder

) {
}