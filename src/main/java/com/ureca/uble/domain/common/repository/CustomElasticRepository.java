package com.ureca.uble.domain.common.repository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.*;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.CalendarInterval;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.MsearchRequest;
import co.elastic.clients.elasticsearch.core.MsearchResponse;
import co.elastic.clients.util.NamedValue;
import com.ureca.uble.domain.common.util.SearchFilterUtils;
import com.ureca.uble.domain.store.dto.response.GetGlobalSuggestionRes;
import com.ureca.uble.entity.document.BrandClickLogDocument;
import com.ureca.uble.entity.document.StoreClickLogDocument;
import com.ureca.uble.entity.document.UsageHistoryDocument;
import com.ureca.uble.entity.enums.Rank;
import com.ureca.uble.entity.enums.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexBoost;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class CustomElasticRepository {

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
                    .query(getBrandQuery(keyword, false, false))
                )
            )
            .build();
        return elasticsearchClient.msearch(request, Map.class);
    }

    /**
     * 매장 자동완성 + 카테고리/제휴처 대표 매장 위경도 조회
     */
    public MsearchResponse<Map> findMapSuggestionsByKeywordWithMsearch(String keyword, int storeSize, double latitude, double longitude, List<GetGlobalSuggestionRes> find) throws IOException {
        MsearchRequest.Builder builder = new MsearchRequest.Builder();

        // 매장 자동완성
        builder.searches(s -> s
                .header(h -> h.index("store-suggestion"))
                .body(b -> b
                    .size(storeSize)
                    .minScore(5.0)
                    .query(getStoreQuery(keyword, latitude, longitude))
                )
        );

        // CATEGORY / BRAND 동적 추가
        for (GetGlobalSuggestionRes res : find) {
            builder.searches(s -> s
                .header(h -> h.index("store-suggestion"))
                .body(b -> b
                    .size(1)
                    .query(getNearestStoreQuery(res.getType(), res.getSuggestion()))
                    .sort(sb -> sb
                        .geoDistance(g -> g
                            .field("location")
                            .location(l -> l.latlon(
                                new LatLonGeoLocation.Builder()
                                    .lat(latitude)
                                    .lon(longitude)
                                    .build()
                            ))
                            .order(SortOrder.Asc)
                            .unit(DistanceUnit.Meters)
                        )
                    )
                )
            );
        }
        return elasticsearchClient.msearch(builder.build(), Map.class);
    }

    /**
     * 위경도 계산 + 제휴처, 카테고리 검색
     */
    public MsearchResponse<Map> findCoordinationAndBrandAndCategoryWithMSearch(String keyword, int categorySize, int brandSize) throws IOException {
        List<String> keywordList = List.of(keyword.split(" "));
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
                    .query(getBrandQuery(keyword, true, true))
                )
            )
            .searches(s -> s
                .header(h -> h.index("location-coordination"))
                .body(b -> b
                    .size(1)
                    .query(findCoordinationByLocation(keywordList))
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

    private Query getNearestStoreQuery(SuggestionType type, String name) {
        String fieldName = switch (type) {
            case BRAND -> "brandName.raw";
            case CATEGORY -> "category.raw";
            default -> "";
        };

        return MatchQuery.of(m -> m
            .field(fieldName)
            .query(name)
        )._toQuery();
    }

    private Query findCoordinationByLocation(List<String> keywordList) {
        // should 쿼리 생성 : 유연한 검색
        List<Query> shouldQueries = new ArrayList<>();
        keywordList.forEach(keyword -> {
            shouldQueries.add(MatchQuery.of(m -> m
                .field("name")
                .query(keyword)
            )._toQuery());
        });

        Query boolQuery = Query.of(q -> q
            .bool(b -> b
                .should(shouldQueries)
                .minimumShouldMatch("1")
            )
        );

        // keyword 일치 시 가중치 부여
        List<FieldValue> fieldValues = keywordList.stream()
            .map(FieldValue::of)
            .toList();

        List<FunctionScore> functions = new ArrayList<>();
        functions.add(FunctionScore.of(fs -> fs
            .filter(Query.of(q -> q
                .terms(t -> t
                    .field("name.raw")
                    .terms(tt -> tt.value(fieldValues))
                )
            ))
            .weight(10.0)
        ));

        // 최종 쿼리 빌드
        FunctionScoreQuery functionScoreQuery = FunctionScoreQuery.of(f -> f
            .query(boolQuery)
            .functions(functions)
        );

        // 검색 실행 및 결과 반환
        return functionScoreQuery._toQuery();
    }

    private Query getCategoryQuery(String keyword) {
        return MultiMatchQuery.of(m -> m
            .query(keyword)
            .fields("categoryName","categoryName._2gram", "categoryName._3gram")
            .type(TextQueryType.BestFields)
            .fuzziness("1")
        )._toQuery();
    }

    private Query getBrandQuery(String keyword, boolean isOfflineOnly, boolean isNotLocal) {
        List<Query> filters = new ArrayList<>();

        if (isOfflineOnly) {
            filters.add(TermQuery.of(t -> t.field("isOnline").value(false))._toQuery());
        }

        if (isNotLocal) {
            filters.add(TermQuery.of(t -> t.field("isLocal").value(false))._toQuery());
        }

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

        if (!filters.isEmpty()) {
            return BoolQuery.of(b -> b
                .must(multiMatchQuery)
                .filter(filters)
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
