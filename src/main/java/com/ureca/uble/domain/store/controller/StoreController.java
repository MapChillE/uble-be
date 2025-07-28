package com.ureca.uble.domain.store.controller;

import com.ureca.uble.domain.common.dto.response.CommonResponse;
import com.ureca.uble.domain.store.dto.response.GetGlobalSuggestionListRes;
import com.ureca.uble.domain.store.dto.response.GetStoreDetailRes;
import com.ureca.uble.domain.store.dto.response.GetStoreListRes;
import com.ureca.uble.domain.store.dto.response.GetStoreSummaryRes;
import com.ureca.uble.domain.store.service.StoreService;
import com.ureca.uble.entity.enums.BenefitType;
import com.ureca.uble.entity.enums.Season;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    /**
     * 사각형 범위 내 매장 정보 조회
     * @param zoomLevel 요청한 줌 레벨
     * @param swLat     남서쪽 위도 (south-west latitude)
     * @param swLng     남서쪽 경도 (south-west longitude)
     * @param neLat     북동쪽 위도 (north-east latitude)
     * @param neLng     북동쪽 경도 (north-east longitude)
     * @param categoryId 카테고리 id 필터링
     * @param brandId   제휴처 id 필터링
     * @param season    계절 필터링
     * @param type      혜택 타입 필터링(POINT, DISCOUNT 등)
     */
    @Operation(summary = "사각형 범위 내 매장 정보 조회", description = "Bounding Box로 주변 매장 조회")
    @GetMapping
    public CommonResponse<GetStoreListRes> getStores(
            @Parameter(description = "요청한 줌 레벨", required = true)
            @RequestParam int zoomLevel,
            @Parameter(description = "남서쪽 위도", required = true)
            @RequestParam double swLat,
            @Parameter(description = "남서쪽 경도", required = true)
            @RequestParam double swLng,
            @Parameter(description = "북동쪽 위도", required = true)
            @RequestParam double neLat,
            @Parameter(description = "북동쪽 경도", required = true)
            @RequestParam double neLng,
            @Parameter(description = "카테고리 id 필터링")
            @RequestParam(required = false) Long categoryId,
            @Parameter(description = "제휴처 id 필터링")
            @RequestParam(required = false) Long brandId,
            @Parameter(description = "계절 필터링")
            @RequestParam(required = false) Season season,
            @Parameter(description = "혜택 타입 필터링(POINT, DISCOUNT 등)")
            @RequestParam(required = false) BenefitType type) {
        return CommonResponse.success(storeService.getStores(zoomLevel, swLat, swLng, neLat, neLng, categoryId, brandId, season, type));
    }

    /**
     * 매장 소모달 정보 조회
     *
     * @param latitude 위도
     * @param longitude 경도
     * @param userId 사용자 정보
     * @param storeId 매장 id
     */
    @Operation(summary = "매장 소모달 정보 조회", description = "매장 소모달 정보 조회")
    @GetMapping("summary/{storeId}")
    public CommonResponse<GetStoreSummaryRes> getStoreSummary(
        @Parameter(description = "위도", required = true)
        @RequestParam double latitude,
        @Parameter(description = "경도", required = true)
        @RequestParam double longitude,
        @Parameter(description = "사용자정보", required = true)
        @AuthenticationPrincipal Long userId,
        @Parameter(description = "매장 id", required = true)
        @PathVariable Long storeId) {
        return CommonResponse.success(storeService.getStoreSummary(latitude, longitude, userId, storeId));
    }

    /**
     * 매장 상세 정보 조회
     *
     * @param latitude 위도
     * @param longitude 경도
     * @param userId 사용자 정보
     * @param storeId 매장 id
     */
    @Operation(summary = "매장 상세 정보 조회", description = "매장 상세 정보 조회")
    @GetMapping("/{storeId}")
    public CommonResponse<GetStoreDetailRes> getStoreDetail(
        @Parameter(description = "위도", required = true)
        @RequestParam double latitude,
        @Parameter(description = "경도", required = true)
        @RequestParam double longitude,
        @Parameter(description = "사용자정보", required = true)
        @AuthenticationPrincipal Long userId,
        @Parameter(description = "매장 id", required = true)
        @PathVariable Long storeId) {
        return CommonResponse.success(storeService.getStoreDetail(latitude, longitude, userId, storeId));
    }

    /**
     * (자동완성) 지도 전체 검색 자동완성
     *
     * @param latitude 위도
     * @param longitude 경도
     * @param size 결과 크기
     */
    @Operation(summary = "(자동완성) 지도 전체 검색 자동완성", description = "(자동완성) 지도 전체 검색 자동완성")
    @GetMapping("/suggestions")
    public CommonResponse<GetGlobalSuggestionListRes> getGlobalSuggestionList(
        @Parameter(description = "검색어", required = true)
        @RequestParam String keyword,
        @Parameter(description = "위도", required = true)
        @RequestParam double latitude,
        @Parameter(description = "경도", required = true)
        @RequestParam double longitude,
        @Parameter(description = "한번에 가져올 크기")
        @RequestParam(defaultValue = "10") int size) {
        return CommonResponse.success(storeService.getGlobalSuggestionList(keyword, latitude, longitude, size));
    }
}
