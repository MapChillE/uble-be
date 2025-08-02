package com.ureca.uble.domain.search.controller;

import com.ureca.uble.domain.common.dto.response.CommonResponse;
import com.ureca.uble.domain.search.dto.request.CreateSearchLogReq;
import com.ureca.uble.domain.search.dto.response.CreateSearchLogRes;
import com.ureca.uble.domain.search.dto.response.TopKeywordListRes;
import com.ureca.uble.domain.search.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    /**
     * 검색 로그 생성
     *
     * @param userId 사용자 정보
     * @param req 검색 로그 정보
     */
    @Operation(summary = "검색 로그 생성", description = "검색 로그 생성")
    @PostMapping("/log")
    public CommonResponse<CreateSearchLogRes> createSearchLog(
        @Parameter(description = "사용자정보", required = true)
        @AuthenticationPrincipal Long userId,
        @Parameter(description = "검색 종류", required = true)
        @Valid @RequestBody CreateSearchLogReq req) {
        return CommonResponse.success(searchService.createSearchLog(userId, req));
    }

    /**
     * 인기 검색어 조회
     */
    @Operation(summary = "인기 검색어 조회", description = "인기 검색어 조회")
    @GetMapping("/popular-keywords")
    public CommonResponse<TopKeywordListRes> getPopularKeywordList() {
        return CommonResponse.success(searchService.getPopularKeywordList());
    }
}
