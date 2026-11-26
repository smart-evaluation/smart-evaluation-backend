package com.smartexam.examattempt.repository;

import com.smartexam.examattempt.entity.EvaluationStatus;
import com.smartexam.examattempt.entity.ExamAttempt;
import com.smartexam.examattempt.entity.ExamAttemptStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ExamAttemptRepository
        extends JpaRepository<ExamAttempt, Long> {

    List<ExamAttempt> findByStudentIdOrderByStartedAtDesc(Long studentId);

    Optional<ExamAttempt> findByIdAndStudentId(
            Long attemptId,
            Long studentId
    );

    Optional<ExamAttempt> findByStudentIdAndExamPaperIdAndStatus(
            Long studentId,
            Long examPaperId,
            ExamAttemptStatus status
    );

    boolean existsByStudentIdAndExamPaperIdAndStatus(
            Long studentId,
            Long examPaperId,
            ExamAttemptStatus status
    );

    long countByExamPaperId(Long examPaperId);
    List<ExamAttempt> findByStatusAndExpiresAtLessThanEqual(
            ExamAttemptStatus status,
            LocalDateTime currentTime
    );

    @Query(value = """
        SELECT *
        FROM exam_attempts
        WHERE id = :attemptId
          AND student_id = :studentId
        FOR UPDATE
        """, nativeQuery = true)
    Optional<ExamAttempt> findByIdAndStudentIdForUpdate(
            @Param("attemptId") Long attemptId,
            @Param("studentId") Long studentId
    );

    @Query(value = """
        SELECT *
        FROM exam_attempts
        WHERE id = :attemptId
        FOR UPDATE
        """, nativeQuery = true)
    Optional<ExamAttempt> findByIdForUpdate(
            @Param("attemptId") Long attemptId
    );

    @Query("""
    SELECT a
    FROM ExamAttempt a
    WHERE a.evaluationStatus = :evaluationStatus
      AND a.status IN :statuses
    ORDER BY a.submittedAt ASC
    """)
    List<ExamAttempt> findPendingManualEvaluations(
            @Param("evaluationStatus") EvaluationStatus evaluationStatus,
            @Param("statuses") Collection<ExamAttemptStatus> statuses
    );
}