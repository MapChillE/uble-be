package com.ureca.uble.domain.bookmark.dto.response;

import com.ureca.uble.entity.Bookmark;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "즐겨찾기 정보 반환 DTO")
public class GetBookmarkRes {
    @Schema(description = "즐겨찾기 id", example = "true")
    private Long bookmarkId;

    @Schema(description = "제휴처 id", example = "1")
    private Long brandId;

    @Schema(description = "제휴처 이름", example = "1")
    private String brandName;

    @Schema(description = "제휴처 카테고리", example = "문화/여가")
    private String category;

    @Schema(description = "제휴처 로고 이미지", example = "링크")
    private String brandImageUrl;

    public static GetBookmarkRes from(Bookmark bookmark) {
        return GetBookmarkRes.builder()
            .bookmarkId(bookmark.getId())
            .brandId(bookmark.getBrand().getId())
            .brandName(bookmark.getBrand().getName())
            .category(bookmark.getBrand().getCategory().getName())
            .brandImageUrl(bookmark.getBrand().getImageUrl())
            .build();
    }
}
