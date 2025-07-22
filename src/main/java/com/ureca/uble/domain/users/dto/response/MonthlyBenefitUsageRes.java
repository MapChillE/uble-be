package com.ureca.uble.domain.users.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "월별 이용 횟수 반환 DTO(6개월)")
public class MonthlyBenefitUsageRes {
    @Schema(description = "이용 연", example = "2025")
    private int year;

    @Schema(description = "이용 월", example = "7")
    private int month;

    @Schema(description = "이용 횟수", example = "100")
    private long usageCount;

    public static MonthlyBenefitUsageRes of(int year, int month, long usageCount) {
        return MonthlyBenefitUsageRes.builder()
            .year(year)
            .month(month)
            .usageCount(usageCount)
            .build();
    }
}
