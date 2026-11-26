ALTER TABLE exam_attempt_answers
ADD COLUMN is_marked_for_review BOOLEAN NOT NULL DEFAULT FALSE;