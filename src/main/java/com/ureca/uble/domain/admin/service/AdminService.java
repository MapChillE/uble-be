package com.ureca.uble.domain.admin.service;

import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch.core.MsearchResponse;
import com.ureca.uble.domain.admin.dto.response.*;
import com.ureca.uble.domain.admin.exception.AdminErrorCode;
import com.ureca.uble.domain.auth.exception.AuthErrorCode;
import com.ureca.uble.domain.brand.repository.BrandClickLogDocumentRepository;
import com.ureca.uble.domain.common.repository.CustomElasticRepository;
import com.ureca.uble.domain.store.repository.SearchLogDocumentRepository;
import com.ureca.uble.domain.users.exception.UserErrorCode;
import com.ureca.uble.domain.users.repository.TokenRepository;
import com.ureca.uble.domain.users.repository.UsageHistoryDocumentRepository;
import com.ureca.uble.domain.users.repository.UserRepository;
import com.ureca.uble.entity.Token;
import com.ureca.uble.entity.User;
import com.ureca.uble.entity.enums.*;
import com.ureca.uble.global.exception.GlobalException;
import com.ureca.uble.global.security.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static com.ureca.uble.domain.common.exception.CommonErrorCode.ELASTIC_INTERNAL_ERROR;
import static com.ureca.uble.entity.enums.InterestType.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    @Value("${admin.code}")
    private String adminCode;

    private final UsageHistoryDocumentRepository usageHistoryDocumentRepository;
    private final BrandClickLogDocumentRepository brandClickLogDocumentRepository;
    private final SearchLogDocumentRepository searchLogDocumentRepository;
    private final CustomElasticRepository customElasticRepository;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final JwtProvider jwtProvider;

    /**
     * Admin 인증
     */
    public AdminCodeRes verifyAdmin(String providedCode, HttpServletResponse response){

        if(!MessageDigest.isEqual(providedCode.getBytes(), adminCode.getBytes())){
            throw new GlobalException(AdminErrorCode.INVALID_ADMIN_CODE);
        }

        User user = userRepository.findById(10000L)
            .orElseThrow(() -> new GlobalException(UserErrorCode.USER_NOT_FOUND));

        Token token = tokenRepository.findByUser(user)
            .orElseThrow(() -> new GlobalException(AuthErrorCode.INVALID_TOKEN));

        String accessToken = jwtProvider.createAccessToken(user);
        String refreshToken = jwtProvider.createRefreshToken(user);
        LocalDateTime newExpiry = jwtProvider.getRefreshTokenExpiry(refreshToken);

        token.updateRefreshToken(refreshToken, newExpiry);
        tokenRepository.save(token);

        jwtProvider.addAccessTokenHeader(response, accessToken);
        jwtProvider.addRefreshTokenCookie(response, refreshToken);
        jwtProvider.addAuthCheckCookie(response);

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
        ElasticsearchAggregations rankResult = customElasticRepository.getTopInterestBrandByFiltering(gender, ageRange, rank, benefitType);

        List<String> brandNameList = rankResult.aggregationsAsMap()
            .get("top_brands").aggregation().getAggregate().sterms().buckets().array().stream()
            .map(b -> b.key().stringValue()).toList();

        // 제휴처 별 각 기준 count 조회
        ElasticsearchAggregations brandResult, storeResult, usageResult;
        try {
            brandResult = customElasticRepository.getCountsByFiltering(BRAND_CLICK, brandNameList, gender, ageRange, rank, benefitType);
            storeResult = customElasticRepository.getCountsByFiltering(STORE_CLICK, brandNameList, gender, ageRange, rank, benefitType);
            usageResult = customElasticRepository.getCountsByFiltering(USAGE, brandNameList, gender, ageRange, rank, benefitType);
        } catch (Exception e) {
            throw new GlobalException(ELASTIC_INTERNAL_ERROR);
        }
        return parseInterestChangeRes(brandResult, storeResult, usageResult);
    }

    /**
     * 어드민 대시보드 조회
     */
    public GetDashBoardRes getDashboardInfo() {
        // 조회
        MsearchResponse<Map> response;
        try {
            response = customElasticRepository.getDashboardInfo();
        } catch (IOException e) {
            throw new GlobalException(ELASTIC_INTERNAL_ERROR);
        }

        Map<String, Aggregate> usageAggs = response.responses().get(0).result().aggregations();

        // MAU
        long curMau = usageAggs.get("cur_mau").filter().aggregations().get("total_user_count").cardinality().value();
        long lastMau = usageAggs.get("last_mau").filter().aggregations().get("total_user_count").cardinality().value();

        // 사용 횟수
        long curUsage = usageAggs.get("cur_mau").filter().docCount();
        long lastUsage = usageAggs.get("last_mau").filter().docCount();

        // top 5 사용 횟수
        List<RankDetailRes> topUsageRankList = usageAggs.get("top_usage_brand").filter().aggregations()
            .get("top_usage_brand").sterms().buckets().array().stream()
            .map(b -> RankDetailRes.of(b.key().stringValue(), b.docCount())).toList();

        // top 5 사용 지역
        List<RankDetailRes> topUsageLocalList = new ArrayList<>(usageAggs.get("top_usage_local").filter().aggregations()
            .get("top_usage_local").sterms().buckets().array().stream()
            .map(b -> RankDetailRes.of(b.key().stringValue(), b.docCount())).toList());

        Long others = usageAggs.get("top_usage_local").filter().aggregations().get("top_usage_local").sterms().sumOtherDocCount();
        if (others != null && others > 0) topUsageLocalList.add(RankDetailRes.of("기타", others));

        // 제휴처, 매장 개수
        long brandCount = Objects.requireNonNull(response.responses().get(1).result().hits().total()).value();
        long storeCount = Objects.requireNonNull(response.responses().get(2).result().hits().total()).value();

        // top 10 검색어
        List<RankDetailRes> topSearchKeywordList = response.responses().get(3).result().aggregations()
            .get("top_keyword").sterms().buckets().array().stream()
            .map(b -> RankDetailRes.of(b.key().stringValue(), b.docCount())).toList();

        return GetDashBoardRes.of(curMau, lastMau, curUsage, lastUsage, brandCount, storeCount, topUsageRankList, topUsageLocalList, topSearchKeywordList);
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
