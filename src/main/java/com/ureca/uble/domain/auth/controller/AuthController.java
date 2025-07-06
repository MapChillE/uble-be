package com.ureca.uble.domain.auth.controller;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ureca.uble.domain.auth.dto.request.KakaoLoginReq;
import com.ureca.uble.domain.auth.dto.response.KakaoLoginRes;
import com.ureca.uble.domain.auth.dto.response.LogoutRes;
import com.ureca.uble.domain.auth.dto.response.ReissueRes;
import com.ureca.uble.domain.auth.exception.AuthErrorCode;
import com.ureca.uble.domain.auth.service.AuthService;
import com.ureca.uble.entity.User;
import com.ureca.uble.global.exception.GlobalException;
import com.ureca.uble.global.response.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/login")
	@Operation(summary = "카카오 로그인", description = "카카오 인가코드를 받아 로그인합니다.")
	public CommonResponse<KakaoLoginRes> login(@RequestBody KakaoLoginReq request, HttpServletResponse response){
		User user = authService.login(request.getCode(), response);
		KakaoLoginRes result = new KakaoLoginRes(user.getRole().name());
		return CommonResponse.success(result);
	}

	@PostMapping("/reissue")
	@Operation(summary = "토큰 재발급", description = "Refresh Token을 통해 새로운 Access Token을 발급합니다.")
	public CommonResponse<ReissueRes> reissue(@CookieValue(value = "refreshToken", required = false) String refreshToken, HttpServletResponse response){
		if (refreshToken == null) {
			throw new GlobalException(AuthErrorCode.REFRESH_TOKEN_NOT_FOUND);
		}
		authService.reissue(refreshToken, response);
		return CommonResponse.success(new ReissueRes());
	}

	@PostMapping("/logout")
	@Operation(summary = "로그아웃", description = "Refresh Token을 만료시키고 로그아웃합니다.")
	public CommonResponse<LogoutRes> logout(@CookieValue(value = "refreshToken", required = false) String refreshToken, HttpServletResponse response){
		if (refreshToken == null) {
			throw new GlobalException(AuthErrorCode.REFRESH_TOKEN_NOT_FOUND);
		}
		authService.logout(refreshToken, response);
		return CommonResponse.success(new LogoutRes());
	}

}
