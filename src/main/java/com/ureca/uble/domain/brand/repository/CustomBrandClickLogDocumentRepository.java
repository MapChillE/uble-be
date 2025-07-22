package com.ureca.uble.domain.brand.repository;

import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;

public interface CustomBrandClickLogDocumentRepository {
    ElasticsearchAggregations getCategoryAndBrandRankByUserId(Long userId);
}
