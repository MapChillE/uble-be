package com.ureca.uble.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GlobalErrorCode implements ResultCode {
    // GLOBAL 1000번대
    SUCCESS(HttpStatus.OK, 0, "정상 처리 되었습니다."),
    ELASTIC_INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 1001, "검색 서버 내부 오류가 발생했습니다."),
    ;

    private final HttpStatus status;
    private final int code;
    private final String message;
}
