package com.ureca.uble.domain.brand.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ureca.uble.domain.brand.dto.response.BrandDetailRes;
import com.ureca.uble.domain.brand.service.BrandService;
import com.ureca.uble.global.response.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandController {

	private final BrandService brandService;

	@Operation(summary = "제휴처 상세 조회")
	@GetMapping("/{brandId}")
	public CommonResponse<BrandDetailRes> getBrandDetail(
		@Parameter(description = "사용자정보", required = true)
		@AuthenticationPrincipal Long userId,
		@Parameter(description = "즐겨찾기 id", required = true)
		@PathVariable Long brandId
	) {
		return CommonResponse.success(brandService.getBrandDetail(userId, brandId));
	}

}
