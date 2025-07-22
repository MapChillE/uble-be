package com.ureca.uble.domain.brand.repository;

import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.util.NamedValue;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class CustomBrandClickLogDocumentRepositoryImpl implements CustomBrandClickLogDocumentRepository {

    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public ElasticsearchAggregations getCategoryAndBrandRankByUserId(Long userId) {
        // 카테고리 순위
        Aggregation categoryAggregation = Aggregation.of(a -> a
            .terms(t -> t
                .field("category")
                .order(List.of(NamedValue.of("_count", SortOrder.Desc)))
            )
        );

        // 제휴처 순위
        Aggregation brandAggregation = Aggregation.of(a -> a
            .terms(t -> t
                .field("brandName")
                .size(10)
                .order(List.of(NamedValue.of("_count", SortOrder.Desc)))
            )
        );

        // Query 생성
        NativeQuery query = NativeQuery.builder()
            .withQuery(q -> q
                .term(t -> t
                    .field("userId")
                    .value(userId)
                )
            )
            .withAggregation("category_rank", categoryAggregation)
            .withAggregation("brand_rank", brandAggregation)
            .withMaxResults(0)
            .build();

        // 실행 및 결과 반환
        return (ElasticsearchAggregations) elasticsearchOperations
            .search(query, Map.class, IndexCoordinates.of("brand-click-log", "store-click-log"))
            .getAggregations();
    }
}
