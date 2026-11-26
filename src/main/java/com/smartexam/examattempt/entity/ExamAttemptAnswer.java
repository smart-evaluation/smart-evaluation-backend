package com.smartexam.examattempt.entity;

import com.smartexam.question.entity.Question;
import com.smartexam.question.entity.QuestionOption;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "exam_attempt_answers",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_attempt_question",
                        columnNames = {"attempt_id", "question_id"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_attempt_answer_attempt",
                        columnList = "attempt_id"
                )
        }
)
@Data
public class ExamAttemptAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "attempt_id", nullable = false)
    private ExamAttempt attempt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_option_id")
    private QuestionOption selectedOption;

    @Column(name = "answer_text", columnDefinition = "TEXT")
    private String answerText;

    @Column(name = "saved_at", nullable = false)
    private LocalDateTime savedAt;

    @PrePersist
    @PreUpdate
    protected void updateSavedAt() {
        savedAt = LocalDateTime.now();
    }

    @OneToMany(
            mappedBy = "answer",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ExamAttemptAnswerOption> selectedOptions =
            new ArrayList<>();
    @Column(name = "is_visited", nullable = false)
    private Boolean visited = false;

    @Column(name = "is_marked_for_review", nullable = false)
    private Boolean markedForReview = false;

    public List<ExamAttemptAnswerOption> getSelectedOptions() {
        return selectedOptions;
    }

    public void clearSelectedOptions() {
        selectedOptions.clear();
    }

    public void addSelectedOption(ExamAttemptAnswerOption option) {
        selectedOptions.add(option);
    }
//
//    public Long getId() {
//        return id;
//    }
//
//    public ExamAttempt getAttempt() {
//        return attempt;
//    }
//
//    public void setAttempt(ExamAttempt attempt) {
//        this.attempt = attempt;
//    }
//
//    public Question getQuestion() {
//        return question;
//    }
//
//    public void setQuestion(Question question) {
//        this.question = question;
//    }
//
//    public QuestionOption getSelectedOption() {
//        return selectedOption;
//    }
//
//    public void setSelectedOption(QuestionOption selectedOption) {
//        this.selectedOption = selectedOption;
//    }
//
//    public String getAnswerText() {
//        return answerText;
//    }
//
//    public void setAnswerText(String answerText) {
//        this.answerText = answerText;
//    }
//
//    public LocalDateTime getSavedAt() {
//        return savedAt;
//    }
}