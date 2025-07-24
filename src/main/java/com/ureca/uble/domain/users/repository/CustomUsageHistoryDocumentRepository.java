package com.ureca.uble.domain.users.repository;

import com.ureca.uble.entity.User;
import com.ureca.uble.entity.enums.BenefitType;
import com.ureca.uble.entity.enums.Gender;
import com.ureca.uble.entity.enums.Rank;
import com.ureca.uble.entity.enums.RankTarget;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;

public interface CustomUsageHistoryDocumentRepository {
    ElasticsearchAggregations getUsageDateAndDiffAndCount(User user);

    ElasticsearchAggregations getUsageRankByFiltering(RankTarget rankTarget, Gender gender, Integer ageRange, Rank rank, BenefitType benefitType);

    ElasticsearchAggregations getLocalRankByFiltering(Gender gender, Integer ageRange, Rank rank, BenefitType benefitType);

    ElasticsearchAggregations getRecommendationBySimilarUser(User user, int ageRange);
}
