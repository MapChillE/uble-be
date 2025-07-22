package com.ureca.uble.domain.brand.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "지도 초기 데이터 조회 응답 DTO")
public class InitialDataRes {
    @Schema(description = "필터용 카테고리 리스트", required = true)
    private List<CategoryRes> categories;

    @Schema(description = "사용자 저장 위치 리스트", required = true)
    private List<LocationRes> locations;

    public static InitialDataRes of(List<CategoryRes> categories, List<LocationRes> locations) {
        return InitialDataRes.builder()
                .categories(categories)
                .locations(locations)
                .build();
    }
}
