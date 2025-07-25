package com.ureca.uble.domain.common.repository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.GeoLocation;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.MsearchRequest;
import co.elastic.clients.elasticsearch.core.MsearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class CustomSuggestionRepository {

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
