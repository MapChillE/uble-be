package com.ureca.uble.domain.brand.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "저장 위치 정보 DTO")
public class LocationRes {
    @Schema(description = "저장 위치 ID", example = "10")
    private Long id;

    @Schema(description = "저장 위치 명칭", example = "집")
    private String name;

    @Schema(description = "경도(longitude)", example = "127.027644")
    private double longitude;

    @Schema(description = "위도(latitude)", example = "37.497943")
    private double latitude;

    public static LocationRes of(Long id, String name, double longitude, double latitude) {
        return LocationRes.builder()
                .id(id)
                .name(name)
                .longitude(longitude)
                .latitude(latitude)
                .build();
    }
}
