package com.ureca.uble.domain.category.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Schema(description="전체 카테고리 조회 반환 DTO")
public class GetCategoryListRes {

    @Schema(description = "카테고리 리스트", example = "카테고리 리스트")
    private List<GetCategoryDetailRes> categoryList;
}
