package com.ureca.uble.domain.users.repository;

import com.ureca.uble.domain.users.dto.response.UsageHistoryRes;
import com.ureca.uble.domain.common.dto.response.CursorPageRes;

public interface CustomUsageHistoryRepository {
	CursorPageRes<UsageHistoryRes> findUsagesByUserId(Long userId, Long lastHistoryId, int size);
}
