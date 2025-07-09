package com.ureca.uble.domain.brand.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ureca.uble.domain.brand.dto.response.BrandDetailRes;
import com.ureca.uble.domain.brand.dto.response.BrandListRes;
import com.ureca.uble.domain.brand.service.BrandService;
import com.ureca.uble.entity.enums.Season;
import com.ureca.uble.global.response.CommonResponse;
import com.ureca.uble.global.response.CursorPageRes;

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
		@Parameter(description = "제휴처 id", required = true)
		@PathVariable Long brandId
	) {
		return CommonResponse.success(brandService.getBrandDetail(userId, brandId));
	}

	@Operation(summary = "제휴처 브랜드 전체 리스트 조회")
	@GetMapping("/search")
	public CommonResponse<CursorPageRes<BrandListRes>> getBrandList(
		@Parameter(description = "사용자정보", required = true)
		@AuthenticationPrincipal Long userId,
		@Parameter(description = "필터링할 카테고리 ID", example = "2")
		@RequestParam(required=false) Long categoryId,
		@Parameter(description = "필터링할 계절", example = "ETC")
		@RequestParam(required=false) Season season,
		@Parameter(description = "우리동네멤버십 여부", example = "true")
		@RequestParam(required=false) Boolean isLocal,
		@Parameter(description = "마지막 제휴처 ID")
		@RequestParam(required = false) Long lastBrandId,
		@Parameter(description = "한 번에 가져올 크기")
		@RequestParam(defaultValue = "5") int size
	){
		return CommonResponse.success(brandService.getBrandList(userId, categoryId, season, isLocal, lastBrandId, size));
	}

}
