package com.smartexam.catalogue.repository;

import com.smartexam.catalogue.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExamRepository
        extends JpaRepository<Exam, Long> {

    Optional<Exam> findByCode(String code);

    boolean existsByCode(String code);

    List<Exam> findByCategoryIdAndActiveTrueOrderByDisplayOrderAsc(
            Long categoryId
    );

    List<Exam> findByCategoryIdOrderByDisplayOrderAsc(
            Long categoryId
    );

    List<Exam> findByActiveTrueOrderByDisplayOrderAsc();

    boolean existsByCategoryIdAndCode(
            Long categoryId,
            String code
    );
}