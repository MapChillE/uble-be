package com.ureca.uble.domain.brand.dto.response;

import com.ureca.uble.entity.Store;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "가장 가까운 제휴처 매장 반환 API")
public class GetNearestStoreRes {
    @Schema(description = "위도", example = "37.123")
    private Double latitude;

    @Schema(description = "경도", example = "127.123")
    private Double longitude;

    public static GetNearestStoreRes from(Store store) {
        return GetNearestStoreRes.builder()
            .latitude(store.getLocation().getY())
            .longitude(store.getLocation().getX())
            .build();
    }
}
