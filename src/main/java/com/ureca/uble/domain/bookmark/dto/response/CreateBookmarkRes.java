package com.ureca.uble.domain.bookmark.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "즐겨찾기 추가 반환 DTO")
public class CreateBookmarkRes {
    @Schema(description = "생성된 즐겨찾기 id", example = "1")
    private Long bookmarkId;
}
