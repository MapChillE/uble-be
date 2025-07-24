package com.ureca.uble.domain.admin.dto.response;

import com.ureca.uble.entity.enums.RankTarget;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "제휴처/카테고리 클릭 내역 순위 반환 DTO")
public class GetClickRankListRes {

    @Schema(description = "통계 대상", example = "CATEGORY")
    private RankTarget rankTarget;

    @Schema(description = "결과 List")
    private List<RankDetailRes> clickRankList;

    public static GetClickRankListRes of(RankTarget rankTarget, List<RankDetailRes> clickRankList) {
        return GetClickRankListRes.builder()
            .rankTarget(rankTarget)
            .clickRankList(clickRankList)
            .build();
    }
}
