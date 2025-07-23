package com.ureca.uble.domain.admin.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "순위 반환 DTO")
public class RankDetailRes {

    @Schema(description = "이름", example = "이름")
    private String name;

    @Schema(description = "횟수", example = "100")
    private Long count;

    public static RankDetailRes of(String name, Long count) {
        return RankDetailRes.builder()
            .name(name)
            .count(count)
            .build();
    }
}
