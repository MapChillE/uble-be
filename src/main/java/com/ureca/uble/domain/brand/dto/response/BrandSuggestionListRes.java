package com.ureca.uble.domain.brand.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Schema(description="제휴처 자동완성 조회 응답 DTO")
public class BrandSuggestionListRes {

    @Schema(description = "자동완성 목록", example = "12")
    private List<SuggestionRes> suggestionList;
}
