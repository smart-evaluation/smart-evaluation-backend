package com.smartexam.evaluation.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ManualEvaluationRequest(

        @NotNull
        @DecimalMin("0.00")
        BigDecimal awardedMarks,

        @Size(max = 2000)
        String feedback

) {}