package com.ureca.uble.domain.store.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Schema(description = "지도 내 자동완성 결과 반환 DTO")
public class GetGlobalSuggestionListRes {

    @Schema(description = "자동완성 결과 List")
    private List<GetGlobalSuggestionRes> suggestionList;
}
