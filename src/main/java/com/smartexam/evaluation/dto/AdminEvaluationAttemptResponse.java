
package com.smartexam.evaluation.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record AdminEvaluationAttemptResponse(
        Long attemptId,
        Long studentId,
        String studentEmail,
        String examPaperTitle,
        String attemptStatus,
        String evaluationStatus,
        BigDecimal totalMarks,
        BigDecimal obtainedMarks,
        Boolean passed,
        LocalDateTime submittedAt,
        List<AdminEvaluationQuestionResponse> questions
) {
}
