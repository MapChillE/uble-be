package com.ureca.uble.global.schedule.batch;

import com.ureca.uble.entity.enums.Period;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UsageBatchRunner {

    private final JobLauncher jobLauncher;

    private final Job normalUpdateJob;
    private final Job vipUpdateJob;
    private final Job localUpdateJob;

    public void runNormalBatch(Period period) {
        try {
            log.info("runNormalBatchAsyncRunner 실행");
            jobLauncher.run(
                normalUpdateJob,
                new JobParametersBuilder()
                    .addString("timestamp", String.valueOf(System.currentTimeMillis()))
                    .addString("period", period.toString())
                    .toJobParameters()
            );
        } catch (Exception e) {
            log.error("기본 혜택 사용 내역 배치 실행 실패: {}", e.getMessage(), e);
        }
    }

    public void runVipBatch() {
        try {
            log.info("runVipBatchAsyncRunner 실행");
            jobLauncher.run(
                vipUpdateJob,
                new JobParametersBuilder()
                    .addString("timestamp", String.valueOf(System.currentTimeMillis()))
                    .toJobParameters()
            );
        } catch (Exception e) {
            log.error("vip 사용 내역 배치 실행 실패: {}", e.getMessage(), e);
        }
    }

    public void runLocalBatch() {
        try {
            log.info("runLocalBatchAsyncRunner 실행");
            jobLauncher.run(
                localUpdateJob,
                new JobParametersBuilder()
                    .addString("timestamp", String.valueOf(System.currentTimeMillis()))
                    .toJobParameters()
            );
        } catch (Exception e) {
            log.error("우리 동네 사용 내역 배치 실행 실패: {}", e.getMessage(), e);
        }
    }
}
