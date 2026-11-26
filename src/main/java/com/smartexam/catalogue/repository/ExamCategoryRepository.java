package com.smartexam.catalogue.repository;

import com.smartexam.catalogue.entity.ExamCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExamCategoryRepository
        extends JpaRepository<ExamCategory, Long> {

    Optional<ExamCategory> findByCode(String code);

    boolean existsByCode(String code);

    List<ExamCategory> findByActiveTrueOrderByDisplayOrderAsc();

    List<ExamCategory> findAllByOrderByDisplayOrderAsc();
}