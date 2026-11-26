package com.smartexam.catalogue.service;

import com.smartexam.catalogue.dto.request.CreateExamCategoryRequest;
import com.smartexam.catalogue.dto.response.ExamCategoryResponse;

import java.util.List;

public interface ExamCategoryService {

    ExamCategoryResponse createCategory(
            CreateExamCategoryRequest request);

    List<ExamCategoryResponse> getAllCategories();

    List<ExamCategoryResponse> getActiveCategories();

    ExamCategoryResponse getCategoryById(Long id);
}