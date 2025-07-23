package com.ureca.uble.domain.pin.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "자주 가는 곳 추가 응답 DTO")
public class GetPinRes {

    @Schema(description = "생성된 핀 ID", example = "1")
    private final Long pinId;

    @Schema(description = "사용자가 지정한 장소 별칭", example = "집")
    private final String name;

    @Schema(description = "경도 (longitude)", example = "127.04")
    private final double longitude;

    @Schema(description = "위도 (latitude)", example = "37.50")
    private final double latitude;

    @Schema(description = "장소의 도로명 주소", example = "서울시 강남구 테헤란로 12")
    private final String address;


    public static GetPinRes of(Long pinId, String name, double longitude, double latitude, String address) {
        return GetPinRes.builder()
                .pinId(pinId)
                .name(name)
                .longitude(longitude)
                .latitude(latitude)
                .address(address)
                .build();
    }
}
