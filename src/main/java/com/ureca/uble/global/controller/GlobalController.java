package com.ureca.uble.global.controller;

import com.ureca.uble.global.response.CommonResponse;
import com.ureca.uble.global.service.GlobalService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GlobalController {

	private final GlobalService globalService;

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
		return CommonResponse.success(globalService.updateIndex());
	}
}
