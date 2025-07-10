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
public class LocalBatchConfig {

    private final UserRepository userRepository;
    private final EntityManagerFactory entityManagerFactory;
    private final TaskExecutor batchTaskExecutor;

    @Bean(name = "localUpdateJob")
    public Job localUpdateJob(JobRepository jobRepository, Step localUsageCountResetStep) {
        return new JobBuilder("localUpdateJob", jobRepository)
            .start(localUsageCountResetStep)
            .build();
    }

    @Bean
    public Step localUsageCountResetStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                                         ItemReader<User> localUserPagingReader, ItemWriter<User> localUserWriter) {
        return new StepBuilder("localUsageCountResetStep", jobRepository)
            .<User, User>chunk(100, transactionManager)
            .reader(localUserPagingReader)
            .writer(localUserWriter)
            .taskExecutor(batchTaskExecutor)
            .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<User> localUserPagingReader() {
        JpaPagingItemReader<User> reader = new JpaPagingItemReader<>();
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setQueryString("SELECT u FROM User u WHERE u.isLocalAvailable = false ORDER BY u.id");
        reader.setPageSize(100);
        return reader;
    }

    @Bean
    @StepScope
    public ItemWriter<User> localUserWriter() {
        return users -> {
            for (User user : users) {
                user.updateLocalAvailability(true);
            }
            userRepository.saveAll(users);
            log.info("[LOCAL] {}명의 LOCAL 혜택 사용 여부를 초기화", users.size());
        };
    }
}
