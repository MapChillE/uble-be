package com.ureca.uble.domain.admin.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Schema(description = "관심사 변화 추이 반환 DTO")
public class GetInterestChangeRes {
    @Schema(description = "결과 List")
    private List<GetInterestListRes> interestRankList;
}
