CREATE TABLE exam_attempts (
    id BIGINT NOT NULL AUTO_INCREMENT,
    exam_paper_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,

    started_at DATETIME(6) NOT NULL,
    expires_at DATETIME(6) NOT NULL,
    submitted_at DATETIME(6) NULL,

    status VARCHAR(30) NOT NULL DEFAULT 'IN_PROGRESS',

    total_marks DECIMAL(10,2) NOT NULL DEFAULT 0.00,

    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
        ON UPDATE CURRENT_TIMESTAMP(6),

    PRIMARY KEY (id),

    CONSTRAINT fk_attempt_exam_paper
        FOREIGN KEY (exam_paper_id)
        REFERENCES exam_papers(id),

    CONSTRAINT fk_attempt_student
        FOREIGN KEY (student_id)
        REFERENCES users(id),

    INDEX idx_attempt_student (student_id),
    INDEX idx_attempt_exam_paper (exam_paper_id),
    INDEX idx_attempt_status (status)
);

CREATE TABLE exam_attempt_answers (
    id BIGINT NOT NULL AUTO_INCREMENT,
    attempt_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    selected_option_id BIGINT NULL,
    answer_text TEXT NULL,

    saved_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
        ON UPDATE CURRENT_TIMESTAMP(6),

    PRIMARY KEY (id),

    CONSTRAINT fk_attempt_answer_attempt
        FOREIGN KEY (attempt_id)
        REFERENCES exam_attempts(id),

    CONSTRAINT fk_attempt_answer_question
        FOREIGN KEY (question_id)
        REFERENCES questions(id),

    CONSTRAINT fk_attempt_answer_option
        FOREIGN KEY (selected_option_id)
        REFERENCES question_options(id),

    CONSTRAINT uk_attempt_question
        UNIQUE (attempt_id, question_id),

    INDEX idx_attempt_answer_attempt (attempt_id)
);