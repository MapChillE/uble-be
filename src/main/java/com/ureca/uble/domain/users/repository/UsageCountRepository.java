package com.ureca.uble.domain.users.repository;

import com.ureca.uble.entity.Benefit;
import com.ureca.uble.entity.UsageCount;
import com.ureca.uble.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UsageCountRepository extends JpaRepository<UsageCount, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT uc FROM UsageCount uc WHERE uc.user = :user AND uc.benefit = :benefit")
    Optional<UsageCount> findByUserAndBenefitWithPessimisticLock(@Param("user") User user, @Param("benefit") Benefit benefit);

    Optional<UsageCount> findByUserAndBenefit(User user, Benefit benefit);

    void deleteByUser(User user);

    @Modifying
    @Transactional
    @Query(value = "UPDATE usage_count SET count = 0, isavailable = true WHERE benefit_id = :id", nativeQuery = true)
    int resetCountAndIsAvailableByBenefitId(@Param("id") Long id);
}
