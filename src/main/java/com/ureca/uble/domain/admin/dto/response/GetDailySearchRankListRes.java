package com.ureca.uble.domain.admin.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Schema(description = "일별 검색어 인기 순위 반환 DTO")
public class GetDailySearchRankListRes {
    @Schema(description = "전체 인기 순위 리스트")
    List<GetRankListRes> rankList;
}
