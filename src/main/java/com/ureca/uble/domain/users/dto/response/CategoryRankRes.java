package com.ureca.uble.domain.users.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "많이 사용한 카테고리 순위 반환 DTO")
public class CategoryRankRes {

    @Schema(description = "카테고리명", example = "카페")
    private String categoryName;

    @Schema(description = "사용 횟수", example = "100")
    private long usageCount;

    public static CategoryRankRes of(String categoryName, long usageCount) {
        return CategoryRankRes.builder()
            .categoryName(categoryName)
            .usageCount(usageCount)
            .build();
    }
}
