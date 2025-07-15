package com.ureca.uble.domain.feedback.dto.response;

import com.ureca.uble.entity.Feedback;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "단일 피드백 항목")
public class FeedbackInfo {
    @Schema(description = "피드백 제목", example = "UI 개선 요청")
    private final String title;

    @Schema(description = "피드백 내용", example = "지도에서 핀을 더 크게 해주세요.")
    private final String content;

    @Schema(description = "평점 (1~5)", example = "4")
    private final int score;

    @Schema(description = "생성 일시 (ISO 형식)", example = "2025-07-13T14:22:31")
    private final LocalDateTime createdAt;

    @Schema(description = "사용자 닉네임", example = "오잉")
    private final String nickname;

    public static FeedbackInfo of(Feedback fee) {
        return FeedbackInfo.builder()
                .title(fee.getTitle())
                .content(fee.getContent())
                .score(fee.getScore())
                .createdAt(fee.getCreatedAt())
                .nickname(fee.getUser().getNickname())
                .build();
    }
}
