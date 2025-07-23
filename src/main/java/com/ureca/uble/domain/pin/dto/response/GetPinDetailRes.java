package com.ureca.uble.domain.pin.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "저장 위치 정보 DTO")
public class GetPinDetailRes {
    @Schema(description = "저장 위치 ID", example = "10")
    private Long id;

    @Schema(description = "저장 위치 명칭", example = "집")
    private String name;

    @Schema(description = "경도(longitude)", example = "127.027644")
    private double longitude;

    @Schema(description = "위도(latitude)", example = "37.497943")
    private double latitude;

    @Schema(description = "저장 위치 주소", example = "서울 강남구 테헤란로 340")
    private String address;

    public static GetPinDetailRes of(Long id, String name, double longitude, double latitude, String address) {
        return GetPinDetailRes.builder()
                .id(id)
                .name(name)
                .longitude(longitude)
                .latitude(latitude)
                .address(address)
                .build();
    }
}
