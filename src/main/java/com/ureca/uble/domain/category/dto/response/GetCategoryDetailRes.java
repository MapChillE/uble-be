package com.ureca.uble.domain.category.dto.response;

import com.ureca.uble.entity.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description="카테고리 상세 정보")
public class GetCategoryDetailRes {

    @Schema(description = "카테고리 id", example = "1")
    private Long categoryId;

    @Schema(description = "카테고리 이름", example = "푸드")
    private String categoryName;

    public static GetCategoryDetailRes from(Category category) {
        return GetCategoryDetailRes.builder()
            .categoryId(category.getId())
            .categoryName(category.getName())
            .build();
    }
}
