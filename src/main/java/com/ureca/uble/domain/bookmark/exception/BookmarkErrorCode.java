package com.ureca.uble.domain.bookmark.exception;

import com.ureca.uble.global.exception.ResultCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BookmarkErrorCode implements ResultCode {
    BOOKMARK_NOT_FOUND(HttpStatus.NOT_FOUND, 6000, "즐겨찾기를 찾을 수 없습니다."),
    DUPLICATED_BOOKMARK(HttpStatus.BAD_REQUEST, 6001, "이미 존재하는 즐겨찾기입니다."),
    ;

    private final HttpStatus status;
    private final int code;
    private final String message;
}
