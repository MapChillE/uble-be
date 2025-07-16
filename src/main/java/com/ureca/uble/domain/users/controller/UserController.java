package com.ureca.uble.domain.users.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ureca.uble.domain.users.dto.request.UpdateUserInfoReq;
import com.ureca.uble.domain.users.dto.response.GetUserInfoRes;
import com.ureca.uble.domain.users.dto.response.UpdateUserInfoRes;
import com.ureca.uble.domain.users.service.UserService;
import com.ureca.uble.domain.common.dto.response.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

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
}
