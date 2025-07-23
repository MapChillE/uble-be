package com.ureca.uble.domain.pin.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Schema(description = "지도 초기 데이터 조회 응답 DTO")
public class InitialDataRes {

    @Schema(description = "사용자 저장 위치 리스트", required = true)
    private List<LocationRes> locations;
}
