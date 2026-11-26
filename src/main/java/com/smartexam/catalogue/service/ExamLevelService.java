package com.smartexam.catalogue.service;

import com.smartexam.catalogue.dto.request.CreateExamLevelRequest;
import com.smartexam.catalogue.dto.response.ExamLevelResponse;

import java.util.List;

public interface ExamLevelService {

    ExamLevelResponse createExamLevel(
            CreateExamLevelRequest request);

    List<ExamLevelResponse> getLevelsByExam(Long examId);

    ExamLevelResponse getLevelById(Long id);
}