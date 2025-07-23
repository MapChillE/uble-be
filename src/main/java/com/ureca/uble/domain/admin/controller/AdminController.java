package com.ureca.uble.domain.admin.controller;

import com.ureca.uble.domain.admin.dto.response.GetUsageRankListRes;
import com.ureca.uble.domain.admin.service.AdminService;
import com.ureca.uble.domain.common.dto.response.CommonResponse;
import com.ureca.uble.entity.enums.BenefitType;
import com.ureca.uble.entity.enums.Gender;
import com.ureca.uble.entity.enums.Rank;
import com.ureca.uble.entity.enums.RankTarget;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    /**
     * (통계) 제휴처/카테고리 이용 순위
     *
     * @param rankTarget 통계 낼 대상
     * @param gender 성별
     * @param ageRange 나이대
     * @param rank 사용자 등급
     * @param benefitType 혜택 타입
     */
    @Operation(summary = "(통계) 제휴처/카테고리 이용 순위", description = "성별/나이/유저등급/혜택타입에 따른 제휴처/카테고리 이용 순위")
    @GetMapping("/statistics/rank/usage")
    public CommonResponse<GetUsageRankListRes> getUsageRank (
        @Parameter(description = "통계 낼 대상")
        @RequestParam RankTarget rankTarget,
        @Parameter(description = "성별")
        @RequestParam(required = false) Gender gender,
        @Parameter(description = "나이대 (10 단위)")
        @RequestParam(required = false) Integer ageRange,
        @Parameter(description = "사용자 등급 (NONE 제외)")
        @RequestParam(required = false) Rank rank,
        @Parameter(description = "혜택 타입 ")
        @RequestParam(required = false) BenefitType benefitType) {
        return CommonResponse.success(adminService.getUsageRank(rankTarget, gender, ageRange, rank, benefitType));
    }
}
