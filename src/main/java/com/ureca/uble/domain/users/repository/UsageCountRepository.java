package com.ureca.uble.domain.users.repository;

import com.ureca.uble.entity.Benefit;
import com.ureca.uble.entity.UsageCount;
import com.ureca.uble.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsageCountRepository extends JpaRepository<UsageCount, Long> {
    Optional<UsageCount> findByUserAndBenefit(User user, Benefit benefit);
    void deleteByUser(User user);
}
