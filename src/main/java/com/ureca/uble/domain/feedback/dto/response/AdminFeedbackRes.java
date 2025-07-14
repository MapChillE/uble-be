package com.ureca.uble.domain.feedback.dto.response;

import com.ureca.uble.entity.Feedback;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 단일 피드백 항목 응답 DTO
 */
@Getter
@Builder
@AllArgsConstructor
@Schema(description = "관리자용 피드백 페이지 응답 DTO")
public class AdminFeedbackRes {

    @Schema(description = "페이징된 피드백 리스트")
    private final List<FeedbackItem> content;

    @Schema(description = "전체 피드백 건수", example = "157")
    private final long totalCount;

    @Schema(description = "전체 페이지 수", example = "16")
    private final int totalPages;

    public static AdminFeedbackRes from(Page<Feedback> page) {
        List<FeedbackItem> items = page.getContent().stream()
                .map(f -> FeedbackItem.builder()
                        .title(f.getTitle())
                        .content(f.getContent())
                        .score(f.getScore())
                        .createdAt(f.getCreatedAt())
                        .nickname(f.getUser().getNickname())
                        .build()
                ).toList();

        return AdminFeedbackRes.builder()
                .content(items)
                .totalCount(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }
    @Getter
    @Builder
    @AllArgsConstructor
    @Schema(description = "단일 피드백 항목")
    public static class FeedbackItem {
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

        public static FeedbackItem of(Feedback fee){
            return FeedbackItem.builder()
                    .title(fee.getTitle())
                    .content(fee.getContent())
                    .score(fee.getScore())
                    .createdAt(fee.getCreatedAt())
                    .nickname(fee.getUser().getNickname())
                    .build();
        }
    }
}