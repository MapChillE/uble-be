package com.ureca.uble.domain.search.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Schema(description = "인기 검색어 순위 List 반환")
public class TopKeywordListRes {
    @Schema(description = "인기 검색어 List")
    private List<KeywordRankRes> keywordList;
}
