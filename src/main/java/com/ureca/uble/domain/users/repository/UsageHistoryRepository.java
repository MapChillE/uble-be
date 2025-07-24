package com.ureca.uble.domain.users.repository;

import com.ureca.uble.entity.UsageHistory;
import com.ureca.uble.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UsageHistoryRepository extends JpaRepository<UsageHistory, Long> {
	void deleteByUser(User user);
}
