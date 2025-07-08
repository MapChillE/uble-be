package com.ureca.uble.domain.usageHistory.repository;

import com.ureca.uble.domain.usageHistory.dto.response.UsageHistoryRes;
import com.ureca.uble.global.dto.response.CursorPageRes;

public interface CustomUsageHistoryRepository {
	CursorPageRes<UsageHistoryRes> findUsagesByUserId(Long userId, Long lastHistoryId, int size);
}
