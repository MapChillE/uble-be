package com.ureca.uble.domain.brand.exception;

import com.ureca.uble.global.exception.ResultCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BrandErrorCode implements ResultCode {
    BRAND_NOT_FOUND(HttpStatus.NOT_FOUND, 4000, "제휴처를 찾을 수 없습니다."),
    BENEFIT_NOT_FOUND(HttpStatus.NOT_FOUND, 4001, "혜택 정보를 찾을 수 없습니다."),
    ;

    private final HttpStatus status;
    private final int code;
    private final String message;
}
