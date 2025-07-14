package com.ureca.uble.domain.feedback.exception;

import com.ureca.uble.global.exception.ResultCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FeedbackErrorCode implements ResultCode {
    FEEDBACK_SAVE_FAILED (HttpStatus.INTERNAL_SERVER_ERROR, 7000, "피드백 저장에 실패했습니다.");

    private final HttpStatus status;
    private final int      code;
    private final String   message;
}
