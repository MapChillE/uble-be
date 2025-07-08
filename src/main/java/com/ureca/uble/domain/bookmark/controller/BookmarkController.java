package com.ureca.uble.domain.bookmark.controller;

import com.ureca.uble.domain.bookmark.dto.response.CreateBookmarkRes;
import com.ureca.uble.domain.bookmark.dto.response.DeleteBookmarkRes;
import com.ureca.uble.domain.bookmark.service.BookmarkService;
import com.ureca.uble.global.response.CommonResponse;
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
     * @param bookmarkId 제휴처 id
     */
    @Operation(summary = "즐겨찾기 삭제", description = "즐겨찾기 삭제")
    @DeleteMapping("/{bookmarkId}")
    public CommonResponse<DeleteBookmarkRes> deleteBookmark(
        @Parameter(description = "사용자정보", required = true)
        @AuthenticationPrincipal Long userId,
        @Parameter(description = "즐겨찾기 id", required = true)
        @PathVariable Long bookmarkId) {
        return CommonResponse.success(bookmarkService.deleteBookmark(userId, bookmarkId));
    }
}
