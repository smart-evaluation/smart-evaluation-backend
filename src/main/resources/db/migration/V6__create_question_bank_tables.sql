CREATE TABLE questions (
    id BIGINT NOT NULL AUTO_INCREMENT,

    exam_level_id BIGINT NOT NULL,
    subject_id BIGINT NOT NULL,
    topic_id BIGINT NULL,

    question_text TEXT NOT NULL,

    question_type VARCHAR(30) NOT NULL,
    difficulty VARCHAR(20) NOT NULL,

    marks DECIMAL(8,2) NOT NULL DEFAULT 1.00,
    negative_marks DECIMAL(8,2) NOT NULL DEFAULT 0.00,

    explanation TEXT NULL,
    language VARCHAR(20) NOT NULL DEFAULT 'ENGLISH',

    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    created_by BIGINT NOT NULL,

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    CONSTRAINT fk_question_exam_level
        FOREIGN KEY (exam_level_id)
        REFERENCES exam_levels(id),

    CONSTRAINT fk_question_subject
        FOREIGN KEY (subject_id)
        REFERENCES subjects(id),

    CONSTRAINT fk_question_topic
        FOREIGN KEY (topic_id)
        REFERENCES topics(id),

    CONSTRAINT fk_question_created_by
        FOREIGN KEY (created_by)
        REFERENCES users(id),

    CONSTRAINT chk_question_marks
        CHECK (marks > 0),

    CONSTRAINT chk_question_negative_marks
        CHECK (negative_marks >= 0),

    INDEX idx_question_subject (subject_id),
    INDEX idx_question_topic (topic_id),
    INDEX idx_question_level (exam_level_id),
    INDEX idx_question_active (is_active),
    INDEX idx_question_difficulty (difficulty)

) ENGINE=InnoDB;

CREATE TABLE question_options (
    id BIGINT NOT NULL AUTO_INCREMENT,

    question_id BIGINT NOT NULL,

    option_text TEXT NOT NULL,

    display_order INT NOT NULL DEFAULT 0,

    is_correct BOOLEAN NOT NULL DEFAULT FALSE,

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    CONSTRAINT fk_question_option_question
        FOREIGN KEY (question_id)
        REFERENCES questions(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_option_display_order
        CHECK (display_order >= 0),

    INDEX idx_question_option_question (question_id)

) ENGINE=InnoDB;