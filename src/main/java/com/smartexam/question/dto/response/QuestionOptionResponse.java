package com.smartexam.question.dto.response;

public record QuestionOptionResponse(

        Long id,
        String optionText,
        Integer displayOrder,
        Boolean correct

) {
}