package com.ureca.uble.global.schedule;

import com.ureca.uble.entity.enums.Period;
import com.ureca.uble.global.schedule.batch.UsageBatchRunner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class UsageScheduler {

    private final UsageBatchRunner batchRunner;

    // 하루에 한 번 (매일 자정)
    @Scheduled(cron = "0 0 0 * * *")
    public void updateDaily() {
        log.info("[NORMAL] DAILY 초기화 작업 시작: {}", LocalDateTime.now());
        batchRunner.runNormalBatch(Period.DAILY);
    }

    // 일주에 한 번 (매주 월요일 자정)
    @Scheduled(cron = "0 0 0 * * 1")
    public void updateWeekly() {
        log.info("[NORMAL] WEEKLY 초기화 작업 시작: {}", LocalDateTime.now());
        batchRunner.runNormalBatch(Period.WEEKLY);
    }

    // 한달에 한 번 (매월 1일 자정)
    @Scheduled(cron = "0 0 0 1 * *")
    public void updateMonthly() {
        log.info("[NORMAL] MONTHLY 초기화 작업 시작: {}", LocalDateTime.now());
        batchRunner.runNormalBatch(Period.MONTHLY);
    }

    // 일년에 한 번 (매년 1월 1일 자정)
    @Scheduled(cron = "0 0 0 1 1 *")
    public void updateYearly() {
        log.info("[NORMAL] YEARLY 초기화 작업 시작: {}", LocalDateTime.now());
        batchRunner.runNormalBatch(Period.YEARLY);
    }

    // 한달에 한 번 (매월 1일 자정)
    @Scheduled(cron = "0 0 0 1 * *")
    public void updateVip() {
        log.info("[VIP] MONTHLY 초기화 작업 시작: {}", LocalDateTime.now());
        batchRunner.runVipBatch();
    }

    // 하루에 한 번 (매일 자정)
    @Scheduled(cron = "0 0 0 * * *")
    public void updateLocal() {
        log.info("[LOCAL] DAILY 초기화 작업 시작: {}", LocalDateTime.now());
        batchRunner.runLocalBatch();
    }
}
