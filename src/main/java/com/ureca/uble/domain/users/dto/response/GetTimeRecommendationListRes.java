package com.ureca.uble.domain.users.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Schema(description = "시간대 기반 추천 반환 DTO")
public class GetTimeRecommendationListRes {

    @Schema(description = "추천 리스트")
    private List<GetTimeRecommendationRes> recommendationsList;
}
