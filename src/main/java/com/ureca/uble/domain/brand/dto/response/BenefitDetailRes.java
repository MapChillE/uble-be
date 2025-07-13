package com.ureca.uble.domain.brand.dto.response;

import com.ureca.uble.entity.Benefit;
import com.ureca.uble.entity.enums.Rank;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description="등급별 혜택 상세 정보 DTO")
public class BenefitDetailRes {

	@Schema(description = "혜택 ID", example = "1")
	private Long benefitId;

	@Schema(description = "기본혜택(NORMAL), VIP콕(VIP)", example = "NORMAL")
	private String type;

	@Schema(description = "등급", example="NORMAL")
	private Rank minRank;

	@Schema(description = "혜택 내용", example="결제한 금액의 10% 할인")
	private String content;

	@Schema(description="이용 방법", example="바코드 제시")
	private String manual;

	@Schema(description="제공 단위", example="월 1회")
	private String provisionCount;

	public static BenefitDetailRes of(Benefit benefit, String type) {
		String provisionCount = benefit.getPeriod().formatProvisionCount(benefit.getNumber());
		return BenefitDetailRes.builder()
			.benefitId(benefit.getId())
			.type(type)
			.minRank(benefit.getRank())
			.content(benefit.getContent())
			.manual(benefit.getManual())
			.provisionCount(provisionCount)
			.build();
	}

}
