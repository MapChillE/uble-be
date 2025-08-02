package com.ureca.uble.domain.search.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "검색 로그 생성 반환 DTO")
public class CreateSearchLogRes {
    @Schema(description = "생성된 로그 id", example = "abcd")
    private String searchLogId;
}
