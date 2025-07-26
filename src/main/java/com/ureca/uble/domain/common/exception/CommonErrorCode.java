package com.ureca.uble.domain.common.exception;

import org.springframework.http.HttpStatus;

import com.ureca.uble.global.exception.ResultCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ResultCode {
	// COMMON 9000번대
	ELASTIC_INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 9000, "검색 서버 내부 오류가 발생했습니다."),
	;

	private final HttpStatus status;
	private final int code;
	private final String message;
}
