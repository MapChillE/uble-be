package com.ureca.uble.domain.brand.dto.response;

import com.ureca.uble.entity.enums.SuggestionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description="제휴처 자동완성 조회 응답 DTO")
public class SuggestionRes {

    @Schema(description = "자동완성", example = "할리스")
    private String suggestion;

    @Schema(description = "자동완성 타입", example = "BRAND")
    private SuggestionType type;

    public static SuggestionRes of(String suggestion, SuggestionType type) {
        return SuggestionRes.builder()
            .suggestion(suggestion)
            .type(type)
            .build();
    }
}
