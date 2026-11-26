package com.smartexam.question.repository;

import com.smartexam.question.entity.QuestionOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionOptionRepository
        extends JpaRepository<QuestionOption, Long> {

    List<QuestionOption> findByQuestionIdOrderByDisplayOrderAsc(
            Long questionId
    );

    void deleteByQuestionId(Long questionId);
    List<QuestionOption> findByQuestionId(Long questionId);

}