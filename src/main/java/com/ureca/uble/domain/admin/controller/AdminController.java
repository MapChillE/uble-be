package com.ureca.uble.domain.admin.controller;

import com.ureca.uble.domain.admin.dto.response.*;
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
@RequestMapping("/api/admin")
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

    /**
     * (통계) 제휴처/카테고리 클릭 순위
     *
     * @param rankTarget 통계 낼 대상
     * @param gender 성별
     * @param ageRange 나이대
     * @param rank 사용자 등급
     * @param benefitType 혜택 타입
     */
    @Operation(summary = "(통계) 제휴처/카테고리 클릭 순위", description = "성별/나이/유저등급/혜택타입에 따른 제휴처/카테고리 클릭 순위")
    @GetMapping("/statistics/rank/click")
    public CommonResponse<GetClickRankListRes> getClickRank (
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
        return CommonResponse.success(adminService.getClickRank(rankTarget, gender, ageRange, rank, benefitType));
    }

    /**
     * (통계) 서울 지역구 이용 순위
     *
     * @param gender 성별
     * @param ageRange 나이대
     * @param rank 사용자 등급
     * @param benefitType 혜택 타입
     */
    @Operation(summary = "(통계) 서울 지역구 이용 순위", description = "성별/나이/유저등급/혜택타입에 따른 서울 지역구 이용 순위")
    @GetMapping("/statistics/rank/local")
    public CommonResponse<GetLocalRankListRes> getLocalRank (
        @Parameter(description = "성별")
        @RequestParam(required = false) Gender gender,
        @Parameter(description = "나이대 (10 단위)")
        @RequestParam(required = false) Integer ageRange,
        @Parameter(description = "사용자 등급 (NONE 제외)")
        @RequestParam(required = false) Rank rank,
        @Parameter(description = "혜택 타입 ")
        @RequestParam(required = false) BenefitType benefitType) {
        return CommonResponse.success(adminService.getLocalRank(gender, ageRange, rank, benefitType));
    }

    /**
     * (통계) 일별 인기 검색어 순위
     *
     * @param gender 성별
     * @param ageRange 나이대
     * @param rank 사용자 등급
     * @param benefitType 혜택 타입
     */
    @Operation(summary = "(통계) 일별 인기 검색어 순위", description = "성별/나이/유저등급/혜택타입에 따른 일별 인기 검색어 순위 (7일)")
    @GetMapping("/statistics/rank/keywords/daily-top")
    public CommonResponse<GetDailySearchRankListRes> getDailySearchRank (
        @Parameter(description = "성별")
        @RequestParam(required = false) Gender gender,
        @Parameter(description = "나이대 (10 단위)")
        @RequestParam(required = false) Integer ageRange,
        @Parameter(description = "사용자 등급 (NONE 제외)")
        @RequestParam(required = false) Rank rank,
        @Parameter(description = "혜택 타입 ")
        @RequestParam(required = false) BenefitType benefitType) {
        return CommonResponse.success(adminService.getDailySearchRank(gender, ageRange, rank, benefitType));
    }

    /**
     * (통계) 결과 미포함 검색어 순위
     *
     * @param gender 성별
     * @param ageRange 나이대
     * @param rank 사용자 등급
     * @param benefitType 혜택 타입
     */
    @Operation(summary = "(통계) 결과 미포함 검색어 순위", description = "성별/나이/유저등급/혜택타입에 따른 결과 미포함 검색어 순위")
    @GetMapping("/statistics/rank/keywords/empty-top")
    public CommonResponse<GetEmptySearchRankListRes> getEmptySearchRank (
        @Parameter(description = "성별")
        @RequestParam(required = false) Gender gender,
        @Parameter(description = "나이대 (10 단위)")
        @RequestParam(required = false) Integer ageRange,
        @Parameter(description = "사용자 등급 (NONE 제외)")
        @RequestParam(required = false) Rank rank,
        @Parameter(description = "혜택 타입 ")
        @RequestParam(required = false) BenefitType benefitType) {
        return CommonResponse.success(adminService.getEmptySearchRank(gender, ageRange, rank, benefitType));
    }

    /**
     * (통계) 관심사 변화 추이 (top 10)
     *
     * @param gender 성별
     * @param ageRange 나이대
     * @param rank 사용자 등급
     * @param benefitType 혜택 타입
     */
    @Operation(summary = "(통계) 상위 10개 제휴처 대상 관심사 변화 추이", description = "성별/나이/유저등급/혜택타입에 따른 관심사 변화 추이 (상위 10개 제휴처 대상)")
    @GetMapping("/statistics/rank/asdfasdf") // TODO: 이름바꾸기
    public CommonResponse<GetInterestChangeRes> getInterestChange (
        @Parameter(description = "성별")
        @RequestParam(required = false) Gender gender,
        @Parameter(description = "나이대 (10 단위)")
        @RequestParam(required = false) Integer ageRange,
        @Parameter(description = "사용자 등급 (NONE 제외)")
        @RequestParam(required = false) Rank rank,
        @Parameter(description = "혜택 타입 ")
        @RequestParam(required = false) BenefitType benefitType) {
        return CommonResponse.success(adminService.getInterestChange(gender, ageRange, rank, benefitType));
    }
}
