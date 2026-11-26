package com.smartexam.examattempt.entity;

import com.smartexam.exampaper.entity.ExamPaper;
import com.smartexam.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "exam_attempts",
        indexes = {
                @Index(name = "idx_attempt_student", columnList = "student_id"),
                @Index(name = "idx_attempt_exam_paper", columnList = "exam_paper_id"),
                @Index(name = "idx_attempt_status", columnList = "status")
        }
)
@Data
public class ExamAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exam_paper_id", nullable = false)
    private ExamPaper examPaper;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ExamAttemptStatus status = ExamAttemptStatus.IN_PROGRESS;

    @Column(name = "total_marks", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalMarks = BigDecimal.ZERO;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }
    @Enumerated(EnumType.STRING)
    @Column(name = "evaluation_status", nullable = false, length = 30)
    private EvaluationStatus evaluationStatus = EvaluationStatus.PENDING;

    @Column(name = "obtained_marks", nullable = false,
            precision = 10, scale = 2)
    private BigDecimal obtainedMarks = BigDecimal.ZERO;

    @Column(name = "passed")
    private Boolean passed;
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

//    public Long getId() {
//        return id;
//    }
//
//    public ExamPaper getExamPaper() {
//        return examPaper;
//    }
//
//    public void setExamPaper(ExamPaper examPaper) {
//        this.examPaper = examPaper;
//    }
//
//    public User getStudent() {
//        return student;
//    }
//
//    public void setStudent(User student) {
//        this.student = student;
//    }
//
//    public LocalDateTime getStartedAt() {
//        return startedAt;
//    }
//
//    public void setStartedAt(LocalDateTime startedAt) {
//        this.startedAt = startedAt;
//    }
//
//    public LocalDateTime getExpiresAt() {
//        return expiresAt;
//    }
//
//    public void setExpiresAt(LocalDateTime expiresAt) {
//        this.expiresAt = expiresAt;
//    }
//
//    public LocalDateTime getSubmittedAt() {
//        return submittedAt;
//    }
//
//    public void setSubmittedAt(LocalDateTime submittedAt) {
//        this.submittedAt = submittedAt;
//    }
//
//    public ExamAttemptStatus getStatus() {
//        return status;
//    }
//
//    public void setStatus(ExamAttemptStatus status) {
//        this.status = status;
//    }
//
//    public BigDecimal getTotalMarks() {
//        return totalMarks;
//    }
//
//    public void setTotalMarks(BigDecimal totalMarks) {
//        this.totalMarks = totalMarks;
//    }
//
//    public LocalDateTime getCreatedAt() {
//        return createdAt;
//    }
//
//    public LocalDateTime getUpdatedAt() {
//        return updatedAt;
//    }
}