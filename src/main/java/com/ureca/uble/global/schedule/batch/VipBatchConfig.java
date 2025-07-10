package com.ureca.uble.global.schedule.batch;

import com.ureca.uble.domain.users.repository.UserRepository;
import com.ureca.uble.entity.User;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class VipBatchConfig {

    private final UserRepository userRepository;
    private final EntityManagerFactory entityManagerFactory;
    private final TaskExecutor batchTaskExecutor;

    @Bean(name = "vipUpdateJob")
    public Job vipUpdateJob(JobRepository jobRepository, Step vipUsageCountResetStep) {
        return new JobBuilder("vipUpdateJob", jobRepository)
            .start(vipUsageCountResetStep)
            .build();
    }

    @Bean
    public Step vipUsageCountResetStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                                       ItemReader<User> vipUserPagingReader, ItemWriter<User> vipUserWriter) {
        return new StepBuilder("vipUsageCountResetStep", jobRepository)
            .<User, User>chunk(100, transactionManager)
            .reader(vipUserPagingReader)
            .writer(vipUserWriter)
            .taskExecutor(batchTaskExecutor)
            .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<User> vipUserPagingReader() {
        JpaPagingItemReader<User> reader = new JpaPagingItemReader<>();
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setQueryString("SELECT u FROM User u WHERE u.isVipAvailable = false ORDER BY u.id");
        reader.setPageSize(100);
        return reader;
    }

    @Bean
    @StepScope
    public ItemWriter<User> vipUserWriter() {
        return users -> {
            for (User user : users) {
                user.updateVipAvailability(true);
            }
            userRepository.saveAll(users);
            log.info("[VIP] {}명의 VIP 혜택 사용 여부를 초기화", users.size());
        };
    }
}
