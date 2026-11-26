package com.smartexam.exampaper.service;

import com.smartexam.exampaper.dto.request.CreateExamPaperRequest;
import com.smartexam.exampaper.dto.request.AssignQuestionsRequest;
import com.smartexam.exampaper.dto.response.ExamPaperResponse;
import com.smartexam.exampaper.dto.response.StudentExamPaperResponse;

import java.util.List;

public interface ExamPaperService {

    ExamPaperResponse createExamPaper(
            CreateExamPaperRequest request,
            String adminEmail
    );

    ExamPaperResponse assignQuestions(
            Long examPaperId,
            AssignQuestionsRequest request
    );

    ExamPaperResponse getExamPaperById(Long examPaperId);

    List<ExamPaperResponse> getAllExamPapers();

    void publishExamPaper(Long examPaperId);

    void closeExamPaper(Long examPaperId);
    List<StudentExamPaperResponse> getPublishedExamPapers();

}