package com.ureca.uble.domain.feedback.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
@Schema(description = "관리자용 피드백 페이지 응답 DTO")
public class AdminFeedbackRes {

    @Schema(description = "페이징된 피드백 리스트")
    private final List<FeedbackInfo> content;

    @Schema(description = "전체 피드백 건수", example = "157")
    private final long totalCount;

    @Schema(description = "전체 페이지 수", example = "16")
    private final int totalPages;

}
