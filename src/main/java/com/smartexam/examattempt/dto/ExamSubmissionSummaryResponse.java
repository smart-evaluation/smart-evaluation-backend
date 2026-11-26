package com.smartexam.examattempt.dto;

public record ExamSubmissionSummaryResponse(
        Long attemptId,
        Integer totalQuestions,
        Integer answeredQuestions,
        Integer unansweredQuestions,
        Integer markedForReviewQuestions,
        Integer notVisitedQuestions,
        Boolean canSubmit
) {}