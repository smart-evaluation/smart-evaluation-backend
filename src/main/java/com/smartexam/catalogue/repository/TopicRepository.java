package com.smartexam.catalogue.repository;

import com.smartexam.catalogue.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TopicRepository
        extends JpaRepository<Topic, Long> {

    Optional<Topic> findBySubjectIdAndCode(
            Long subjectId,
            String code
    );

    boolean existsBySubjectIdAndCode(
            Long subjectId,
            String code
    );

    List<Topic> findBySubjectIdAndActiveTrueOrderByDisplayOrderAsc(
            Long subjectId
    );

    List<Topic> findBySubjectIdOrderByDisplayOrderAsc(
            Long subjectId
    );
}