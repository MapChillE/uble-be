package com.ureca.uble.domain.store.dto.response;

import com.ureca.uble.entity.Store;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "매장 정보 반환 DTO")
public class GetStoreRes {
    @Schema(description = "매장 id", example = "1")
    private Long storeId;

    @Schema(description = "매장 이름", example = "CGV 강남")
    private String storeName;

    @Schema(description = "카테고리", example = "문화/여가")
    private String category;

    @Schema(description = "위도", example = "37.5017831")
    private Double latitude;

    @Schema(description = "경도", example = "127.0262445")
    private Double longitude;

    public static GetStoreRes from(Store store) {
        return GetStoreRes.builder()
            .storeId(store.getId())
            .storeName(store.getName())
            .category(store.getBrand().getCategory().getName())
            .latitude(store.getLocation().getY())
            .longitude(store.getLocation().getX())
            .build();
    }
}
