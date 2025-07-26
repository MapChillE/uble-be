package com.ureca.uble.domain.feedback.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ureca.uble.domain.common.dto.response.CommonResponse;
import com.ureca.uble.domain.feedback.dto.request.CreateFeedbackReq;
import com.ureca.uble.domain.feedback.dto.response.AdminFeedbackRes;
import com.ureca.uble.domain.feedback.dto.response.CreateFeedbackRes;
import com.ureca.uble.domain.feedback.service.FeedbackService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    /**
     * 서비스 피드백 등록
     *
     * @param userId 로그인한 사용자의 ID
     * @param req       제목(title), 내용(content), 별점(score) 필드를 담은 요청 객체
     * @return 등록된 피드백 ID를 담은 CommonResponse
     */
    @Operation(summary = "서비스 피드백 등록", description = "사용자가 제목·내용·평점을 입력해 피드백을 남깁니다.")
    @PostMapping("/users/feedback")
    public CommonResponse<CreateFeedbackRes> createFeedback(
            @Parameter(description = "사용자정보", required = true)
            @AuthenticationPrincipal Long userId,
            @Parameter(description = "제목(title), 내용(content), 별점1~5(score)을 담은 요청 객체", required = true)
            @Valid @RequestBody CreateFeedbackReq req
    ) {
        CreateFeedbackRes res = feedbackService.createFeedback(userId, req);
        return CommonResponse.success(res);
    }

    @Operation(summary = "관리자: 사용자 피드백 조회", description = "생성일(createdAt) 내림차순으로 피드백 목록을 페이지 단위로 조회합니다.")
    @GetMapping("/admin/feedback")
    public CommonResponse<AdminFeedbackRes> getFeedbacks(
            @ParameterObject
            @PageableDefault(sort="id", direction=Sort.Direction.DESC) Pageable pageable
    )
    {
        AdminFeedbackRes result = feedbackService.getFeedbacks(pageable);
        return CommonResponse.success(result);
    }
}
