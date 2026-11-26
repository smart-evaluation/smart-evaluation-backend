package com.smartexam.exampaper.repository;

import com.smartexam.exampaper.entity.ExamPaper;
import com.smartexam.exampaper.enums.ExamPaperStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExamPaperRepository
        extends JpaRepository<ExamPaper, Long> {

    List<ExamPaper> findByExamId(Long examId);

    List<ExamPaper> findByExamLevelId(Long examLevelId);

    List<ExamPaper> findByStatus(ExamPaperStatus status);

    List<ExamPaper> findByExamLevelIdAndStatus(
            Long examLevelId,
            ExamPaperStatus status
    );
    List<ExamPaper> findByStatusOrderByCreatedAtDesc(
            ExamPaperStatus status
    );

}