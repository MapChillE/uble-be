package com.ureca.uble.domain.admin.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Schema(description = "지역구 이용 내역 순위 반환 DTO")
public class GetLocalRankListRes {
    @Schema(description = "결과 List")
    private List<RankDetailRes> usageRankList;
}
