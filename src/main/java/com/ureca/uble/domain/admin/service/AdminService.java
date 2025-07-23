package com.ureca.uble.domain.admin.service;

import com.ureca.uble.domain.admin.dto.response.GetUsageRankListRes;
import com.ureca.uble.domain.admin.dto.response.UsageRankDetailRes;
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

    /**
     * (통계) 제휴처/카테고리 이용 순위
     */
    public GetUsageRankListRes getUsageRank(RankTarget rankTarget, Gender gender, Integer ageRange, Rank rank, BenefitType benefitType) {
        ElasticsearchAggregations rankResult = usageHistoryDocumentRepository.getUsageRankByFiltering(rankTarget, gender, ageRange, rank, benefitType);

        List<UsageRankDetailRes> rankList = rankResult.aggregationsAsMap()
            .get("usage_rank").aggregation().getAggregate().filter().aggregations()
            .get("brand_rank").sterms().buckets().array().stream()
            .map(b -> UsageRankDetailRes.of(b.key().stringValue(), b.docCount()))
            .toList();

        return GetUsageRankListRes.of(rankTarget, rankList);
    }
}
