package com.smartexam.catalogue.service.impl;

import com.smartexam.catalogue.dto.request.CreateExamCategoryRequest;
import com.smartexam.catalogue.dto.response.ExamCategoryResponse;
import com.smartexam.catalogue.entity.ExamCategory;
import com.smartexam.catalogue.exception.*;
import com.smartexam.catalogue.repository.ExamCategoryRepository;
import com.smartexam.catalogue.service.ExamCategoryService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ExamCategoryServiceImpl implements ExamCategoryService {

    private final ExamCategoryRepository repository;

    public ExamCategoryServiceImpl(ExamCategoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public ExamCategoryResponse createCategory(
            CreateExamCategoryRequest request) {

        String code = request.code().trim().toUpperCase();

        if (repository.existsByCode(code)) {
            throw new DuplicateResourceException(
                    "Exam category code already exists: " + code);
        }

        ExamCategory category = ExamCategory.builder()
                .name(request.name().trim())
                .code(code)
                .description(request.description())
                .displayOrder(request.displayOrder() == null
                        ? 0 : request.displayOrder())
                .active(true)
                .build();

        return map(repository.save(category));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamCategoryResponse> getAllCategories() {
        return repository.findAllByOrderByDisplayOrderAsc()
                .stream().map(this::map).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamCategoryResponse> getActiveCategories() {
        return repository.findByActiveTrueOrderByDisplayOrderAsc()
                .stream().map(this::map).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ExamCategoryResponse getCategoryById(Long id) {
        return map(repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Exam category not found: " + id)));
    }

    private ExamCategoryResponse map(ExamCategory category) {
        return new ExamCategoryResponse(
                category.getId(),
                category.getName(),
                category.getCode(),
                category.getDescription(),
                category.getActive(),
                category.getDisplayOrder(),
                category.getCreatedAt()
        );
    }
}