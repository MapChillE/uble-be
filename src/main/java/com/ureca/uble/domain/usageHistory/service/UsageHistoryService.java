package com.ureca.uble.domain.usageHistory.service;

import org.springframework.stereotype.Service;

import com.ureca.uble.domain.usageHistory.dto.response.UsageHistoryRes;
import com.ureca.uble.domain.usageHistory.repository.CustomUsageHistoryRepository;
import com.ureca.uble.entity.UsageHistory;
import com.ureca.uble.global.dto.response.CursorPageRes;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsageHistoryService {

	private final CustomUsageHistoryRepository usageRepository;

	public CursorPageRes<UsageHistoryRes> getUsageHistory(Long userId, Long lastHistoryId, int size) {
		return usageRepository.findUsagesByUserId(userId, lastHistoryId, size);
	}
}
