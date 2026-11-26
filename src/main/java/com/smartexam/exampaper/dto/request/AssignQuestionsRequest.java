package com.smartexam.exampaper.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record AssignQuestionsRequest(

        @NotEmpty
        @Size(max = 500)
        List<@Valid ExamPaperQuestionRequest> questions

) {
}