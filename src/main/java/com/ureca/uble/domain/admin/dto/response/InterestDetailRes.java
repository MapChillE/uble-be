package com.ureca.uble.domain.admin.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "관심사 순위 상세 정보 반환 DTO")
public class InterestDetailRes {
    @Schema(description = "브랜드명", example = "할리스")
    private String name;

    @Schema(description = "통합 점수", example = "100")
    private Long totalScore;

    @Schema(description = "제휴처 클릭수", example = "20")
    private Long brandClickCount;

    @Schema(description = "매장 클릭수", example = "20")
    private Long storeClickCount;

    @Schema(description = "사용량", example = "20")
    private Long usageCount;

    public static InterestDetailRes of(String name, Long totalScore, Long brandClickCount, Long storeClickCount, Long usageCount) {
        return InterestDetailRes.builder()
            .name(name)
            .totalScore(totalScore)
            .brandClickCount(brandClickCount)
            .storeClickCount(storeClickCount)
            .usageCount(usageCount)
            .build();
    }
}
