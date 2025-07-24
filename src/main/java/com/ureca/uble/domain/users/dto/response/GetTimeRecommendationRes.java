package com.ureca.uble.domain.users.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ureca.uble.entity.Brand;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "추천 제휴처 정보 반환 DTO")
public class GetTimeRecommendationRes {
    @Schema(description = "제휴처 id", example = "1")
    private Long brandId;

    @Schema(description = "제휴처 이름", example = "CGV 강남")
    private String brandName;

    @Schema(description = "카테고리", example = "문화/여가")
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

    public static GetTimeRecommendationRes from(Brand brand) {
        return GetTimeRecommendationRes.builder()
            .brandId(brand.getId())
            .brandName(brand.getName())
            .category(brand.getCategory().getName())
            .description(brand.getDescription())
            .vipcock(brand.isVIPcock())
            .minRank(brand.getMinRank().toString())
            .imgUrl(brand.getImageUrl())
            .build();
    }
}
