package com.ureca.uble.domain.brand.repository;

import com.ureca.uble.entity.enums.BenefitType;
import com.ureca.uble.entity.enums.Gender;
import com.ureca.uble.entity.enums.Rank;
import com.ureca.uble.entity.enums.RankTarget;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;

public interface CustomBrandClickLogDocumentRepository {
    ElasticsearchAggregations getCategoryAndBrandRankByUserId(Long userId);

    ElasticsearchAggregations getUsageRankByFiltering(RankTarget rankTarget, Gender gender, Integer ageRange, Rank rank, BenefitType benefitType);
}
