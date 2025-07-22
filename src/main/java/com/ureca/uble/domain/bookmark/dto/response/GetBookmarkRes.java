package com.ureca.uble.domain.bookmark.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    private String name;

    @Schema(description = "제휴처 설명", example = "커피가 맛있는 집")
    private String description;

    @Schema(description = "VIP콕 대상 여부", example = "false")
    @JsonProperty("isVIPcock")
    private boolean vipcock;

    @Schema(description = "최소 이용 등급", example = "NORMAL")
    private String minRank;

    @Schema(description = "제휴처 카테고리", example = "문화/여가")
    private String category;

    @Schema(description = "제휴처 로고 이미지", example = "링크")
    private String imgUrl;

    @Schema(description = "북마크 여부", example = "true")
    @JsonProperty("isBookmarked")
    private boolean bookmarked;

    public static GetBookmarkRes from(Bookmark bookmark) {
        return GetBookmarkRes.builder()
            .bookmarkId(bookmark.getId())
            .brandId(bookmark.getBrand().getId())
            .name(bookmark.getBrand().getName())
            .description(bookmark.getBrand().getDescription())
            .vipcock(bookmark.getBrand().isVIPcock())
            .minRank(bookmark.getBrand().getMinRank().toString())
            .category(bookmark.getBrand().getCategory().getName())
            .imgUrl(bookmark.getBrand().getImageUrl())
            .bookmarked(true)
            .build();
    }
}
