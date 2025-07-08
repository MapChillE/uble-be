package com.ureca.uble.domain.users.repository;

import com.ureca.uble.entity.UsageHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsageHistoryRepository extends JpaRepository<UsageHistory, Long>, CustomUsageHistoryRepository{
}
