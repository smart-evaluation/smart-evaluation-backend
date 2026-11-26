package com.smartexam.catalogue.repository;

import com.smartexam.catalogue.entity.ExamLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExamLevelRepository
        extends JpaRepository<ExamLevel, Long> {

    Optional<ExamLevel> findByExamIdAndCode(
            Long examId,
            String code
    );

    boolean existsByExamIdAndCode(
            Long examId,
            String code
    );

    List<ExamLevel> findByExamIdAndActiveTrueOrderByDisplayOrderAsc(
            Long examId
    );

    List<ExamLevel> findByExamIdOrderByDisplayOrderAsc(
            Long examId
    );
}