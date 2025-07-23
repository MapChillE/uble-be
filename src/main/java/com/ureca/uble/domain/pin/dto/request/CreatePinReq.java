package com.ureca.uble.domain.pin.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "자주 가는 곳 등록 요청 DTO")
public class CreatePinReq {
    @Schema(description = "사용자가 지정한 장소 별칭", example = "집")
    private String name;

    @Schema(description = "경도 (longitude)", example = "127.04")
    private double longitude;

    @Schema(description = "위도 (latitude)", example = "37.50")
    private double latitude;

    @Schema(description = "장소의 도로명 주소", example = "서울시 강남구 테헤란로 12")
    private String address;
}
