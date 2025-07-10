package com.ureca.uble.global.schedule.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisLockUtil {

    private final RedissonClient redissonClient;

    private static final int MAX_RETRY_COUNT = 3;
    private static final int RETRY_DELAY_MS = 1000;

    public <T> void executeWithLockWithRetry(String lockKey, long waitTime, long leaseTime, Supplier<T> task) {
        RLock lock = redissonClient.getLock(lockKey);
        boolean isLocked = false;

        try {
            isLocked = lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
            if (!isLocked) {
                log.info("락 획득 실패: 다른 서버에서 실행 중이므로 종료");
                return;
            }

            for (int atmp = 1; atmp <= MAX_RETRY_COUNT; atmp++) {
                try {
                    task.get();
                    return;
                } catch (Exception e) {
                    log.warn("재시도 : ({}/{})", atmp, MAX_RETRY_COUNT, e);
                    if (atmp == MAX_RETRY_COUNT) {
                        log.error("최대 재시도 횟수 초과, 종료합니다.");
                        throw e;
                    }
                    Thread.sleep(RETRY_DELAY_MS);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            // 항상 락 해제
            if (isLocked && lock.isHeldByCurrentThread()) {
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                lock.unlock();
            }
        }
    }
}
