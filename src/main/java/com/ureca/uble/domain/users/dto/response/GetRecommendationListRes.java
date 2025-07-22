package com.ureca.uble.domain.users.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "추천 리스트 반환 DTO")
public class GetRecommendationListRes {
    @Schema(description = "추천 리스트")
    private List<GetRecommendationsRes> recommendationsList;
}
