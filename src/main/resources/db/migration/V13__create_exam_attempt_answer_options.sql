CREATE TABLE exam_attempt_answer_options (
    id BIGINT NOT NULL AUTO_INCREMENT,
    answer_id BIGINT NOT NULL,
    option_id BIGINT NOT NULL,

    PRIMARY KEY (id),

    CONSTRAINT fk_answer_option_answer
        FOREIGN KEY (answer_id)
        REFERENCES exam_attempt_answers(id),

    CONSTRAINT fk_answer_option_option
        FOREIGN KEY (option_id)
        REFERENCES question_options(id),

    CONSTRAINT uk_answer_option
        UNIQUE (answer_id, option_id)
);