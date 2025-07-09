package com.ureca.uble.domain.brand.dto.response;

import com.ureca.uble.entity.Benefit;
import com.ureca.uble.entity.enums.Rank;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description="등급별 헤택 간략 정보 DTO")
public class BenefitListRes {

	@Schema(description = "등급", example="우수/VIP/VVIP")
	private Rank rank;

	@Schema(description = "혜택 내용", example="결제한 금액의 10% 할인")
	private String content;

	@Schema(description="제공 단위", example="월 1회")
	private String provisionCount;

	public static BenefitListRes from(Benefit benefit) {
		String provisionCount = benefit.getPeriod().formatProvisionCount(benefit.getNumber());
		return BenefitListRes.builder()
			.rank(benefit.getRank())
			.content(benefit.getContent())
			.provisionCount(provisionCount)
			.build();
	}

}
