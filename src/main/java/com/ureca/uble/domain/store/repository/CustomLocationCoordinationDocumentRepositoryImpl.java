package com.ureca.uble.domain.store.repository;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.FunctionScore;
import co.elastic.clients.elasticsearch._types.query_dsl.FunctionScoreQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.ureca.uble.entity.document.LocationCoordinationDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomLocationCoordinationDocumentRepositoryImpl implements CustomLocationCoordinationDocumentRepository {

    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public SearchHits<LocationCoordinationDocument> findCoordinationByLocation(List<String> keywordList) {
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

        NativeQuery searchQuery = NativeQuery.builder()
            .withQuery(functionScoreQuery._toQuery())
            .withMaxResults(1)
            .build();

        // 검색 실행 및 결과 반환
        return elasticsearchOperations.search(searchQuery, LocationCoordinationDocument.class);
    }

}
