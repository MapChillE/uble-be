package com.ureca.uble.domain.store.dto.response;

import com.ureca.uble.entity.Store;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "매장 상세 정보 반환 DTO")
public class GetStoreDetailRes {
    @Schema(description = "제휴처 id", example = "123")
    private Long brandId;

    @Schema(description = "매장 id", example = "123")
    private Long storeId;

    @Schema(description = "매장 이름", example = "스타벅스 선릉점")
    private String storeName;

    @Schema(description = "매장 설명", example = "커피가 맛있는 스타벅스")
    private String description;

    @Schema(description = "매장 주소", example = "서울 강남구 테헤란로64길 18")
    private String address;

    @Schema(description = "매장 대표 연락처", example = "02-1234-5678")
    private String phoneNumber;

    @Schema(description = "매장까지의 거리 (m 기준)", example = "900.123")
    private Double distance;

    @Schema(description = "카테고리", example = "음식점")
    private String category;

    @Schema(description = "대표 이미지 URL", example = "https://example.com")
    private String imageUrl;

    @Schema(description = "(사용자) 기본 혜택 사용 가능 여부", example = "true")
    private boolean isNormalAvailable;

    @Schema(description = "(사용자) VIP 콕 혜택 사용 가능 여부", example = "true")
    private boolean isVipAvailable;

    @Schema(description = "(사용자) 우리 동네 멤버십 혜택 사용 가능 여부", example = "false")
    private boolean isLocalAvailable;

    @Schema(description = "혜택 리스트", example = "혜택 리스트")
    private List<GetBenefitInfoRes> benefitList;

    public static GetStoreDetailRes of(Store store,Double distance, boolean isNormalAvailable, boolean isVipAvailable,
                                       boolean isLocalAvailable, List<GetBenefitInfoRes> benefitList) {
        return GetStoreDetailRes.builder()
            .brandId(store.getBrand().getId())
            .storeId(store.getId())
            .storeName(store.getName())
            .description(store.getBrand().getDescription())
            .address(store.getAddress())
            .phoneNumber(store.getPhoneNumber())
            .distance(distance)
            .category(store.getBrand().getCategory().getName())
            .imageUrl(store.getBrand().getImageUrl())
            .isNormalAvailable(isNormalAvailable)
            .isVipAvailable(isVipAvailable)
            .isLocalAvailable(isLocalAvailable)
            .benefitList(benefitList)
            .build();
    }
}
