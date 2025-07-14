package com.ureca.uble.domain.brand.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ureca.uble.entity.Brand;
import com.ureca.uble.entity.document.BrandDocument;
import com.ureca.uble.entity.enums.Rank;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description="제휴처 목록 정보 조회 응답 DTO")
public class BrandListRes {
	@Schema(description = "제휴처 ID", example = "12")
	private Long brandId;

	@Schema(description = "제휴처 이름", example = "투썸 플레이스")
	private String name;

	@Schema(description = "카테고리 이름", example = "푸드")
	private String category;

	@Schema(description = "제휴처 설명", example = "커피가 맛있는 집")
	private String description;

	@Schema(description = "VIP콕 대상 여부", example = "false")
	@JsonProperty("isVIPcock")
	private boolean vipcock;

	@Schema(description = "최소 이용 등급", example = "NORMAL")
	private String minRank;

	@Schema(description = "제휴처 대표 이미지 url", example = "https://image.com")
	private String imgUrl;

	@Schema(description = "북마크 여부", example = "true")
	@JsonProperty("isBookmarked")
	private boolean bookmarked;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Schema(description = "북마크 ID", example = "36")
	private Long bookmarkId;

	public static BrandListRes of(Brand brand, boolean isBookmarked, Long bookmarkId, boolean isVIPcock, Rank minRank) {
		return BrandListRes.builder()
			.brandId(brand.getId())
			.name(brand.getName())
			.category(brand.getCategory().getName())
			.description(brand.getDescription())
			.vipcock(isVIPcock)
			.minRank(minRank.toString())
			.imgUrl(brand.getImageUrl())
			.bookmarked(isBookmarked)
			.bookmarkId(bookmarkId)
			.build();
	}

	public static BrandListRes of(BrandDocument brand, boolean isBookmarked, Long bookmarkId) {
		return BrandListRes.builder()
			.brandId(brand.getBrandId())
			.name(brand.getBrandName())
			.category(brand.getCategory())
			.description(brand.getDescription())
			.vipcock(brand.getIsVipCock())
			.minRank(brand.getMinRank())
			.imgUrl(brand.getImageUrl())
			.bookmarked(isBookmarked)
			.bookmarkId(bookmarkId)
			.build();
	}
}
