package com.ureca.uble.domain.users.repository;

import com.ureca.uble.entity.Benefit;
import com.ureca.uble.entity.UsageCount;
import com.ureca.uble.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UsageCountRepository extends JpaRepository<UsageCount, Long> {
    Optional<UsageCount> findByUserAndBenefit(User user, Benefit benefit);
    void deleteByUser(User user);

    @Modifying
    @Transactional
    @Query(value = "UPDATE usage_count SET count = 0, isavailable = true WHERE benefit_id = :id", nativeQuery = true)
    int resetCountAndIsAvailableByBenefitId(@Param("id") Long id);
}
