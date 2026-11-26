package com.smartexam.catalogue.repository;

import com.smartexam.catalogue.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubjectRepository
        extends JpaRepository<Subject, Long> {

    Optional<Subject> findByExamLevelIdAndCode(
            Long examLevelId,
            String code
    );

    boolean existsByExamLevelIdAndCode(
            Long examLevelId,
            String code
    );

    List<Subject> findByExamLevelIdAndActiveTrueOrderByDisplayOrderAsc(
            Long examLevelId
    );

    List<Subject> findByExamLevelIdOrderByDisplayOrderAsc(
            Long examLevelId
    );
}