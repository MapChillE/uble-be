package com.ureca.uble.domain.users.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "추천 정보 반환 DTO")
public class GetRecommendationsRes {

    @Schema(description = "매장 id", example = "1")
    private Long storeId;

    @Schema(description = "브랜드 id", example = "1")
    private Long brandId;

    @Schema(description = "매장 이름", example = "CGV 강남")
    private String name;

    @Schema(description = "위도", example = "37.5017831")
    private Double latitude;

    @Schema(description = "경도", example = "127.0262445")
    private Double longitude;

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
}
