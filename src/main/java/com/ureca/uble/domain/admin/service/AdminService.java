package com.ureca.uble.domain.admin.service;

import com.ureca.uble.domain.admin.dto.response.GetClickRankListRes;
import com.ureca.uble.domain.admin.dto.response.GetLocalRankListRes;
import com.ureca.uble.domain.admin.dto.response.GetUsageRankListRes;
import com.ureca.uble.domain.admin.dto.response.RankDetailRes;
import com.ureca.uble.domain.brand.repository.BrandClickLogDocumentRepository;
import com.ureca.uble.domain.users.repository.UsageHistoryDocumentRepository;
import com.ureca.uble.entity.enums.BenefitType;
import com.ureca.uble.entity.enums.Gender;
import com.ureca.uble.entity.enums.Rank;
import com.ureca.uble.entity.enums.RankTarget;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UsageHistoryDocumentRepository usageHistoryDocumentRepository;
    private final BrandClickLogDocumentRepository brandClickLogDocumentRepository;

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
        ElasticsearchAggregations rankResult = brandClickLogDocumentRepository.getUsageRankByFiltering(rankTarget, gender, ageRange, rank, benefitType);

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
}
