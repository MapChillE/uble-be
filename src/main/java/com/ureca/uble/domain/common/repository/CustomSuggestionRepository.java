package com.ureca.uble.domain.common.repository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.GeoLocation;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.CalendarInterval;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.MsearchRequest;
import co.elastic.clients.elasticsearch.core.MsearchResponse;
import co.elastic.clients.util.NamedValue;
import com.ureca.uble.domain.common.util.SearchFilterUtils;
import com.ureca.uble.entity.document.BrandClickLogDocument;
import com.ureca.uble.entity.document.StoreClickLogDocument;
import com.ureca.uble.entity.document.UsageHistoryDocument;
import com.ureca.uble.entity.enums.BenefitType;
import com.ureca.uble.entity.enums.Gender;
import com.ureca.uble.entity.enums.InterestType;
import com.ureca.uble.entity.enums.Rank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexBoost;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class CustomSuggestionRepository {

    private final ElasticsearchOperations elasticsearchOperations;
    private final ElasticsearchClient elasticsearchClient;

    /**
     * 카테고리, 재휴처 자동완성
     */
    public MsearchResponse<Map> findBrandSuggestionsByKeywordWithMsearch(String keyword, int categorySize, int brandSize) throws IOException {
        MsearchRequest request = new MsearchRequest.Builder()
            .searches(s -> s
                .header(h -> h.index("category-suggestion"))
                .body(b -> b
                    .size(categorySize)
                    .query(getCategoryQuery(keyword))
                )
            )
            .searches(s -> s
                .header(h -> h.index("brand-suggestion"))
                .body(b -> b
                    .size(brandSize)
                    .query(getBrandQuery(keyword, false))
                )
            )
            .build();
        return elasticsearchClient.msearch(request, Map.class);
    }

    /**
     * 카테고리, 재휴처, 매장 자동완성
     */
    public MsearchResponse<Map> findMapSuggestionsByKeywordWithMsearch(String keyword, int categorySize, int brandSize, int storeSize, double latitude, double longitude) throws IOException {
        MsearchRequest request = new MsearchRequest.Builder()
            .searches(s -> s
                .header(h -> h.index("category-suggestion"))
                .body(b -> b
                    .size(categorySize)
                    .query(getCategoryQuery(keyword))
                )
            )
            .searches(s -> s
                .header(h -> h.index("brand-suggestion"))
                .body(b -> b
                    .size(brandSize)
                    .query(getBrandQuery(keyword, true))
                )
            )
            .searches(s -> s
                .header(h -> h.index("store-suggestion"))
                .body(b -> b
                    .size(storeSize)
                    .minScore(5.0)
                    .query(getStoreQuery(keyword, latitude, longitude))
                )
            )
            .build();
        return elasticsearchClient.msearch(request, Map.class);
    }

    /**
     * 관심도 상위 10개 제휴처 조회
     */
    public ElasticsearchAggregations getTopInterestBrandByFiltering(Gender gender, Integer ageRange, Rank rank, BenefitType benefitType) {
        // filter 설정
        List<Query> filters = SearchFilterUtils.getAdminStatisticFilters(gender, ageRange, rank, benefitType);

        filters.add(DateRangeQuery.of(r -> r
            .field("createdAt")
            .gte("now/M")
            .lte("now")
        )._toRangeQuery()._toQuery());

        // 통계 쿼리 작성
        Aggregation aggregation = Aggregation.of(a -> a
            .terms(t -> t
                .field("brandName")
                .size(10)
                .order(List.of(NamedValue.of("_count", SortOrder.Desc)))
            )
        );

        // Index 가중치 설정
        List<IndexBoost> boosts = List.of(
            new IndexBoost("usage-history-log", 2.0f),
            new IndexBoost("brand-click-log", 1.0f),
            new IndexBoost("store-click-log", 1.0f)
        );

        // 최종 쿼리 작성
        NativeQuery query = NativeQuery.builder()
            .withQuery(q -> q.bool(b -> b.filter(filters)))
            .withAggregation("top_brands", aggregation)
            .withIndicesBoost(boosts)
            .withMaxResults(0)
            .withIndicesBoost()
            .build();

        return (ElasticsearchAggregations) elasticsearchOperations
            .search(query, Map.class, IndexCoordinates.of("brand-click-log", "store-click-log", "usage-history-log"))
            .getAggregations();
    }

    /**
     * 제휴처 별 클릭 및 사용량 조회
     */
    public ElasticsearchAggregations getCountsByFiltering(InterestType type, List<String> brandNameList, Gender gender, Integer ageRange, Rank rank, BenefitType benefitType) {
        List<Query> filters = SearchFilterUtils.getAdminStatisticFilters(gender, ageRange, rank, benefitType);

        filters.add(DateRangeQuery.of(r -> r
            .field("createdAt")
            .gte("now-6M/M")
            .lte("now/M")
        )._toRangeQuery()._toQuery());

        filters.add(TermsQuery.of(t -> t
            .field("brandName")
            .terms(v -> v.value(
                brandNameList.stream().map(FieldValue::of).toList()
            ))
        )._toQuery());

        // 통계 쿼리 작성
        Aggregation aggregation = Aggregation.of(a -> a
            .filter(f -> f
                .bool(b -> b.filter(filters))
            )
            .aggregations("count_info", da -> da
                .dateHistogram(dh -> dh
                    .field("createdAt")
                    .calendarInterval(CalendarInterval.Month)
                    .format("yyyy-MM")
                    .order(List.of(NamedValue.of("_key", SortOrder.Asc)))
                )
                .aggregations("rank", b -> b
                    .terms(t -> t
                        .field("brandName")
                        .order(List.of(NamedValue.of("_count", SortOrder.Desc)))
                    )
                )
            )
        );

        // Index 가중치 설정
        List<IndexBoost> boosts = List.of(
            new IndexBoost("usage-history-log", 2.0f),
            new IndexBoost("brand-click-log", 1.0f),
            new IndexBoost("store-click-log", 1.0f)
        );

        // 최종 쿼리 생성
        NativeQuery query = NativeQuery.builder()
            .withIndicesBoost(boosts)
            .withQuery(q -> q.bool(b -> b.filter(filters)))
            .withAggregation("monthly_count", aggregation)
            .withMaxResults(0)
            .build();

        Class<?> classType = switch (type) {
            case BRAND_CLICK -> BrandClickLogDocument.class;
            case STORE_CLICK -> StoreClickLogDocument.class;
            case USAGE -> UsageHistoryDocument.class;
        };

        return (ElasticsearchAggregations) elasticsearchOperations
            .search(query, classType)
            .getAggregations();
    }

    private Query getCategoryQuery(String keyword) {
        return MultiMatchQuery.of(m -> m
            .query(keyword)
            .fields("categoryName","categoryName._2gram", "categoryName._3gram")
            .type(TextQueryType.BestFields)
            .fuzziness("1")
        )._toQuery();
    }

    private Query getBrandQuery(String keyword, boolean isOfflineOnly) {
        Query multiMatchQuery = MultiMatchQuery.of(m -> m
            .query(keyword)
            .fields(
                "brandName^5",
                "brandName._2gram^5",
                "brandName._3gram^5",
                "category",
                "category._2gram",
                "category._3gram",
                "season",
                "season._2gram"
            )
            .type(TextQueryType.BestFields)
        )._toQuery();

        if (isOfflineOnly) {
            return BoolQuery.of(b -> b
                .must(multiMatchQuery)
                .filter(f -> f
                    .term(t -> t
                        .field("isOnline")
                        .value(false)
                    )
                )
            )._toQuery();
        }
        return multiMatchQuery;
    }

    private Query getStoreQuery(String keyword, double latitude, double longitude) {
        List<String> tokens = Arrays.stream(keyword.trim().split("\\s+"))
            .filter(token -> !token.isEmpty())
            .toList();

        // 각 토큰을 should 쿼리로 변환
        List<Query> shouldQueries = tokens.stream()
            .map(token -> MultiMatchQuery.of(q -> q
                .query(token)
                .type(TextQueryType.BoolPrefix)
                .fields(List.of(
                    "brandName", "brandName._2gram", "brandName._3gram",
                    "storeName", "storeName._2gram", "storeName._3gram",
                    "category", "category._2gram", "category._3gram",
                    "season", "season._2gram", "season._3gram",
                    "address"
                ))
            )._toQuery()
        ).toList();

        Query boolQuery = Query.of(q -> q
            .bool(b -> b
                .should(shouldQueries)
                .minimumShouldMatch("1")
            )
        );

        // 위치 정보
        GeoLocation geoLocation = GeoLocation.of(gl -> gl
            .latlon(ll -> ll.lat(latitude).lon(longitude))
        );

        FunctionScore gaussFunction = FunctionScore.of(fs -> fs
            .gauss(df -> df
                .geo(g -> g
                    .field("location")
                    .placement(p -> p
                        .origin(geoLocation)
                        .scale("5km")
                        .offset("0km")
                        .decay(0.5)
                    )
                )
            )
            .weight(10.0)
        );

        return Query.of(q -> q
            .functionScore(fsq -> fsq
                .query(boolQuery)
                .functions(List.of(gaussFunction))
                .scoreMode(FunctionScoreMode.Sum)
                .boostMode(FunctionBoostMode.Sum)
            )
        );
    }
}
