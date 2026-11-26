package com.smartexam.catalogue.service.impl;

import com.smartexam.catalogue.dto.request.CreateExamLevelRequest;
import com.smartexam.catalogue.dto.response.ExamLevelResponse;
import com.smartexam.catalogue.entity.ExamLevel;
import com.smartexam.catalogue.exception.*;
import com.smartexam.catalogue.repository.*;
import com.smartexam.catalogue.service.ExamLevelService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ExamLevelServiceImpl implements ExamLevelService {

    private final ExamLevelRepository levelRepository;
    private final ExamRepository examRepository;

    public ExamLevelServiceImpl(
            ExamLevelRepository levelRepository,
            ExamRepository examRepository) {
        this.levelRepository = levelRepository;
        this.examRepository = examRepository;
    }

    @Override
    public ExamLevelResponse createExamLevel(
            CreateExamLevelRequest request) {

        var exam = examRepository.findById(request.examId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Exam not found: " + request.examId()));

        if (!Boolean.TRUE.equals(exam.getActive())) {
            throw new IllegalArgumentException(
                    "Cannot add level to inactive exam");
        }

        String code = request.code().trim().toUpperCase();

        if (levelRepository.existsByExamIdAndCode(
                request.examId(), code)) {
            throw new DuplicateResourceException(
                    "Exam level code already exists: " + code);
        }

        ExamLevel level = ExamLevel.builder()
                .exam(exam)
                .name(request.name().trim())
                .code(code)
                .description(request.description())
                .displayOrder(request.displayOrder() == null
                        ? 0 : request.displayOrder())
                .active(true)
                .build();

        return map(levelRepository.save(level));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamLevelResponse> getLevelsByExam(Long examId) {

        if (!examRepository.existsById(examId)) {
            throw new ResourceNotFoundException(
                    "Exam not found: " + examId);
        }

        return levelRepository
                .findByExamIdAndActiveTrueOrderByDisplayOrderAsc(examId)
                .stream().map(this::map).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ExamLevelResponse getLevelById(Long id) {
        return map(levelRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Exam level not found: " + id)));
    }

    private ExamLevelResponse map(ExamLevel level) {
        return new ExamLevelResponse(
                level.getId(),
                level.getExam().getId(),
                level.getExam().getName(),
                level.getName(),
                level.getCode(),
                level.getDescription(),
                level.getActive(),
                level.getDisplayOrder(),
                level.getCreatedAt()
        );
    }
}