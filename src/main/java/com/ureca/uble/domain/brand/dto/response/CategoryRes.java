package com.ureca.uble.domain.brand.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "카테고리 정보 DTO")
public class CategoryRes {
    @Schema(description = "카테고리 ID", example = "1")
    private Long id;

    @Schema(description = "카테고리 이름", example = "액티비티")
    private String name;

    public static CategoryRes of(Long id, String name) {
        return CategoryRes.builder()
                .id(id)
                .name(name)
                .build();
    }
}
