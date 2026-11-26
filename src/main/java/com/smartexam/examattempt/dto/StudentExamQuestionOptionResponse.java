package com.smartexam.examattempt.dto;

public record StudentExamQuestionOptionResponse(
        Long optionId,
        String optionText,
        Integer displayOrder
) {
}