package com.smartexam.exampaper.repository;

import com.smartexam.exampaper.entity.ExamPaperQuestion;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExamPaperQuestionRepository
        extends JpaRepository<ExamPaperQuestion, Long> {

    List<ExamPaperQuestion> findByExamPaperIdOrderByQuestionOrderAsc(
            Long examPaperId
    );

    boolean existsByExamPaperIdAndQuestionId(
            Long examPaperId,
            Long questionId
    );

    long countByExamPaperId(Long examPaperId);
    List<ExamPaperQuestion> findByExamPaperId(Long examPaperId);

    Optional<ExamPaperQuestion> findByExamPaperIdAndQuestionId(
            Long examPaperId,
            Long questionId
    );

}