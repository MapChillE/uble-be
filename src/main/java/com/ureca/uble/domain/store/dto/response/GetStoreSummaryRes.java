package com.ureca.uble.domain.store.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ureca.uble.entity.Store;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "매장 소모달 정보 반환 DTO")
public class GetStoreSummaryRes {
    @Schema(description = "제휴처 id", example = "123")
    private Long brandId;

    @Schema(description = "매장 id", example = "123")
    private Long storeId;

    @Schema(description = "매장 이름", example = "스타벅스 선릉점")
    private String storeName;

    @Schema(description = "매장 설명", example = "커피가 맛있는 스타벅스")
    private String description;

    @Schema(description = "매장 대표 연락처", example = "02-1234-5678")
    private String phoneNumber;

    @Schema(description = "매장 주소", example = "서울 강남구 테헤란로64길 18")
    private String address;

    @Schema(description = "매장까지의 거리 (m 기준)", example = "900.123")
    private Double distance;

    @Schema(description = "카테고리", example = "음식점")
    private String category;

    @Schema(description = "대표 이미지 URL", example = "https://example.com")
    private String imageUrl;

    @Schema(description = "북마크 여부", example = "true")
    @JsonProperty("isBookmarked")
    private boolean bookmarked;

    public static GetStoreSummaryRes of(Store store, Double distance, boolean bookmarked) {
        return GetStoreSummaryRes.builder()
            .brandId(store.getBrand().getId())
            .storeId(store.getId())
            .storeName(store.getName())
            .description(store.getBrand().getDescription())
            .phoneNumber(store.getPhoneNumber())
            .address(store.getAddress())
            .distance(distance)
            .category(store.getBrand().getCategory().getName())
            .imageUrl(store.getBrand().getImageUrl())
            .bookmarked(bookmarked)
            .build();
    }
}
