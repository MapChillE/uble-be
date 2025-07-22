package com.ureca.uble.domain.brand.controller;

import com.ureca.uble.domain.brand.dto.response.*;
import com.ureca.uble.domain.brand.service.BrandService;
import com.ureca.uble.domain.common.dto.response.CommonResponse;
import com.ureca.uble.domain.common.dto.response.CursorPageRes;
import com.ureca.uble.entity.enums.BenefitType;
import com.ureca.uble.entity.enums.Season;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

	/**
	 * 제휴처 검색 자동완성
	 *
	 * @param keyword 검색어
	 */
	@Operation(summary = "(자동완성) 제휴처 전체 검색 자동완성", description = "(자동완성) 제휴처 전체 검색 자동완성")
	@GetMapping("/suggestions")
	public CommonResponse<BrandSuggestionListRes> getBrandSuggestionList(
		@Parameter(description = "검색어", required = true)
		@RequestParam String keyword,
		@Parameter(description = "한 번에 가져올 크기")
		@RequestParam(defaultValue = "5") int size){
		return CommonResponse.success(brandService.getBrandSuggestionList(keyword, size));
	}

	@Operation(summary = "오프라인 브랜드 이름+이미지 리스트 (커서 페이징)")
	@GetMapping("/names")
	public CommonResponse<CursorPageRes<OfflineBrandRes>> getOfflineBrands(
			@Parameter(description = "이전 호출의 마지막 브랜드 ID", example = "50")
			@RequestParam(required = false) Long lastBrandId,
			@Parameter(description = "한 번에 가져올 개수", example = "20")
			@RequestParam(defaultValue = "20") int size) {
		return CommonResponse.success(brandService.getOfflineBrands(lastBrandId, size));
	}
}
