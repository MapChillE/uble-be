package com.ureca.uble.domain.common.controller;

import com.ureca.uble.domain.common.dto.request.CreateSearchLogReq;
import com.ureca.uble.domain.common.dto.response.CommonResponse;
import com.ureca.uble.domain.common.dto.response.CreateSearchLogRes;
import com.ureca.uble.domain.common.service.CommonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CommonController {

	private final CommonService commonService;

	@GetMapping("/health")
	public CommonResponse<String> healthCheck() {
		return CommonResponse.success("OK");
	}

	/**
	 * ElasticSearch에 전체 정보 넣는 API (임시)
	 */
	@Operation(summary = "ES 정보 전체 추가", description = "ES 정보 전체 추가")
	@GetMapping("/temp/elastic")
	public CommonResponse<String> updateIndex() {
		return CommonResponse.success(commonService.updateIndex());
	}

	/**
	 * 검색 로그 생성
	 *
	 * @param userId 사용자 정보
	 * @param req 검색 로그 정보
	 */
	@Operation(summary = "검색 로그 생성", description = "검색 로그 생성")
	@PostMapping("/search/log")
	public CommonResponse<CreateSearchLogRes> createSearchLog(
		@Parameter(description = "사용자정보", required = true)
		@AuthenticationPrincipal Long userId,
		@Parameter(description = "검색 종류", required = true)
		@Valid @RequestBody CreateSearchLogReq req) {
		return CommonResponse.success(commonService.createSearchLog(userId, req));
	}
}
