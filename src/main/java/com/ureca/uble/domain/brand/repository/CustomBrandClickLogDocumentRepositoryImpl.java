package com.ureca.uble.domain.brand.repository;

import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.util.NamedValue;
import com.ureca.uble.domain.common.util.SearchFilterUtils;
import com.ureca.uble.entity.enums.BenefitType;
import com.ureca.uble.entity.enums.Gender;
import com.ureca.uble.entity.enums.Rank;
import com.ureca.uble.entity.enums.RankTarget;
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

    @Override
    public ElasticsearchAggregations getClickRankByFiltering(RankTarget rankTarget, Gender gender, Integer ageRange, Rank rank, BenefitType benefitType) {
        // filter 설정
        List<Query> filters = SearchFilterUtils.getAdminStatisticFilters(gender, ageRange, rank, benefitType);

        // 통계 쿼리
        String fieldName = switch (rankTarget) {
            case BRAND -> "brandName";
            case CATEGORY -> "category";
        };

        Aggregation aggregation = Aggregation.of(a -> a
            .filter(f -> f
                .bool(b -> b.filter(filters))
            )
            .aggregations("rank", Aggregation.of(sub -> sub
                .terms(t -> t
                    .field(fieldName)
                    .size(10)
                    .order(List.of(NamedValue.of("_count", SortOrder.Desc)))
                )
            ))
        );

        // 최종 쿼리 생성
        NativeQuery query = NativeQuery.builder()
            .withAggregation("click_rank", aggregation)
            .withMaxResults(0)
            .build();

        return (ElasticsearchAggregations) elasticsearchOperations
            .search(query, Map.class, IndexCoordinates.of("brand-click-log", "store-click-log"))
            .getAggregations();
    }
}
