package com.ureca.uble.domain.users.dto.response;

import com.ureca.uble.entity.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "비슷한 유저 기반 추천 리스트 반환 DTO")
public class GetSimilarUserRecommendationListRes {
    @Schema(description = "나이대", example = "20")
    private int ageRange;

    @Schema(description = "성별", example = "FEMALE")
    private Gender gender;

    @Schema(description = "추천 리스트")
    private List<GetSimilarUserRecommendationRes> recommendationsList;

    public static GetSimilarUserRecommendationListRes of(int ageRange, Gender gender, List<GetSimilarUserRecommendationRes> recommendationsList) {
        return GetSimilarUserRecommendationListRes.builder()
            .ageRange(ageRange)
            .gender(gender)
            .recommendationsList(recommendationsList)
            .build();
    }
}
