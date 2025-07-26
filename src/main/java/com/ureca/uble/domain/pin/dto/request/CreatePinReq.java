package com.ureca.uble.domain.pin.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "자주 가는 곳 등록 요청 DTO")
public class CreatePinReq {
    @Schema(description = "사용자가 지정한 장소 별칭", example = "집")
    @NotBlank(message = "장소 별칭은 필수입니다.")
    @Size(max = 24, message = "장소 별칭은 24자 이하로 입력해주세요.")
    private String name;

    @Schema(description = "경도 (longitude)", example = "127.04")
    @NotNull(message = "경도는 필수입니다.")
    @DecimalMin(value = "-180.0", inclusive = true, message = "경도는 -180 이상이어야 합니다.")
    @DecimalMax(value = "180.0", inclusive = true, message = "경도는 180 이하이어야 합니다.")
    private double longitude;

    @Schema(description = "위도 (latitude)", example = "37.50")
    @NotNull(message = "위도는 필수입니다.")
    @DecimalMin(value = "-90.0", inclusive = true, message = "위도는 -90 이상이어야 합니다.")
    @DecimalMax(value = "90.0", inclusive = true, message = "위도는 90 이하이어야 합니다.")
    private double latitude;

    @Schema(description = "장소의 도로명 주소", example = "서울시 강남구 테헤란로 12")
    @NotBlank(message = "장소 별칭은 필수입니다.")
    @Size(max = 100, message = "도로명 주소는 100자 이하로 입력해주세요.")
    private String address;
}
