package com.smartexam.examattempt.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record SaveAnswerRequest(

        @NotNull
        Long questionId,

        List<Long> selectedOptionIds,

        String answerText
) {
}