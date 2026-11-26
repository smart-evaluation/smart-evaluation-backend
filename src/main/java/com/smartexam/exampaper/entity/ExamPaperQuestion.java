package com.smartexam.exampaper.entity;

import com.smartexam.question.entity.Question;

import jakarta.persistence.*;

import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "exam_paper_questions",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_paper_question",
                        columnNames = {"exam_paper_id", "question_id"}
                ),
                @UniqueConstraint(
                        name = "uk_paper_question_order",
                        columnNames = {"exam_paper_id", "question_order"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_paper_question_paper",
                        columnList = "exam_paper_id"
                ),
                @Index(
                        name = "idx_paper_question_question",
                        columnList = "question_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamPaperQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exam_paper_id", nullable = false)
    private ExamPaper examPaper;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "question_order", nullable = false)
    private Integer questionOrder;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal marks;

    @Column(name = "negative_marks", nullable = false, precision = 8, scale = 2)
    private BigDecimal negativeMarks;
}