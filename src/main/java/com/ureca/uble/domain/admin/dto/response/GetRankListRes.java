package com.ureca.uble.domain.admin.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@Schema(description = "일별 검색어 인기 순위 반환 DTO")
public class GetRankListRes {
    @Schema(description = "대상 일자", example = "2025-07-26")
    private LocalDate date;

    @Schema(description = "인기 순위 리스트")
    private List<RankDetailRes> rankList;

    public static GetRankListRes of(LocalDate date, List<RankDetailRes> rankList) {
        return GetRankListRes.builder()
            .date(date)
            .rankList(rankList)
            .build();
    }
}
