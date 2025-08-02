package com.ureca.uble.domain.search.dto.response;

import com.ureca.uble.entity.enums.RankChangeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KeywordRankRes {

    @Schema(description = "검색어", example = "맛집")
    private String keyword;

    @Schema(description = "순위", example = "1")
    private int rank;

    @Schema(description = "검색 횟수", example = "10")
    private long count;

    @Schema(description = "변화 종류", example = "UP")
    private RankChangeType change;

    @Schema(description = "등수 변화", example = "2")
    private int diff;

    public static KeywordRankRes of(String keyword, int rank, long count, RankChangeType change, int diff) {
        return KeywordRankRes.builder()
            .keyword(keyword)
            .rank(rank)
            .count(count)
            .change(change)
            .diff(diff)
            .build();
    }
}
