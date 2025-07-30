package com.ureca.uble.domain.admin.exception;

import org.springframework.http.HttpStatus;

import com.ureca.uble.global.exception.ResultCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AdminErrorCode implements ResultCode {
	NOT_ADMIN(HttpStatus.FORBIDDEN, 1, "관리자 권한이 없습니다."),
	INVALID_ADMIN_CODE(HttpStatus.UNAUTHORIZED, 2, "올바르지 않은 관리자 코드입니다.");

	private final HttpStatus status;
	private final int code;
	private final String message;
}
