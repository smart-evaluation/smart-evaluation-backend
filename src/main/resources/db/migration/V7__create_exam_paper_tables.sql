CREATE TABLE exam_papers (

    id BIGINT NOT NULL AUTO_INCREMENT,

    exam_id BIGINT NOT NULL,
    exam_level_id BIGINT NOT NULL,

    title VARCHAR(200) NOT NULL,
    description TEXT NULL,
    instructions TEXT NULL,

    duration_minutes INT NOT NULL,

    total_marks DECIMAL(10,2) NOT NULL,
    passing_marks DECIMAL(10,2) NOT NULL DEFAULT 0.00,

    negative_marking_enabled BOOLEAN NOT NULL DEFAULT FALSE,

    start_time DATETIME NULL,
    end_time DATETIME NULL,

    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',

    created_by BIGINT NOT NULL,

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    CONSTRAINT fk_exam_paper_exam
        FOREIGN KEY (exam_id)
        REFERENCES exams(id),

    CONSTRAINT fk_exam_paper_level
        FOREIGN KEY (exam_level_id)
        REFERENCES exam_levels(id),

    CONSTRAINT fk_exam_paper_created_by
        FOREIGN KEY (created_by)
        REFERENCES users(id),

    CONSTRAINT chk_exam_duration
        CHECK (duration_minutes > 0),

    CONSTRAINT chk_exam_total_marks
        CHECK (total_marks > 0),

    CONSTRAINT chk_exam_passing_marks
        CHECK (passing_marks >= 0 AND passing_marks <= total_marks),

    INDEX idx_exam_paper_exam (exam_id),
    INDEX idx_exam_paper_level (exam_level_id),
    INDEX idx_exam_paper_status (status),
    INDEX idx_exam_paper_schedule (start_time, end_time)

) ENGINE=InnoDB;


CREATE TABLE exam_paper_questions (

    id BIGINT NOT NULL AUTO_INCREMENT,

    exam_paper_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,

    question_order INT NOT NULL,

    marks DECIMAL(8,2) NOT NULL,
    negative_marks DECIMAL(8,2) NOT NULL DEFAULT 0.00,

    PRIMARY KEY (id),

    CONSTRAINT fk_paper_question_paper
        FOREIGN KEY (exam_paper_id)
        REFERENCES exam_papers(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_paper_question_question
        FOREIGN KEY (question_id)
        REFERENCES questions(id),

    CONSTRAINT uk_paper_question
        UNIQUE (exam_paper_id, question_id),

    CONSTRAINT uk_paper_question_order
        UNIQUE (exam_paper_id, question_order),

    CONSTRAINT chk_paper_question_order
        CHECK (question_order > 0),

    CONSTRAINT chk_paper_question_marks
        CHECK (marks > 0),

    CONSTRAINT chk_paper_question_negative_marks
        CHECK (negative_marks >= 0),

    INDEX idx_paper_question_paper (exam_paper_id),
    INDEX idx_paper_question_question (question_id)

) ENGINE=InnoDB;