package com.ureca.uble.domain.bookmark.controller;

import com.ureca.uble.domain.bookmark.dto.response.CreateBookmarkRes;
import com.ureca.uble.domain.bookmark.dto.response.DeleteBookmarkRes;
import com.ureca.uble.domain.bookmark.dto.response.GetBookmarkRes;
import com.ureca.uble.domain.bookmark.service.BookmarkService;
import com.ureca.uble.domain.common.dto.response.CommonResponse;
import com.ureca.uble.domain.common.dto.response.CursorPageRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    /**
     * 즐겨찾기 추가
     * @param userId 사용자 정보
     * @param brandId 제휴처 id
     */
    @Operation(summary = "즐겨찾기 추가", description = "즐겨찾기 추가")
    @PostMapping
    public CommonResponse<CreateBookmarkRes> createBookmark(
        @Parameter(description = "사용자정보", required = true)
        @AuthenticationPrincipal Long userId,
        @Parameter(description = "제휴처 id", required = true)
        @RequestParam Long brandId) {
        return CommonResponse.success(bookmarkService.createBookmark(userId, brandId));
    }

    /**
     * 즐겨찾기 삭제
     *
     * @param userId 사용자 정보
     * @param brandId 제휴처 id
     */
    @Operation(summary = "즐겨찾기 삭제", description = "즐겨찾기 삭제")
    @DeleteMapping("/{brandId}")
    public CommonResponse<DeleteBookmarkRes> deleteBookmark(
        @Parameter(description = "사용자정보", required = true)
        @AuthenticationPrincipal Long userId,
        @Parameter(description = "제휴처 id", required = true)
        @PathVariable Long brandId) {
        return CommonResponse.success(bookmarkService.deleteBookmark(userId, brandId));
    }

    /**
     * 즐겨찾기 전체 조회
     *
     * @param userId 사용자 정보
     * @param lastBookmarkId 마지막 커서 id
     * @param size 페이지 크기
     */
    @Operation(summary = "즐겨찾기 전체 조회", description = "즐겨찾기 전체 조회")
    @GetMapping
    public CommonResponse<CursorPageRes<GetBookmarkRes>> getBookmarks(
        @Parameter(description = "사용자정보", required = true)
        @AuthenticationPrincipal Long userId,
        @Parameter(description = "마지막 즐겨찾기 Id")
        @RequestParam(required = false) Long lastBookmarkId,
        @Parameter(description = "한 번에 가져올 크기")
        @RequestParam(defaultValue = "5") int size) {
        return CommonResponse.success(bookmarkService.getBookmarks(userId, lastBookmarkId, size));
    }
}
