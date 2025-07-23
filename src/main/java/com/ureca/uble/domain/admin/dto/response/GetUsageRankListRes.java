package com.ureca.uble.domain.admin.dto.response;

import com.ureca.uble.entity.enums.RankTarget;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "제휴처/카테고리 이용 내역 순위 반환 DTO")
public class GetUsageRankListRes {

    @Schema(description = "통계 대상", example = "CATEGORY")
    private RankTarget rankTarget;

    @Schema(description = "결과 List")
    private List<UsageRankDetailRes> usageRankList;

    public static GetUsageRankListRes of(RankTarget rankTarget, List<UsageRankDetailRes> usageRankList) {
        return GetUsageRankListRes.builder()
            .rankTarget(rankTarget)
            .usageRankList(usageRankList)
            .build();
    }
}
