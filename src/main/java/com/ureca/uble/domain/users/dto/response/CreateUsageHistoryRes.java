package com.ureca.uble.domain.users.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description="매장 이용내역 생성 DTO 응답")
public class CreateUsageHistoryRes {
    @Schema(description = "이용내역 ID", example = "1")
    private String id;
}
