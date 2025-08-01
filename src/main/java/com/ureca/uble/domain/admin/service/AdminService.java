package com.ureca.uble.domain.admin.service;

import static com.ureca.uble.domain.common.exception.CommonErrorCode.*;
import static com.ureca.uble.entity.enums.InterestType.*;

import java.security.MessageDigest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.stereotype.Service;

import com.ureca.uble.domain.admin.dto.response.AdminCodeRes;
import com.ureca.uble.domain.admin.dto.response.GetClickRankListRes;
import com.ureca.uble.domain.admin.dto.response.GetDailySearchRankListRes;
import com.ureca.uble.domain.admin.dto.response.GetEmptySearchRankListRes;
import com.ureca.uble.domain.admin.dto.response.GetInterestChangeRes;
import com.ureca.uble.domain.admin.dto.response.GetInterestListRes;
import com.ureca.uble.domain.admin.dto.response.GetLocalRankListRes;
import com.ureca.uble.domain.admin.dto.response.GetRankListRes;
import com.ureca.uble.domain.admin.dto.response.GetUsageRankListRes;
import com.ureca.uble.domain.admin.dto.response.InterestDetailRes;
import com.ureca.uble.domain.admin.dto.response.RankDetailRes;
import com.ureca.uble.domain.admin.exception.AdminErrorCode;
import com.ureca.uble.domain.brand.repository.BrandClickLogDocumentRepository;
import com.ureca.uble.domain.common.repository.CustomSuggestionRepository;
import com.ureca.uble.domain.store.repository.SearchLogDocumentRepository;
import com.ureca.uble.domain.users.exception.UserErrorCode;
import com.ureca.uble.domain.users.repository.UsageHistoryDocumentRepository;
import com.ureca.uble.domain.users.repository.UserRepository;
import com.ureca.uble.entity.User;
import com.ureca.uble.entity.enums.BenefitType;
import com.ureca.uble.entity.enums.Gender;
import com.ureca.uble.entity.enums.InterestType;
import com.ureca.uble.entity.enums.Rank;
import com.ureca.uble.entity.enums.RankTarget;
import com.ureca.uble.entity.enums.Role;
import com.ureca.uble.global.exception.GlobalException;
import com.ureca.uble.global.security.jwt.JwtProvider;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    @Value("${admin.code}")
    private String adminCode;

    private final UsageHistoryDocumentRepository usageHistoryDocumentRepository;
    private final BrandClickLogDocumentRepository brandClickLogDocumentRepository;
    private final SearchLogDocumentRepository searchLogDocumentRepository;
    private final CustomSuggestionRepository customSuggestionRepository;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    /**
     * Admin 인증
     */
    public AdminCodeRes verifyAdmin(Long userId, String providedCode, HttpServletResponse response){
        User user = findUser(userId);

        if(user.getRole() != Role.ADMIN){
            throw new GlobalException(AdminErrorCode.NOT_ADMIN);
        }

        if(!MessageDigest.isEqual(providedCode.getBytes(), adminCode.getBytes())){
            throw new GlobalException(AdminErrorCode.INVALID_ADMIN_CODE);
        }

        String accessToken = jwtProvider.createAccessToken(user);
        String refreshToken = jwtProvider.createRefreshToken(user);

        jwtProvider.addAccessTokenHeader(response, accessToken);
        jwtProvider.addRefreshTokenCookie(response, refreshToken);

        return new AdminCodeRes();
    }

    /**
     * (통계) 제휴처/카테고리 이용 순위
     */
    public GetUsageRankListRes getUsageRank(RankTarget rankTarget, Gender gender, Integer ageRange, Rank rank, BenefitType benefitType) {
        ElasticsearchAggregations rankResult = usageHistoryDocumentRepository.getUsageRankByFiltering(rankTarget, gender, ageRange, rank, benefitType);

        List<RankDetailRes> rankList = rankResult.aggregationsAsMap()
            .get("usage_rank").aggregation().getAggregate().filter().aggregations()
            .get("rank").sterms().buckets().array().stream()
            .map(b -> RankDetailRes.of(b.key().stringValue(), b.docCount()))
            .toList();

        return GetUsageRankListRes.of(rankTarget, rankList);
    }

    /**
     * (통계) 제휴처/카테고리 클릭 순위
     */
    public GetClickRankListRes getClickRank(RankTarget rankTarget, Gender gender, Integer ageRange, Rank rank, BenefitType benefitType) {
        ElasticsearchAggregations rankResult = brandClickLogDocumentRepository.getClickRankByFiltering(rankTarget, gender, ageRange, rank, benefitType);

        List<RankDetailRes> rankList = rankResult.aggregationsAsMap()
            .get("click_rank").aggregation().getAggregate().filter().aggregations()
            .get("rank").sterms().buckets().array().stream()
            .map(b -> RankDetailRes.of(b.key().stringValue(), b.docCount()))
            .toList();

        return GetClickRankListRes.of(rankTarget, rankList);
    }

    /**
     * (통계) 서울 지역구 이용 순위
     */
    public GetLocalRankListRes getLocalRank(Gender gender, Integer ageRange, Rank rank, BenefitType benefitType) {
        ElasticsearchAggregations rankResult = usageHistoryDocumentRepository.getLocalRankByFiltering(gender, ageRange, rank, benefitType);

        List<RankDetailRes> rankList = rankResult.aggregationsAsMap()
            .get("local_rank").aggregation().getAggregate().filter().aggregations()
            .get("rank").sterms().buckets().array().stream()
            .map(b -> RankDetailRes.of(b.key().stringValue(), b.docCount()))
            .toList();

        return new GetLocalRankListRes(rankList);
    }

    /**
     * (통계) 일별 인기 검색어 순위
     */
    public GetDailySearchRankListRes getDailySearchRank(Gender gender, Integer ageRange, Rank rank, BenefitType benefitType) {
        ElasticsearchAggregations rankResult = searchLogDocumentRepository.getPopularSearchRankByFiltering(gender, ageRange, rank, benefitType);

        List<GetRankListRes> rankList = rankResult.aggregationsAsMap()
            .get("daily_top_keywords").aggregation().getAggregate().filter().aggregations()
            .get("daily_top10").dateHistogram().buckets().array().stream()
                .map(bucket -> GetRankListRes.of(
                    LocalDate.parse(bucket.keyAsString()),
                    bucket.aggregations()
                        .get("rank").sterms()
                        .buckets().array().stream()
                        .map(b -> RankDetailRes.of(
                            b.key().stringValue(),
                            b.docCount()
                        ))
                        .toList()
                ))
            .toList();

        return new GetDailySearchRankListRes(rankList);
    }

    /**
     * (통계) 결과 미포함 검색어 순위
     */
    public GetEmptySearchRankListRes getEmptySearchRank(Gender gender, Integer ageRange, Rank rank, BenefitType benefitType) {
        ElasticsearchAggregations rankResult = searchLogDocumentRepository.getEmptySearchRankByFiltering(gender, ageRange, rank, benefitType);

        List<RankDetailRes> rankList = rankResult.aggregationsAsMap()
            .get("empty_top_keywords").aggregation().getAggregate().sterms().buckets().array().stream()
            .map(b -> RankDetailRes.of(b.key().stringValue(), b.docCount()))
            .toList();

        return new GetEmptySearchRankListRes(rankList);
    }

    /**
     * (통계) 관심사 변화 추이
     */
    public GetInterestChangeRes getInterestChange(Gender gender, Integer ageRange, Rank rank, BenefitType benefitType) {
        // 이번 달 상위 10개 제휴처 조회
        ElasticsearchAggregations rankResult = customSuggestionRepository.getTopInterestBrandByFiltering(gender, ageRange, rank, benefitType);

        List<String> brandNameList = rankResult.aggregationsAsMap()
            .get("top_brands").aggregation().getAggregate().sterms().buckets().array().stream()
            .map(b -> b.key().stringValue()).toList();

        // 제휴처 별 각 기준 count 조회
        ElasticsearchAggregations brandResult, storeResult, usageResult;
        try {
            brandResult = customSuggestionRepository.getCountsByFiltering(BRAND_CLICK, brandNameList, gender, ageRange, rank, benefitType);
            storeResult = customSuggestionRepository.getCountsByFiltering(STORE_CLICK, brandNameList, gender, ageRange, rank, benefitType);
            usageResult = customSuggestionRepository.getCountsByFiltering(USAGE, brandNameList, gender, ageRange, rank, benefitType);
        } catch (Exception e) {
            throw new GlobalException(ELASTIC_INTERNAL_ERROR);
        }
        return parseInterestChangeRes(brandResult, storeResult, usageResult);
    }

    public GetInterestChangeRes parseInterestChangeRes(ElasticsearchAggregations brandResult, ElasticsearchAggregations storeResult, ElasticsearchAggregations usageResult) {
        List<GetInterestListRes> resultList = new ArrayList<>();
        resultList.addAll(parseGetInterestListRes(brandResult, BRAND_CLICK));
        resultList.addAll(parseGetInterestListRes(storeResult, STORE_CLICK));
        resultList.addAll(parseGetInterestListRes(usageResult, USAGE));

        return new GetInterestChangeRes(mergeAndSumByDateAndBrand(resultList));
    }

    private List<GetInterestListRes> parseGetInterestListRes(ElasticsearchAggregations result, InterestType type) {
        return result.aggregationsAsMap()
            .get("monthly_count").aggregation().getAggregate().filter().aggregations()
            .get("count_info").dateHistogram().buckets().array().stream()
            .map(bucket -> GetInterestListRes.of(
                LocalDate.parse(bucket.keyAsString() + "-01"),
                bucket.aggregations()
                    .get("rank").sterms()
                    .buckets().array().stream()
                    .map(b -> switch (type) {
                        case BRAND_CLICK -> InterestDetailRes.of(b.key().stringValue(), b.docCount(), b.docCount(), 0L, 0L);
                        case STORE_CLICK -> InterestDetailRes.of(b.key().stringValue(), b.docCount(), 0L,  b.docCount(), 0L);
                        case USAGE -> InterestDetailRes.of(b.key().stringValue(), b.docCount() * 2, 0L, 0L, b.docCount());
                    })
                    .toList()
            ))
            .toList();
    }

    public List<GetInterestListRes> mergeAndSumByDateAndBrand(List<GetInterestListRes> resultList) {
        Map<LocalDate, Map<String, InterestDetailRes>> grouped = new HashMap<>();

        // date 기반 그룹화
        for (GetInterestListRes res : resultList) {
            grouped.computeIfAbsent(res.getDate(), d -> new HashMap<>());

            // brandName 기반 그룹화
            for (InterestDetailRes detail : res.getRankList()) {
                Map<String, InterestDetailRes> brandMap = grouped.get(res.getDate());
                brandMap.merge(detail.getName(), detail, (r1, r2) ->
                    InterestDetailRes.of(
                        r1.getName(),
                        r1.getTotalScore() + r2.getTotalScore(),
                        r1.getBrandClickCount() + r2.getBrandClickCount(),
                        r1.getStoreClickCount() + r2.getStoreClickCount(),
                        r1.getUsageCount() + r2.getUsageCount()
                    )
                );
            }
        }

        return grouped.entrySet().stream()
            .map(e -> GetInterestListRes.of(e.getKey(), new ArrayList<>(e.getValue().values())))
            .sorted(Comparator.comparing(GetInterestListRes::getDate)).toList();
    }

    private User findUser(Long userId){
        return userRepository.findById(userId)
            .orElseThrow(() -> new GlobalException(UserErrorCode.USER_NOT_FOUND));
    }
}
