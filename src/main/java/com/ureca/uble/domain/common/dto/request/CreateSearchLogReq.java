package com.ureca.uble.domain.common.dto.request;

import com.ureca.uble.entity.enums.SearchType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "검색 로그 생성 요청 DTO")
public class CreateSearchLogReq {
    @Schema(description = "검색 타입 (ENTER, CLICK)", example = "CLICK")
    private SearchType searchType;

    @Schema(description = "검색어", example = "뽀로로")
    private String keyword;

    @Schema(description = "검색 결과 존재 여부", example = "true")
    private Boolean isResultExists;
}
