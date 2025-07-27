package com.ureca.uble.domain.feedback.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "서비스 피드백 생성 요청 DTO")
public class CreateFeedbackReq {

    @Schema(description = "피드백 제목 (100자 이내)", example = "앱 사용성이 좋습니다.")
    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 100)
    private String title;

    @Schema(description = "피드백 내용 (200자 이내)", example = "지도 기능이 직관적이라 너무 편리해요!")
    @NotBlank(message = "내용은 필수입니다.")
    @Size(max = 200)
    private String content;

    @Schema(description = "평점 (1~5)", example = "5")
    @Min(value = 1)
    @Max(value = 5)
    private int score;
}
