package com.ureca.uble.domain.brand.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ureca.uble.entity.Brand;

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

	@Schema(description = "제휴처 대표 이미지 url", example = "https://image.com")
	private String imgUrl;

	@Schema(description = "북마크 여부", example = "true")
	private boolean isBookmarked;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Schema(description = "북마크 ID", example = "36")
	private Long bookmarkId;

	@Schema(description = "혜택 리스트")
	private List<BenefitListRes> benefits;

	public static BrandListRes of(Brand brand, boolean isBookmarked, Long bookmarkId, List<BenefitListRes> benefits) {
		return BrandListRes.builder()
			.brandId(brand.getId())
			.name(brand.getName())
			.imgUrl(brand.getImageUrl())
			.isBookmarked(isBookmarked)
			.bookmarkId(bookmarkId)
			.benefits(benefits)
			.build();
	}

}
