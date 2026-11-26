package com.smartexam.question.dto.request;

import com.smartexam.question.enums.QuestionDifficulty;
import com.smartexam.question.enums.QuestionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public record CreateQuestionRequest(

        @NotNull(message = "Exam level ID is required")
        @Positive(message = "Exam level ID must be positive")
        Long examLevelId,

        @NotNull(message = "Subject ID is required")
        @Positive(message = "Subject ID must be positive")
        Long subjectId,

        @Positive(message = "Topic ID must be positive")
        Long topicId,

        @NotBlank(message = "Question text is required")
        @Size(max = 10000, message = "Question text is too long")
        String questionText,

        @NotNull(message = "Question type is required")
        QuestionType questionType,

        @NotNull(message = "Difficulty is required")
        QuestionDifficulty difficulty,

        @NotNull(message = "Marks are required")
        @DecimalMin(value = "0.01", message = "Marks must be greater than zero")
        @Digits(integer = 6, fraction = 2)
        BigDecimal marks,

        @NotNull(message = "Negative marks are required")
        @DecimalMin(value = "0.00", message = "Negative marks cannot be negative")
        @Digits(integer = 6, fraction = 2)
        BigDecimal negativeMarks,

        @Size(max = 10000, message = "Explanation is too long")
        String explanation,

        @NotBlank(message = "Language is required")
        @Size(max = 20)
        String language,

        @Size(max = 10, message = "Maximum 10 options are allowed")
        List<@Valid QuestionOptionRequest> options

) {
}