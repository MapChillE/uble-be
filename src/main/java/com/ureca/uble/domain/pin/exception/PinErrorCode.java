package com.ureca.uble.domain.pin.exception;

import com.ureca.uble.global.exception.ResultCode;
import org.springframework.http.HttpStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PinErrorCode implements ResultCode {
    // Pin 8000번대
    PIN_NOT_FOUND(HttpStatus.NOT_FOUND, 8001,"해당 핀을 찾을 수 없습니다."),
    PIN_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, 8000, "최대 2개까지만 등록 가능합니다.");

    private final HttpStatus status;
    private final int code;
    private final String message;
}
