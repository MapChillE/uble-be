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
    ;

    private final HttpStatus status;
    private final int code;
    private final String message;
}
