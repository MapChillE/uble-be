package com.ureca.uble.domain.store.controller;

import com.ureca.uble.domain.store.dto.response.GetStoreListRes;
import com.ureca.uble.domain.store.service.StoreService;
import com.ureca.uble.entity.enums.Season;
import com.ureca.uble.global.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
     * @param season 계절 정보
     * @param isLocal 우리 동네 여부
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
        @Parameter(description = "우리 동네 필터링")
        @RequestParam(required = false) Boolean isLocal) {
        return CommonResponse.success(storeService.getStores(latitude, longitude, distance, categoryId, brandId, season, isLocal));
    }

}
