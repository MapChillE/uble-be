package com.ureca.uble.domain.admin.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "제휴처/카테고리 이용 내역 순위 반환 DTO")
public class UsageRankDetailRes {

    @Schema(description = "카테고리/제휴처 이름", example = "푸드 or 할리스")
    private String name;

    @Schema(description = "이용 횟수", example = "100")
    private Long count;

    public static UsageRankDetailRes of(String name, Long count) {
        return UsageRankDetailRes.builder()
            .name(name)
            .count(count)
            .build();
    }
}
