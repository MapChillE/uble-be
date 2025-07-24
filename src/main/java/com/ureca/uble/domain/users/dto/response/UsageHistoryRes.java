package com.ureca.uble.domain.users.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description="매장 이용내역 응답")
public class UsageHistoryRes {

	@Schema(description = "이용내역 ID", example = "abcd")
	private String id;

	@Schema(description = "매장 이름", example = "투썸플레이스 선릉점")
	private String storeName;

	@Schema(description = "이용 시각", example = "2025-07-06T12:34:56")
	private LocalDateTime usedAt;

	@Schema(description = "카테고리", example = "카페")
	private String category;

	@Schema(description = "이미지 URL", example = "카페")
	private String imageUrl;

	public static UsageHistoryRes of(String id, String storeName, LocalDateTime usedAt, String category, String imageUrl) {
		return UsageHistoryRes.builder()
			.id(id)
			.storeName(storeName)
			.usedAt(usedAt)
			.category(category)
			.imageUrl(imageUrl)
			.build();
	}
}
