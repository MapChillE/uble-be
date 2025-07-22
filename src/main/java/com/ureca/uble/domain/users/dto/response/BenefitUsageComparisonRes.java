package com.ureca.uble.domain.users.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "성별/나이별 평균 대비 사용량")
public class BenefitUsageComparisonRes {

    @Schema(description = "평균 사용량", example = "8.7")
    private double averageUsageCount;

    @Schema(description = "사용자 사용량", example = "15")
    private int userUsageCount;

    @Schema(description = "평균 대비 N% 더/덜 사용", example = "72.4")
    private double averageDiffPercent;

    public static BenefitUsageComparisonRes of(double averageUsageCount, int userUsageCount) {
        return BenefitUsageComparisonRes.builder()
            .averageUsageCount(averageUsageCount)
            .userUsageCount(userUsageCount)
            .averageDiffPercent(averageUsageCount == 0.0 ? 0.0 : ((userUsageCount - averageUsageCount) / averageUsageCount) * 100)
            .build();
    }
}
