package com.ureca.uble.domain.users.dto.request;

import com.ureca.uble.entity.enums.BenefitType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "이용 내역 생성 DTO")
public class CreateUsageHistoryReq {
    @Schema(description = "사용 혜택 종류", example = "VIP")
    private BenefitType benefitType;
}
