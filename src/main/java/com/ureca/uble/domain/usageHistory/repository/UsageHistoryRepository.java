package com.ureca.uble.domain.usageHistory.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ureca.uble.entity.UsageHistory;

public interface UsageHistoryRepository extends JpaRepository<UsageHistory, Long>, CustomUsageHistoryRepository{

}
