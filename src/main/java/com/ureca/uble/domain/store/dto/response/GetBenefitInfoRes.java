package com.ureca.uble.domain.store.dto.response;

import com.ureca.uble.entity.Benefit;
import com.ureca.uble.entity.enums.BenefitType;
import com.ureca.uble.entity.enums.Period;
import com.ureca.uble.entity.enums.Rank;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "혜택 정보 반환 DTO")
public class GetBenefitInfoRes {
    @Schema(description = "혜택 id", example = "1")
    private Long benefitId;

    @Schema(description = "혜택 종류", example = "NORMAL")
    private String type;

    @Schema(description = "혜택 사용 가능 최소 등급", example = "NORMAL")
    private String minRank;

    @Schema(description = "혜택 정보", example = "패밀리 사이즈(5가지 맛) 4천원 할인")
    private String content;

    @Schema(description = "이용 방법", example = "U+ 멤버십 앱 메인 화면에서 검색 후 쿠폰 다운")
    private String manual;

    @Schema(description = "제공 횟수", example = "월 1회")
    private String provisionCount;

    public static GetBenefitInfoRes of(Benefit benefit, BenefitType type) {
        return GetBenefitInfoRes.builder()
            .benefitId(benefit.getId())
            .type(type.name())
            .minRank(benefit.getRank() == Rank.NONE ? "VIP" : benefit.getRank().toString())
            .content(benefit.getContent())
            .manual(benefit.getManual())
            .provisionCount(benefit.getPeriod().formatProvisionCount(benefit.getNumber()))
            .build();
    }

}
