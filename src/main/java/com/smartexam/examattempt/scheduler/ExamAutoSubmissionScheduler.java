package com.smartexam.examattempt.scheduler;

import com.smartexam.examattempt.entity.ExamAttempt;
import com.smartexam.examattempt.entity.ExamAttemptStatus;
import com.smartexam.examattempt.repository.ExamAttemptRepository;
import com.smartexam.examattempt.service.ExamAttemptService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExamAutoSubmissionScheduler {

    private final ExamAttemptRepository examAttemptRepository;
    private final ExamAttemptService examAttemptService;

    @Scheduled(fixedDelay = 15000)
    public void autoSubmitExpiredExams() {

        LocalDateTime now = LocalDateTime.now();

        List<ExamAttempt> expiredAttempts =
                examAttemptRepository
                        .findByStatusAndExpiresAtLessThanEqual(
                                ExamAttemptStatus.IN_PROGRESS,
                                now
                        );

        for (ExamAttempt attempt : expiredAttempts) {
            try {
                examAttemptService.autoSubmitExpiredAttempt(
                        attempt.getId()
                );

                log.info(
                        "Automatically submitted expired exam attempt: {}",
                        attempt.getId()
                );

            } catch (Exception exception) {
                log.error(
                        "Failed to auto-submit exam attempt: {}",
                        attempt.getId(),
                        exception
                );
            }
        }
    }
}