package com.ureca.uble.global.schedule.batch;

import com.ureca.uble.domain.users.repository.UsageCountRepository;
import com.ureca.uble.entity.Benefit;
import com.ureca.uble.entity.enums.Period;
import com.ureca.uble.entity.enums.Rank;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class NormalBatchConfig {

    private final UsageCountRepository usageCountRepository;
    private final EntityManagerFactory entityManagerFactory;
    private final TaskExecutor batchTaskExecutor;

    @Bean(name = "normalUpdateJob")
    public Job normalUpdateJob(JobRepository jobRepository, Step normalUsageCountResetStep) {
        return new JobBuilder("normalUpdateJob", jobRepository)
            .start(normalUsageCountResetStep)
            .build();
    }

    @Bean
    public Step normalUsageCountResetStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                                          ItemReader<Benefit> normalBenefitPagingReader, ItemWriter<Benefit> usageCountResetWriter) {
        return new StepBuilder("normalUsageCountResetStep", jobRepository)
            .<Benefit, Benefit>chunk(100, transactionManager)
            .reader(normalBenefitPagingReader)
            .writer(usageCountResetWriter)
            .taskExecutor(batchTaskExecutor)
            .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<Benefit> normalBenefitPagingReader(
        @Value("#{jobParameters['period']}") String periodStr) {
        JpaPagingItemReader<Benefit> reader = new JpaPagingItemReader<>();
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setQueryString(
            "SELECT b FROM Benefit b " +
                "JOIN b.brand br " +
                "WHERE b.period = :period " +
                "AND (br.rankType = 'NORMAL' OR (br.rankType = 'VIP_NORMAL' AND b.rank != 'VIP')) "
        );
        reader.setParameterValues(Map.of(
            "period", Period.valueOf(periodStr)
        ));
        reader.setPageSize(100);
        return reader;
    }

    @Bean
    @StepScope
    public ItemWriter<Benefit> usageCountResetWriter() {
        return benefits -> {
            long cnt = 0L;
            for (Benefit benefit : benefits) {
                cnt += usageCountRepository.resetCountAndIsAvailableByBenefitId(benefit.getId());
            }
            log.info("usageCount cnt 0으로 초기화 ({}건)", cnt);
        };
    }
}
