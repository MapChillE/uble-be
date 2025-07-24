package com.ureca.uble.domain.users.controller;

import com.ureca.uble.domain.common.dto.response.CommonResponse;
import com.ureca.uble.domain.users.dto.request.CreateUsageHistoryReq;
import com.ureca.uble.domain.users.dto.response.CreateUsageHistoryRes;
import com.ureca.uble.domain.users.dto.response.UsageHistoryListRes;
import com.ureca.uble.domain.users.service.UsageHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UsageHistoryController {
	private final UsageHistoryService usageHistoryService;

	@GetMapping("/history")
	@Operation(summary = "제휴처 이용내역 조회", description = "커서 기반으로 제휴처 이용내역을 조회합니다.")
	public CommonResponse<UsageHistoryListRes> getUsageHistory(
		@Parameter(hidden=true)
		@AuthenticationPrincipal Long userId,
		@Parameter(description = "이용 연도", example = "2025")
		@RequestParam int year,
		@Parameter(description = "이용 월", example = "7")
		@RequestParam int month,
		@Parameter(description = "페이지 번호 (0부터 시작)")
		@RequestParam(defaultValue = "0") int page,
		@Parameter(description = "페이지 크기", example = "10")
		@RequestParam(defaultValue="10") int size) {
		return CommonResponse.success(usageHistoryService.getUsageHistory(userId, year, month, page, size));
	}

	/**
	 * 이용 내역 추가
	 *
	 * @param userId 사용자 정보
	 * @param storeId 매장 id
	 */
	@PostMapping("/stores/{storeId}/usages")
	@Operation(summary = "제휴처 이용 내역 등록", description = "제휴처 이용 내역을 추가합니다.")
	public CommonResponse<CreateUsageHistoryRes> createUsageHistory(
		@Parameter(description = "사용자정보", required = true)
		@AuthenticationPrincipal Long userId,
		@Parameter(description = "매장 id", required = true)
		@PathVariable Long storeId,
		@Parameter(description = "VIP 혜택 사용 여부", required = true)
		@RequestBody CreateUsageHistoryReq req) {
		return CommonResponse.success(usageHistoryService.createUsageHistory(userId, storeId, req));
	}
}
