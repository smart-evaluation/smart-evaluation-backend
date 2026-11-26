package com.smartexam.examattempt.repository;

import com.smartexam.examattempt.entity.ExamAttemptAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExamAttemptAnswerRepository
        extends JpaRepository<ExamAttemptAnswer, Long> {

    List<ExamAttemptAnswer> findByAttemptId(Long attemptId);

    Optional<ExamAttemptAnswer> findByAttemptIdAndQuestionId(
            Long attemptId,
            Long questionId
    );

    long countByAttemptId(Long attemptId);


}