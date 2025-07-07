package com.ureca.uble.domain.usageHistory.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ureca.uble.domain.auth.dto.response.ReissueRes;
import com.ureca.uble.domain.auth.exception.AuthErrorCode;
import com.ureca.uble.domain.usageHistory.dto.response.UsageHistoryRes;
import com.ureca.uble.domain.usageHistory.service.UsageHistoryService;
import com.ureca.uble.global.dto.response.CursorPageRes;
import com.ureca.uble.global.exception.GlobalException;
import com.ureca.uble.global.response.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UsageHistoryController {
	private final UsageHistoryService usageHistoryService;

	@GetMapping("/history")
	@Operation(summary = "제휴처 이용내역 조회", description = "커서 기반으로 제휴처 이용내역을 조회합니다.")
	public CommonResponse<CursorPageRes<UsageHistoryRes>> getUsageHistory(
		@Parameter(hidden=true)
		@AuthenticationPrincipal Long userId,
		@Parameter(description = "마지막 커서 ID", required = false)
		@RequestParam(required=false) Long lastHistoryId,
		@Parameter(description = "페이지 크기", example = "10")
		@RequestParam(defaultValue="10") int size

	){
		return CommonResponse.success(usageHistoryService.getUsageHistory(userId, lastHistoryId, size));
	}
}
