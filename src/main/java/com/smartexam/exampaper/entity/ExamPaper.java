package com.smartexam.exampaper.entity;

import com.smartexam.catalogue.entity.Exam;
import com.smartexam.catalogue.entity.ExamLevel;
import com.smartexam.exampaper.enums.ExamPaperStatus;
import com.smartexam.user.entity.User;

import jakarta.persistence.*;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "exam_papers", indexes = {
        @Index(name = "idx_exam_paper_exam", columnList = "exam_id"),
        @Index(name = "idx_exam_paper_level", columnList = "exam_level_id"),
        @Index(name = "idx_exam_paper_status", columnList = "status"),
        @Index(name = "idx_exam_paper_schedule", columnList = "start_time, end_time")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamPaper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exam_level_id", nullable = false)
    private ExamLevel examLevel;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String instructions;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(name = "total_marks", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalMarks;

    @Column(name = "passing_marks", nullable = false, precision = 10, scale = 2)
    private BigDecimal passingMarks;

    @Column(name = "negative_marking_enabled", nullable = false)
    @Builder.Default
    private Boolean negativeMarkingEnabled = false;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ExamPaperStatus status = ExamPaperStatus.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @OneToMany(
            mappedBy = "examPaper",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<ExamPaperQuestion> questions = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;


    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;

        if (status == null) {
            status = ExamPaperStatus.DRAFT;
        }

        if (negativeMarkingEnabled == null) {
            negativeMarkingEnabled = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void addQuestion(ExamPaperQuestion question) {
        questions.add(question);
        question.setExamPaper(this);
    }

    public void removeQuestion(ExamPaperQuestion question) {
        questions.remove(question);
        question.setExamPaper(null);
    }
}