package com.ureca.uble.domain.brand.controller;

import com.ureca.uble.domain.brand.dto.response.BrandDetailRes;
import com.ureca.uble.domain.brand.dto.response.BrandListRes;
import com.ureca.uble.domain.brand.dto.response.InitialDataRes;
import com.ureca.uble.domain.brand.dto.response.SearchBrandListRes;
import com.ureca.uble.domain.brand.service.BrandService;
import com.ureca.uble.entity.enums.BenefitType;
import com.ureca.uble.entity.enums.Season;
import com.ureca.uble.domain.common.dto.response.CommonResponse;
import com.ureca.uble.domain.common.dto.response.CursorPageRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

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
	@GetMapping
	public CommonResponse<CursorPageRes<BrandListRes>> getBrandList(
		@Parameter(description = "사용자정보", required = true)
		@AuthenticationPrincipal Long userId,
		@Parameter(description = "필터링할 카테고리 ID", example = "2")
		@RequestParam(required=false) Long categoryId,
		@Parameter(description = "필터링할 계절", example = "ETC")
		@RequestParam(required=false) Season season,
		@Parameter(description = "필터링할 타입 : VIP 또는 LOCAL", example = "VIP")
		@RequestParam(required=false) BenefitType type,
		@Parameter(description = "마지막 제휴처 ID")
		@RequestParam(required = false) Long lastBrandId,
		@Parameter(description = "한 번에 가져올 크기")
		@RequestParam(defaultValue = "5") int size
	){
		return CommonResponse.success(brandService.getBrandList(userId, categoryId, season, type, lastBrandId, size));
	}

	@Operation(summary = "(검색) 제휴처 브랜드 전체 리스트 조회")
	@GetMapping("/search")
	public CommonResponse<SearchBrandListRes> getBrandListBySearch(
		@Parameter(description = "사용자정보", required = true)
		@AuthenticationPrincipal Long userId,
		@Parameter(description = "검색어", example = "할리스")
		@RequestParam String keyword,
		@Parameter(description = "필터링할 카테고리", example = "2")
		@RequestParam(required=false) String category,
		@Parameter(description = "필터링할 계절", example = "ETC")
		@RequestParam(required=false) Season season,
		@Parameter(description = "필터링할 타입 : VIP 또는 LOCAL", example = "VIP")
		@RequestParam(required=false) BenefitType type,
		@Parameter(description = "페이지")
		@RequestParam(defaultValue = "0") int page,
		@Parameter(description = "한 번에 가져올 크기")
		@RequestParam(defaultValue = "5") int size){
		return CommonResponse.success(brandService.getBrandListBySearch(userId, keyword, category, season, type, page, size));
	}

	@Operation(summary = "지도 초기 데이터 조회")
	@GetMapping("/map/initial-data")
	public CommonResponse<InitialDataRes> getInitialData(
		@Parameter(description = "사용자정보", required = true)
		@AuthenticationPrincipal Long userId) {
		return CommonResponse.success(brandService.getInitialData(userId));
	}
}
