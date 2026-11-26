package com.smartexam.catalogue.service.impl;

import com.smartexam.catalogue.dto.request.CreateExamRequest;
import com.smartexam.catalogue.dto.response.ExamResponse;
import com.smartexam.catalogue.entity.Exam;
import com.smartexam.catalogue.exception.*;
import com.smartexam.catalogue.repository.*;
import com.smartexam.catalogue.service.ExamService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;
    private final ExamCategoryRepository categoryRepository;

    public ExamServiceImpl(
            ExamRepository examRepository,
            ExamCategoryRepository categoryRepository) {
        this.examRepository = examRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public ExamResponse createExam(CreateExamRequest request) {

        var category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Exam category not found"));

        if (!Boolean.TRUE.equals(category.getActive())) {
            throw new IllegalArgumentException(
                    "Cannot create exam under inactive category");
        }

        String code = request.code().trim().toUpperCase();

        if (examRepository.existsByCode(code)) {
            throw new DuplicateResourceException(
                    "Exam code already exists: " + code);
        }

        Exam exam = Exam.builder()
                .category(category)
                .name(request.name().trim())
                .code(code)
                .description(request.description())
                .displayOrder(request.displayOrder() == null
                        ? 0 : request.displayOrder())
                .active(true)
                .build();

        return map(examRepository.save(exam));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamResponse> getExamsByCategory(Long categoryId) {

        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException(
                    "Exam category not found: " + categoryId);
        }

        return examRepository
                .findByCategoryIdAndActiveTrueOrderByDisplayOrderAsc(
                        categoryId)
                .stream().map(this::map).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ExamResponse getExamById(Long id) {
        return map(examRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Exam not found: " + id)));
    }

    private ExamResponse map(Exam exam) {
        return new ExamResponse(
                exam.getId(),
                exam.getCategory().getId(),
                exam.getCategory().getName(),
                exam.getName(),
                exam.getCode(),
                exam.getDescription(),
                exam.getActive(),
                exam.getDisplayOrder(),
                exam.getCreatedAt()
        );
    }
}