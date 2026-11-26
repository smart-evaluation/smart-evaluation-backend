package com.smartexam.evaluation.repository;

import com.smartexam.evaluation.entity.ManualEvaluation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ManualEvaluationRepository
        extends JpaRepository<ManualEvaluation, Long> {

    List<ManualEvaluation> findByAttemptId(Long attemptId);

    Optional<ManualEvaluation> findByAttemptIdAndQuestionId(
            Long attemptId,
            Long questionId
    );

    boolean existsByAttemptIdAndQuestionId(
            Long attemptId,
            Long questionId
    );

    List<ManualEvaluation> findByAttemptIdIn(List<Long> attemptIds);
}