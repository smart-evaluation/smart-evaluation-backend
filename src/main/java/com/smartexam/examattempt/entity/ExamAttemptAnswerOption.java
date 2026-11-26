package com.smartexam.examattempt.entity;

import com.smartexam.question.entity.QuestionOption;
import jakarta.persistence.*;

@Entity
@Table(
        name = "exam_attempt_answer_options",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_answer_option",
                columnNames = {"answer_id", "option_id"}
        )
)
public class ExamAttemptAnswerOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "answer_id", nullable = false)
    private ExamAttemptAnswer answer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "option_id", nullable = false)
    private QuestionOption option;

    protected ExamAttemptAnswerOption() {
    }

    public ExamAttemptAnswerOption(
            ExamAttemptAnswer answer,
            QuestionOption option) {
        this.answer = answer;
        this.option = option;
    }

    public Long getId() {
        return id;
    }

    public ExamAttemptAnswer getAnswer() {
        return answer;
    }

    public QuestionOption getOption() {
        return option;
    }
}