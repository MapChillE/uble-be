package com.ureca.uble.global.schedule;

import com.ureca.uble.entity.enums.Period;
import com.ureca.uble.global.schedule.batch.UsageBatchRunner;
import com.ureca.uble.global.schedule.util.RedisLockUtil;
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
    private final RedisLockUtil redisLockUtil;

    // 하루에 한 번 (매일 자정)
//    @Scheduled(cron = "0 0 0 * * *")
    @Scheduled(cron = "0 0/5 * * * *") // 매 5분마다 실행
    public void updateDaily() {
        redisLockUtil.executeWithLockWithRetry("normal-daily-lock", 1, 300, () -> {
            log.info("[NORMAL] DAILY 초기화 작업 시작: {}", LocalDateTime.now());
            batchRunner.runNormalBatch(Period.DAILY);
            return null;
        });
    }

    // 일주에 한 번 (매주 월요일 자정)
//    @Scheduled(cron = "0 0 0 * * 1")
    @Scheduled(cron = "0 1/5 * * * *") // 매 5분마다 1분부터 실행 (1,6,11,...)
    public void updateWeekly() {
        redisLockUtil.executeWithLockWithRetry("normal-weekly-lock", 1, 300, () -> {
            log.info("[NORMAL] WEEKLY 초기화 작업 시작: {}", LocalDateTime.now());
            batchRunner.runNormalBatch(Period.WEEKLY);
            return null;
        });
    }

    // 한달에 한 번 (매월 1일 자정)
//    @Scheduled(cron = "0 0 0 1 * *")
    @Scheduled(cron = "0 2/5 * * * *") // 매 5분마다 2분부터 실행 (2,7,12,...)
    public void updateMonthly() {
        redisLockUtil.executeWithLockWithRetry("normal-monthly-lock", 1, 300, () -> {
            log.info("[NORMAL] MONTHLY 초기화 작업 시작: {}", LocalDateTime.now());
            batchRunner.runNormalBatch(Period.MONTHLY);
            return null;
        });
    }

    // 일년에 한 번 (매년 1월 1일 자정)
//    @Scheduled(cron = "0 0 0 1 1 *")
    @Scheduled(cron = "0 3/5 * * * *") // 매 5분마다 3분부터 실행 (3,8,13,...)
    public void updateYearly() {
        redisLockUtil.executeWithLockWithRetry("normal-yearly-lock", 1, 300, () -> {
            log.info("[NORMAL] YEARLY 초기화 작업 시작: {}", LocalDateTime.now());
            batchRunner.runNormalBatch(Period.YEARLY);
            return null;
        });
    }

    // 한달에 한 번 (매월 1일 자정)
//    @Scheduled(cron = "0 0 0 1 * *")
    @Scheduled(cron = "0 4/5 * * * *") // 매 5분마다 4분부터 실행 (4,9,14,...)
    public void updateVip() {
        redisLockUtil.executeWithLockWithRetry("vip-monthly-lock", 1, 300, () -> {
            log.info("[VIP] MONTHLY 초기화 작업 시작: {}", LocalDateTime.now());
            batchRunner.runVipBatch();
            return null;
        });
    }

    // 하루에 한 번 (매일 자정)
//    @Scheduled(cron = "0 0 0 * * *")
    @Scheduled(cron = "0 5/5 * * * *") // 매 5분마다 5분부터 실행 (5,10,15,...)
    public void updateLocal() {
        redisLockUtil.executeWithLockWithRetry("local-daily-lock", 1, 300, () -> {
            log.info("[LOCAL] DAILY 초기화 작업 시작: {}", LocalDateTime.now());
            batchRunner.runLocalBatch();
            return null;
        });
    }
}
