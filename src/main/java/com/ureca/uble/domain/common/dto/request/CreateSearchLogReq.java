package com.ureca.uble.domain.common.dto.request;

import com.ureca.uble.entity.enums.SearchType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@Schema(description = "검색 로그 생성 요청 DTO")
public class CreateSearchLogReq {
    @Schema(description = "검색 타입 (ENTER, CLICK)", example = "CLICK")
    private SearchType searchType;

    @Schema(description = "검색어", example = "뽀로로")
    @NotBlank(message = "검색어는 필수입니다.")
    private String keyword;

    @Schema(description = "검색 결과 존재 여부", example = "true")
    @NotNull(message = "검색 결과 존재 여부는 필수입니다.")
    private Boolean isResultExists;
}
