package com.ureca.uble.domain.users.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "(이번 달 기준) 개인 통계 미리보기 반환 DTO")
public class GetUserStatisticsPreviewRes {

    @Schema(description = "(nullable) 가장 많이 사용한 카테고리", example = "카페")
    private String mostUsedCategoryName;

    @Schema(description = "(nullable) 가장 많이 사용한 제휴처", example = "할리스")
    private String mostUsedBrandName;

    @Schema(description = "이번 달 사용량", example = "12")
    private long monthlyUsedCount;

    public static GetUserStatisticsPreviewRes of(String mostUsedCategoryName, String mostUsedBrandName, long monthlyUsedCount) {
        return GetUserStatisticsPreviewRes.builder()
            .mostUsedCategoryName(mostUsedCategoryName)
            .mostUsedBrandName(mostUsedBrandName)
            .monthlyUsedCount(monthlyUsedCount)
            .build();
    }
}
