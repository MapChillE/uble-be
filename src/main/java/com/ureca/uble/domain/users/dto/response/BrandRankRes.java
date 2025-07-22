package com.ureca.uble.domain.users.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "많이 사용한 제휴처 순위 반환 DTO")
public class BrandRankRes {

    @Schema(description = "제휴처명", example = "할리스")
    private String brandName;

    @Schema(description = "사용 횟수", example = "100")
    private long usageCount;

    public static BrandRankRes of(String brandName, long usageCount) {
        return BrandRankRes.builder()
            .brandName(brandName)
            .usageCount(usageCount)
            .build();
    }
}
