package com.smartexam.examattempt.service;

import com.smartexam.examattempt.dto.*;

import java.util.List;

public interface ExamAttemptService {

    StartExamResponse startExam(Long examPaperId, String studentEmail);

    ExamAttemptQuestionsResponse getAttemptQuestions(
            Long attemptId,
            String studentEmail
    );
    SaveAnswerResponse saveAnswer(
            Long attemptId,
            String studentEmail,
            SaveAnswerRequest request
    );

    SubmitExamResponse submitExam(
            Long attemptId,
            String studentEmail
    );

    StudentExamResultResponse getStudentResult(
            Long attemptId,
            String studentEmail
    );

    List<StudentExamAttemptSummaryResponse> getStudentAttempts(
            String studentEmail);

    void autoSubmitExpiredAttempt(Long attemptId);

    List<ExamAnswerReviewResponse> getAnswerReview(
            Long attemptId,
            String studentEmail);

    ExamAttemptResumeResponse resumeExam(
            Long attemptId,
            String studentEmail);

    ExamRemainingTimeResponse getRemainingTime(
            Long attemptId,
            String studentEmail);

    void markQuestionVisited(
            Long attemptId,
            Long questionId,
            String studentEmail
    );

    List<ExamQuestionNavigationResponse> getQuestionNavigation(
            Long attemptId,
            String studentEmail
    );

    void markQuestionForReview(
            Long attemptId,
            Long questionId,
            String studentEmail,
            Boolean markedForReview);

    ExamSubmissionSummaryResponse getSubmissionSummary(
            Long attemptId,
            String studentEmail
    );

}