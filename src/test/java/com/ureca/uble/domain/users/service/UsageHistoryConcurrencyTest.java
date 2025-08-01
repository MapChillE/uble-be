package com.ureca.uble.domain.users.service;

import com.ureca.uble.containers.AbstractPostgresContainerTest;
import com.ureca.uble.containers.ElasticsearchTestContainer;
import com.ureca.uble.domain.brand.fixture.BenefitFixtures;
import com.ureca.uble.domain.brand.fixture.BrandFixtures;
import com.ureca.uble.domain.brand.repository.BenefitRepository;
import com.ureca.uble.domain.brand.repository.BrandRepository;
import com.ureca.uble.domain.category.fixture.CategoryFixtures;
import com.ureca.uble.domain.category.repository.CategoryRepository;
import com.ureca.uble.domain.store.fixture.StoreFixtures;
import com.ureca.uble.domain.store.repository.StoreRepository;
import com.ureca.uble.domain.users.dto.request.CreateUsageHistoryReq;
import com.ureca.uble.domain.users.fixture.UserFixtures;
import com.ureca.uble.domain.users.repository.UsageHistoryDocumentRepository;
import com.ureca.uble.domain.users.repository.UserRepository;
import com.ureca.uble.entity.*;
import com.ureca.uble.entity.enums.BenefitType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@ActiveProfiles("test")
@Import({ElasticsearchTestContainer.class})
@SpringBootTest
public class UsageHistoryConcurrencyTest extends AbstractPostgresContainerTest {

    @Autowired
    private UsageHistoryService usageHistoryService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BenefitRepository benefitRepository;

    @Autowired
    private UsageHistoryDocumentRepository usageHistoryDocumentRepository;

    @Test
    @DisplayName("사용자는 정해진 횟수 만큼의 혜택만을 사용할 수 있다.")
    void checkUsageConcurrency() throws InterruptedException {
        // given
        User savedUser = userRepository.saveAndFlush(UserFixtures.createUser());
        Category savedCategory = categoryRepository.saveAndFlush(CategoryFixtures.createTmpStore());
        Brand savedBrand = brandRepository.saveAndFlush(BrandFixtures.createTmpBrand(savedCategory));
        Benefit savedBenefit = benefitRepository.saveAndFlush(BenefitFixtures.createTmpBenefit(savedBrand, 30));
        Store savedStore = storeRepository.saveAndFlush(StoreFixtures.createTmpStore(savedBrand));
        usageHistoryDocumentRepository.deleteAll();

        CreateUsageHistoryReq req = new CreateUsageHistoryReq(BenefitType.NORMAL);

        // when
        long start = System.currentTimeMillis();

        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    usageHistoryService.createUsageHistory(savedUser.getId(), savedStore.getId(), req);
                } catch (Exception ignored) {
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then
        int finalCount = (int) usageHistoryDocumentRepository.count();
        assertThat(finalCount).isEqualTo(savedBenefit.getNumber());
        long end = System.currentTimeMillis();
        System.out.println("전체 병렬 처리 시간: " + (end - start) + " ms");
    }
}
