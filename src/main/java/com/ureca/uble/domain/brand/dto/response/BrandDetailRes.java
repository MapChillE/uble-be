package com.ureca.uble.domain.brand.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ureca.uble.entity.Brand;
import com.ureca.uble.entity.enums.Season;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description="제휴처 상세 정보 조회 응답 DTO")
public class BrandDetailRes {
	@Schema(description = "제휴처 ID", example = "12")
	private Long brandId;

	@Schema(description = "제휴처 이름", example = "투썸 플레이스")
	private String name;

	@Schema(description = "문의 번호", example = "1543-5432")
	private String csrNumber;

	@Schema(description = "제휴처 설명", example = "얼그레이 밀크티 쉬폰 케이크가 맛있는 카페")
	private String description;

	@Schema(description = "제휴처 대표 이미지 url", example = "https://image.com")
	private String imgUrl;

	@Schema(description = "계절", example = "ETC")
	private Season season;

	@Schema(description = "카테고리", example = "푸드")
	private String categoryName;

	@Schema(description = "북마크 여부", example = "true")
	private boolean isBookmarked;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Schema(description = "북마크 ID", example = "36")
	private Long bookmarkId;

	@Schema(description = "혜택 리스트")
	private List<BenefitDetailRes> benefits;

	public static BrandDetailRes of(Brand brand, boolean isBookmarked, Long bookmarkId, List<BenefitDetailRes> benefits) {
		return BrandDetailRes.builder()
			.brandId(brand.getId())
			.name(brand.getName())
			.csrNumber(brand.getCsrNumber())
			.description(brand.getDescription())
			.imgUrl(brand.getImageUrl())
			.season(brand.getSeason())
			.categoryName(brand.getCategory().getName())
			.isBookmarked(isBookmarked)
			.bookmarkId(bookmarkId)
			.benefits(benefits)
			.build();
	}

}
