package com.smartexam.catalogue.service;

import com.smartexam.catalogue.dto.request.CreateExamRequest;
import com.smartexam.catalogue.dto.response.ExamResponse;

import java.util.List;

public interface ExamService {

    ExamResponse createExam(CreateExamRequest request);

    List<ExamResponse> getExamsByCategory(Long categoryId);

    ExamResponse getExamById(Long id);
}