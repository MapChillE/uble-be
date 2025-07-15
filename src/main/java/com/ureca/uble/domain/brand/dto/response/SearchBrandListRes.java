package com.ureca.uble.domain.brand.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description="제휴처 목록 정보 검색 조회 응답 DTO")
public class SearchBrandListRes {
    @Schema(description = "제휴처 리스트", example = "제휴처 전체 List")
    private List<BrandListRes> brandList;

    @Schema(description = "총 제휴처 개수", example = "50")
    private long totalCount;

    @Schema(description = "총 페이지 수", example = "10")
    private long totalPage;

    public static SearchBrandListRes of(List<BrandListRes> brandList, long totalCount, long totalPage) {
        return SearchBrandListRes.builder()
            .brandList(brandList)
            .totalCount(totalCount)
            .totalPage(totalPage)
            .build();
    }
}
