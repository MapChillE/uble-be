package com.ureca.uble.domain.admin.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Schema(description = "결과 미포함 검색어 순위 반환 DTO")
public class GetEmptySearchRankListRes {
    @Schema(description = "순위 List")
    List<RankDetailRes> rankList;
}
