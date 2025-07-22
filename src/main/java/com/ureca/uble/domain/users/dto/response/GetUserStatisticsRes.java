package com.ureca.uble.domain.users.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "사용자 통계 정보 반환 DTO")
public class GetUserStatisticsRes {

    @Schema(description = "많이 이용한 카테고리 순위")
    private List<CategoryRankRes> categoryRankList;

    @Schema(description = "많이 이용한 제휴처 순위")
    private List<BrandRankRes> brandRankList;

    @Schema(description = "혜택 많이 사용한 날짜/요일/시간대")
    private BenefitUsagePatternRes benefitUsagePattern;

    @Schema(description = "성별/나이별 평균 대비 사용량")
    private BenefitUsageComparisonRes benefitUsageComparison;

    @Schema(description = "월별 이용 횟수 (6개월)")
    private List<MonthlyBenefitUsageRes> monthlyBenefitUsageList;

    public static GetUserStatisticsRes of(List<CategoryRankRes> categoryRankList, List<BrandRankRes> brandRankList, BenefitUsagePatternRes benefitUsagePattern,
                                          BenefitUsageComparisonRes benefitUsageComparison, List<MonthlyBenefitUsageRes> monthlyBenefitUsageList) {
        return GetUserStatisticsRes.builder()
            .categoryRankList(categoryRankList)
            .brandRankList(brandRankList)
            .benefitUsagePattern(benefitUsagePattern)
            .benefitUsageComparison(benefitUsageComparison)
            .monthlyBenefitUsageList(monthlyBenefitUsageList)
            .build();
    }
}
