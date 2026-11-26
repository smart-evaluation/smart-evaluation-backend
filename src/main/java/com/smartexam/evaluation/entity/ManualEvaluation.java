package com.smartexam.evaluation.entity;

import com.smartexam.examattempt.entity.ExamAttempt;
import com.smartexam.question.entity.Question;
import com.smartexam.user.entity.User;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "manual_evaluations",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_attempt_question",
                        columnNames = {"attempt_id", "question_id"}
                )
        }
)
@Getter
@Setter
public class ManualEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id", nullable = false)
    private ExamAttempt attempt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluated_by", nullable = false)
    private User evaluatedBy;

    @Column(name = "awarded_marks", nullable = false)
    private BigDecimal awardedMarks;

    @Column(name = "maximum_marks", nullable = false)
    private BigDecimal maximumMarks;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    @Column(name = "evaluated_at", nullable = false)
    private LocalDateTime evaluatedAt;
}