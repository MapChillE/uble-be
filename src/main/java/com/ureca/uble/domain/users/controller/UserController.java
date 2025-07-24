package com.ureca.uble.domain.users.controller;

import com.ureca.uble.domain.common.dto.response.CommonResponse;
import com.ureca.uble.domain.users.dto.request.UpdateUserInfoReq;
import com.ureca.uble.domain.users.dto.response.*;
import com.ureca.uble.domain.users.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@Operation(summary = "사용자 정보 조회", description = "마이페이지에 표시될 사용자 정보를 조회합니다.")
	@GetMapping("/userInfo")
	public CommonResponse<GetUserInfoRes> getUserInfo(
		@Parameter(description = "사용자정보", required = true)
		@AuthenticationPrincipal Long userId
	){
		return CommonResponse.success(userService.getUserInfo(userId));
	}

	@Operation(summary = "사용자 정보 최초 입력 & 수정", description = "사용자 정보를 수정합니다.")
	@PutMapping("/userInfo")
	public CommonResponse<UpdateUserInfoRes>updateUserInfo(
		@Parameter(description = "사용자정보", required = true)
		@AuthenticationPrincipal Long userId,
		@RequestBody UpdateUserInfoReq request
	){
		return CommonResponse.success(userService.updateUserInfo(userId, request));
	}

	@Operation(summary = "제휴처 매장 추천 정보 조회", description = "사용자에게 맞는 제휴처 매장 추천 정보를 조회합니다.")
	@GetMapping("/recommendation")
	public CommonResponse<GetRecommendationListRes>getRecommendations(
		@Parameter(description = "사용자정보", required = true)
		@AuthenticationPrincipal Long userId,
		@Parameter(description = "위도", example = "37.5")
		@RequestParam Double latitude,
		@Parameter(description = "경도", example = "127.04")
		@RequestParam Double longitude
	){
		return CommonResponse.success(userService.getRecommendations(userId, latitude, longitude));
	}

	/**
	 * 비슷한 유저 로그 기반 추천
	 *
	 * @param userId 사용자 정보
	 */
	@Operation(summary = "비슷한 유저 로그 기반 추천", description = "비슷한 유저 로그 기반 추천")
	@GetMapping("recommendation/similar")
	public CommonResponse<GetSimilarUserRecommendationListRes> getSimilarUserRecommendation(
		@Parameter(description = "사용자정보", required = true)
		@AuthenticationPrincipal Long userId) {
		return CommonResponse.success(userService.getSimilarUserRecommendation(userId));
	}

	/**
	 * 시간대 기반 추천
	 *
	 * @param userId 사용자 정보
	 */
	@Operation(summary = "시간대 기반 추천", description = "시간대 기반 추천")
	@GetMapping("recommendation/time")
	public CommonResponse<GetTimeRecommendationListRes> getTimeRecommendation(
		@Parameter(description = "사용자정보", required = true)
		@AuthenticationPrincipal Long userId) {
		return CommonResponse.success(userService.getTimeRecommendation(userId));
	}

	/**
	 * 사용자 통계 미리보기 정보 조회
	 *
	 * @param userId 사용자 정보
	 */
	@Operation(summary = "사용자 통계 정보 미리보기 조회", description = "사용자의 개인 통계 미리보기를 조회합니다.")
	@GetMapping("/statistics/preview")
	public CommonResponse<GetUserStatisticsPreviewRes> getUserStatisticsPreview(
		@Parameter(description = "사용자정보", required = true)
		@AuthenticationPrincipal Long userId) {
		return CommonResponse.success(userService.getUserStatisticsPreview(userId));
	}

	/**
	 * 사용자 통계 정보 조회
	 *
	 * @param userId 사용자 정보
	 */
	@Operation(summary = "사용자 통계 정보 조회", description = "사용자의 개인 통계 데이터를 조회합니다.")
	@GetMapping("/statistics")
	public CommonResponse<GetUserStatisticsRes> getUserStatistics(
		@Parameter(description = "사용자정보", required = true)
		@AuthenticationPrincipal Long userId) {
		return CommonResponse.success(userService.getUserStatistics(userId));
	}
}
