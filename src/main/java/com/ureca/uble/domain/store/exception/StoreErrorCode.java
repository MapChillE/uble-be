package com.ureca.uble.domain.store.exception;

import com.ureca.uble.global.exception.ResultCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum StoreErrorCode implements ResultCode {
    STORE_NOT_FOUND(HttpStatus.NOT_FOUND, 5000, "매장 정보를 찾을 수 없습니다."),
    ;

    private final HttpStatus status;
    private final int code;
    private final String message;
}
