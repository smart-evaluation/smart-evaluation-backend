
-- ============================================
-- 1. EXAM CATEGORIES
-- ============================================

CREATE TABLE exam_categories (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL,
    code VARCHAR(50) NOT NULL,
    description VARCHAR(500),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    display_order INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    CONSTRAINT uk_exam_category_code UNIQUE (code)
);

-- ============================================
-- 2. EXAMS
-- ============================================

CREATE TABLE exams (
    id BIGINT NOT NULL AUTO_INCREMENT,
    category_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    code VARCHAR(75) NOT NULL,
    description VARCHAR(1000),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    display_order INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    CONSTRAINT uk_exam_code UNIQUE (code),
    CONSTRAINT fk_exam_category
        FOREIGN KEY (category_id)
        REFERENCES exam_categories(id)
);

CREATE INDEX idx_exam_category
    ON exams(category_id);

-- ============================================
-- 3. EXAM LEVELS / CLASSES
-- ============================================

CREATE TABLE exam_levels (
    id BIGINT NOT NULL AUTO_INCREMENT,
    exam_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(50) NOT NULL,
    description VARCHAR(500),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    display_order INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    CONSTRAINT uk_exam_level_code
        UNIQUE (exam_id, code),
    CONSTRAINT fk_exam_level_exam
        FOREIGN KEY (exam_id)
        REFERENCES exams(id)
);

CREATE INDEX idx_exam_level_exam
    ON exam_levels(exam_id);

-- ============================================
-- 4. SUBJECTS
-- ============================================

CREATE TABLE subjects (
    id BIGINT NOT NULL AUTO_INCREMENT,
    exam_level_id BIGINT NOT NULL,
    name VARCHAR(150) NOT NULL,
    code VARCHAR(75) NOT NULL,
    description VARCHAR(500),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    display_order INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    CONSTRAINT uk_subject_code
        UNIQUE (exam_level_id, code),
    CONSTRAINT fk_subject_exam_level
        FOREIGN KEY (exam_level_id)
        REFERENCES exam_levels(id)
);

CREATE INDEX idx_subject_exam_level
    ON subjects(exam_level_id);

-- ============================================
-- 5. TOPICS
-- ============================================

CREATE TABLE topics (
    id BIGINT NOT NULL AUTO_INCREMENT,
    subject_id BIGINT NOT NULL,
    name VARCHAR(150) NOT NULL,
    code VARCHAR(75) NOT NULL,
    description VARCHAR(500),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    display_order INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    CONSTRAINT uk_topic_code
        UNIQUE (subject_id, code),
    CONSTRAINT fk_topic_subject
        FOREIGN KEY (subject_id)
        REFERENCES subjects(id)
);

CREATE INDEX idx_topic_subject
    ON topics(subject_id);
