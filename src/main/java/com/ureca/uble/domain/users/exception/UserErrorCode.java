package com.ureca.uble.domain.users.exception;

import com.ureca.uble.global.exception.ResultCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ResultCode {
    // 사용자 2000번대
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, 2000, "사용자를 찾을 수 없습니다."),
    BENEFIT_NOT_AVAILABLE(HttpStatus.CONFLICT, 2001, "혜택을 사용할 수 없습니다."),
	USER_ALREADY_DELETED(HttpStatus.BAD_REQUEST, 2002, "이미 삭제된 사용자입니다."),
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, 2003, "요청 파라미터가 유효하지 않습니다."),
    EXTERNAL_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 2004, "외부 API 호출에 실패했습니다."),
    RECOMMENDATION_NOT_FOUND(HttpStatus.NOT_FOUND, 2005, "추천 결과를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final int code;
    private final String message;
}
