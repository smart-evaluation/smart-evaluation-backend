package com.smartexam.question.repository;

import com.smartexam.question.entity.Question;
import com.smartexam.question.enums.QuestionDifficulty;
import com.smartexam.question.enums.QuestionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByActiveTrueOrderByCreatedAtDesc();

    List<Question> findBySubjectIdAndActiveTrue(Long subjectId);

    List<Question> findByTopicIdAndActiveTrue(Long topicId);

    List<Question> findByExamLevelIdAndActiveTrue(Long examLevelId);

    List<Question> findByQuestionTypeAndActiveTrue(QuestionType questionType);

    List<Question> findByDifficultyAndActiveTrue(
            QuestionDifficulty difficulty
    );

    Optional<Question> findByIdAndActiveTrue(Long id);

    boolean existsBySubjectIdAndQuestionText(
            Long subjectId,
            String questionText
    );

    Page<Question> findByActiveTrue(Pageable pageable);

    Page<Question> findBySubjectIdAndActiveTrue(
            Long subjectId,
            Pageable pageable
    );

    Page<Question> findBySubjectIdAndDifficultyAndActiveTrue(
            Long subjectId,
            QuestionDifficulty difficulty,
            Pageable pageable
    );

    Page<Question> findBySubjectIdAndQuestionTypeAndActiveTrue(
            Long subjectId,
            QuestionType questionType,
            Pageable pageable
    );

    Page<Question> findBySubjectIdAndDifficultyAndQuestionTypeAndActiveTrue(
            Long subjectId,
            QuestionDifficulty difficulty,
            QuestionType questionType,
            Pageable pageable
    );
}