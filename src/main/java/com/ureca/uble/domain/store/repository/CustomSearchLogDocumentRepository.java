package com.ureca.uble.domain.store.repository;

import com.ureca.uble.entity.enums.BenefitType;
import com.ureca.uble.entity.enums.Gender;
import com.ureca.uble.entity.enums.Rank;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;

public interface CustomSearchLogDocumentRepository {
    ElasticsearchAggregations getPopularSearchRankByFiltering(Gender gender, Integer ageRange, Rank rank, BenefitType benefitType);
}
