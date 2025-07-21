package com.ureca.uble.domain.store.controller;

import com.ureca.uble.domain.store.dto.response.GetStoreDetailRes;
import com.ureca.uble.domain.store.dto.response.GetStoreListRes;
import com.ureca.uble.domain.store.service.StoreService;
import com.ureca.uble.entity.enums.BenefitType;
import com.ureca.uble.entity.enums.Season;
import com.ureca.uble.domain.common.dto.response.CommonResponse;
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
     * 근처 매장 정보 조회
     *
     * @param latitude 위도
     * @param longitude 경도
     * @param distance 거리
     * @param categoryId 카테고리 id
     * @param brandId 제휴처 id
     * @param season 계절 정보
     * @param type 혜택 종류 정보
     */
    @Operation(summary = "근처 매장 정보 조회", description = "근처 매장 정보 조회")
    @GetMapping
    public CommonResponse<GetStoreListRes> getStores(
        @Parameter(description = "위도", required = true)
        @RequestParam double latitude,
        @Parameter(description = "경도", required = true)
        @RequestParam double longitude,
        @Parameter(description = "거리 반경", required = true)
        @RequestParam(defaultValue = "500") int distance,
        @Parameter(description = "카테고리 id 필터링")
        @RequestParam(required = false) Long categoryId,
        @Parameter(description = "제휴처 id 필터링")
        @RequestParam(required = false) Long brandId,
        @Parameter(description = "계절 필터링")
        @RequestParam(required = false) Season season,
        @Parameter(description = "혜택 타입 필터링(LOCAL/VIP)")
        @RequestParam(required = false) BenefitType type) {
        return CommonResponse.success(storeService.getStores(latitude, longitude, distance, categoryId, brandId, season, type));
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
}
