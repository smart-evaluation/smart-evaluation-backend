package com.smartexam.catalogue.service.impl;

import com.smartexam.catalogue.dto.request.CreateSubjectRequest;
import com.smartexam.catalogue.dto.response.SubjectResponse;
import com.smartexam.catalogue.entity.Subject;
import com.smartexam.catalogue.exception.*;
import com.smartexam.catalogue.repository.*;
import com.smartexam.catalogue.service.SubjectService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;
    private final ExamLevelRepository levelRepository;

    public SubjectServiceImpl(
            SubjectRepository subjectRepository,
            ExamLevelRepository levelRepository) {
        this.subjectRepository = subjectRepository;
        this.levelRepository = levelRepository;
    }

    @Override
    public SubjectResponse createSubject(
            CreateSubjectRequest request) {

        var level = levelRepository.findById(request.examLevelId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Exam level not found: " + request.examLevelId()));

        if (!Boolean.TRUE.equals(level.getActive())
                || !Boolean.TRUE.equals(level.getExam().getActive())
                || !Boolean.TRUE.equals(level.getExam()
                .getCategory().getActive())) {
            throw new IllegalArgumentException(
                    "Cannot add subject under an inactive catalogue");
        }

        String code = request.code().trim().toUpperCase();

        if (subjectRepository.existsByExamLevelIdAndCode(
                request.examLevelId(), code)) {
            throw new DuplicateResourceException(
                    "Subject code already exists: " + code);
        }

        Subject subject = Subject.builder()
                .examLevel(level)
                .name(request.name().trim())
                .code(code)
                .description(request.description())
                .displayOrder(request.displayOrder() == null
                        ? 0 : request.displayOrder())
                .active(true)
                .build();

        return map(subjectRepository.save(subject));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubjectResponse> getSubjectsByExamLevel(Long examLevelId) {

        if (!levelRepository.existsById(examLevelId)) {
            throw new ResourceNotFoundException(
                    "Exam level not found: " + examLevelId);
        }

        return subjectRepository
                .findByExamLevelIdAndActiveTrueOrderByDisplayOrderAsc(
                        examLevelId)
                .stream().map(this::map).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SubjectResponse getSubjectById(Long id) {
        return map(subjectRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Subject not found: " + id)));
    }

    private SubjectResponse map(Subject subject) {
        return new SubjectResponse(
                subject.getId(),
                subject.getExamLevel().getId(),
                subject.getExamLevel().getName(),
                subject.getName(),
                subject.getCode(),
                subject.getDescription(),
                subject.getActive(),
                subject.getDisplayOrder(),
                subject.getCreatedAt()
        );
    }
}