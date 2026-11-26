package com.smartexam.question.entity;

import com.smartexam.catalogue.entity.ExamLevel;
import com.smartexam.catalogue.entity.Subject;
import com.smartexam.catalogue.entity.Topic;
import com.smartexam.question.enums.QuestionDifficulty;
import com.smartexam.question.enums.QuestionType;
import com.smartexam.user.entity.User;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questions", indexes = {
        @Index(name = "idx_question_subject", columnList = "subject_id"),
        @Index(name = "idx_question_topic", columnList = "topic_id"),
        @Index(name = "idx_question_level", columnList = "exam_level_id"),
        @Index(name = "idx_question_active", columnList = "is_active"),
        @Index(name = "idx_question_difficulty", columnList = "difficulty")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exam_level_id", nullable = false)
    private ExamLevel examLevel;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    private String questionText;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false, length = 30)
    private QuestionType questionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", nullable = false, length = 20)
    private QuestionDifficulty difficulty;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal marks;

    @Column(name = "negative_marks", nullable = false, precision = 8, scale = 2)
    private BigDecimal negativeMarks;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String language = "ENGLISH";

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @OneToMany(
            mappedBy = "question",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<QuestionOption> options = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;

        if (active == null) {
            active = true;
        }
        if (marks == null) {
            marks = BigDecimal.ONE;
        }
        if (negativeMarks == null) {
            negativeMarks = BigDecimal.ZERO;
        }
        if (language == null || language.isBlank()) {
            language = "ENGLISH";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void addOption(QuestionOption option) {
        options.add(option);
        option.setQuestion(this);
    }

    public void removeOption(QuestionOption option) {
        options.remove(option);
        option.setQuestion(null);
    }
}