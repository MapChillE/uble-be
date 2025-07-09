package com.ureca.uble.domain.brand.dto.response;

import com.ureca.uble.entity.enums.Period;
import com.ureca.uble.entity.enums.Rank;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description="등급별 헤택 정보 DTO")
public class BenefitRes {

	@Schema(description = "등급", example="우수/VIP/VVIP")
	private Rank rank;

	@Schema(description = "혜택 내용", example="결제한 금액의 10% 할인")
	private String content;

	@Schema(description="이용 방법", example="바코드 제시")
	private String manual;

	@Schema(description="제공단위", example="월")
	private Period period;

	@Schema(description="제공횟수", example="1")
	private Integer number;

	public BenefitRes(Rank rank, String content, String manual, Period period, Integer number) {
		this.rank = rank;
		this.content = content;
		this.manual = manual;
		this.period = period;
		this.number = number;
	}
}
