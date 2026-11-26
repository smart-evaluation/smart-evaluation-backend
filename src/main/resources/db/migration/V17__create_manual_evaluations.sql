CREATE TABLE manual_evaluations (
    id BIGINT NOT NULL AUTO_INCREMENT,

    attempt_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    evaluated_by BIGINT NOT NULL,

    awarded_marks DECIMAL(10,2) NOT NULL,
    maximum_marks DECIMAL(10,2) NOT NULL,

    feedback TEXT,

    evaluated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    CONSTRAINT fk_manual_evaluation_attempt
        FOREIGN KEY (attempt_id)
        REFERENCES exam_attempts(id),

    CONSTRAINT fk_manual_evaluation_question
        FOREIGN KEY (question_id)
        REFERENCES questions(id),

    CONSTRAINT fk_manual_evaluation_admin
        FOREIGN KEY (evaluated_by)
        REFERENCES users(id),

    CONSTRAINT uk_attempt_question
        UNIQUE (attempt_id, question_id)
);