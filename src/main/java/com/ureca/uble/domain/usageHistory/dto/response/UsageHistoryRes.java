package com.ureca.uble.domain.usageHistory.dto.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description="매장 이용내역 응답")
public class UsageHistoryRes {

	@Schema(description = "이용내역 ID", example = "100")
	private Long id;

	@Schema(description = "매장 이름", example = "투썸플레이스 선릉점")
	private String storeName;

	@Schema(description = "이용 시각", example = "2025-07-06T12:34:56")
	private LocalDateTime usedAt;

	public static UsageHistoryRes of(Long id, String storeName, LocalDateTime usedAt) {
		return UsageHistoryRes.builder()
			.id(id)
			.storeName(storeName)
			.usedAt(usedAt)
			.build();
	}
}
