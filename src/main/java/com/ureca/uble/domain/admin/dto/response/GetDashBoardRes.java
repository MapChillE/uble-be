package com.ureca.uble.domain.admin.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "대시보드 정보 반환 DTO")
public class GetDashBoardRes {

    @Schema(description = "이번 달 이용자 수", example = "20")
    private long mau;

    @Schema(description = "지난 달 이용자 수", example = "30")
    private long lastMau;

    @Schema(description = "이번 달 혜택 사용 횟수", example = "200")
    private long usageCount;

    @Schema(description = "지난 달 혜택 사용 횟수", example = "250")
    private long lastUsageCount;

    @Schema(description = "총 제휴처 수", example = "215")
    private long totalBrandCount;

    @Schema(description = "총 매장 수", example = "5245")
    private long totalStoreCount;

    @Schema(description = "top 5 제휴처 (사용 수 기준)")
    private List<RankDetailRes> topUsageRankList;

    @Schema(description = "top 5 사용 지역")
    private List<RankDetailRes> topUsageLocalList;

    @Schema(description = "top 10 검색어")
    private List<RankDetailRes> topSearchKeywordList;

    public static GetDashBoardRes of(long mau, long lastMau, long usageCount, long lastUsageCount, long totalBrandCount, long totalStoreCount,
                                     List<RankDetailRes> topUsageRankList, List<RankDetailRes> topUsageLocalList, List<RankDetailRes> topSearchKeywordList) {
        return GetDashBoardRes.builder()
            .mau(mau)
            .lastMau(lastMau)
            .usageCount(usageCount)
            .lastUsageCount(lastUsageCount)
            .totalBrandCount(totalBrandCount)
            .totalStoreCount(totalStoreCount)
            .topUsageRankList(topUsageRankList)
            .topUsageLocalList(topUsageLocalList)
            .topSearchKeywordList(topSearchKeywordList)
            .build();
    }
}
