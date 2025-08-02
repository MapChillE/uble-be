package com.ureca.uble.domain.store.repository;

import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.CalendarInterval;
import co.elastic.clients.elasticsearch._types.query_dsl.DateRangeQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.util.NamedValue;
import com.ureca.uble.domain.common.util.SearchFilterUtils;
import com.ureca.uble.entity.document.SearchLogDocument;
import com.ureca.uble.entity.enums.BenefitType;
import com.ureca.uble.entity.enums.Gender;
import com.ureca.uble.entity.enums.Rank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomSearchLogDocumentRepositoryImpl implements CustomSearchLogDocumentRepository {

    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public ElasticsearchAggregations getPopularSearchRankByFiltering(Gender gender, Integer ageRange, Rank rank, BenefitType benefitType) {
        // filter 설정
        List<Query> filters = SearchFilterUtils.getAdminStatisticFilters(gender, ageRange, rank, benefitType);
        filters.add(DateRangeQuery.of(r -> r
            .field("createdAt")
            .gte("now-6d/d")
            .lte("now/d")
        )._toRangeQuery()._toQuery());

        // 통계 쿼리
        Aggregation aggregation = Aggregation.of(a -> a
            .filter(f -> f
                .bool(b -> b.filter(filters))
            )
            .aggregations("daily_top10", da -> da
                .dateHistogram(dh -> dh
                    .field("createdAt")
                    .calendarInterval(CalendarInterval.Day)
                    .format("yyyy-MM-dd")
                    .order(List.of(NamedValue.of("_key", SortOrder.Desc)))
                )
                .aggregations("rank", ta -> ta
                    .terms(t -> t
                        .field("searchKeyword.raw")
                        .size(10)
                        .order(List.of(NamedValue.of("_count", SortOrder.Desc)))
                    )
                )
            )
        );

        // 최종 쿼리 생성
        NativeQuery query = NativeQuery.builder()
            .withAggregation("daily_top_keywords", aggregation)
            .withMaxResults(0)
            .build();

        return (ElasticsearchAggregations) elasticsearchOperations.search(query, SearchLogDocument.class).getAggregations();
    }

    @Override
    public ElasticsearchAggregations getEmptySearchRankByFiltering(Gender gender, Integer ageRange, Rank rank, BenefitType benefitType) {
        // filter 설정
        List<Query> filters = SearchFilterUtils.getAdminStatisticFilters(gender, ageRange, rank, benefitType);

        filters.add(DateRangeQuery.of(r -> r
            .field("createdAt")
            .gte("now-30d/d")
            .lte("now/d")
        )._toRangeQuery()._toQuery());

        filters.add(TermQuery.of(t -> t.field("isResultExists").value(false))._toQuery());

        Aggregation aggregation = Aggregation.of(a -> a
            .terms(t -> t
                .field("searchKeyword.raw")
                .size(10)
                .order(List.of(NamedValue.of("_count", SortOrder.Desc)))
            )
        );

        // 최종 쿼리 생성
        NativeQuery query = NativeQuery.builder()
            .withQuery(q -> q
                .bool(b -> b.filter(filters))
            )
            .withAggregation("empty_top_keywords", aggregation)
            .withMaxResults(0)
            .build();

        return (ElasticsearchAggregations) elasticsearchOperations.search(query, SearchLogDocument.class).getAggregations();
    }

    @Override
    public ElasticsearchAggregations getPopularKeywordList() {
        // 최근 3시간 filter
        Query filter = DateRangeQuery.of(r -> r
            .field("createdAt")
            .gte("now-3H/H")
            .lte("now/H")
        )._toRangeQuery()._toQuery();

        Aggregation aggregation = Aggregation.of(a -> a
            .terms(t -> t
                .field("searchKeyword.raw")
                .size(10)
                .order(List.of(NamedValue.of("_count", SortOrder.Desc)))
            )
        );

        NativeQuery query = NativeQuery.builder()
            .withQuery(q -> q
                .bool(b -> b.filter(List.of(filter)))
            )
            .withAggregation("top_keywords", aggregation)
            .withMaxResults(0)
            .build();

        return (ElasticsearchAggregations) elasticsearchOperations.search(query, SearchLogDocument.class).getAggregations();
    }
}
