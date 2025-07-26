package com.ureca.uble.domain.category.repository;

import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import com.ureca.uble.entity.document.CategorySuggestionDocument;
import com.ureca.uble.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Repository;

import static com.ureca.uble.domain.common.exception.CommonErrorCode.ELASTIC_INTERNAL_ERROR;

@Repository
@RequiredArgsConstructor
public class CustomCategorySuggestionDocumentRepositoryImpl implements CustomCategorySuggestionDocumentRepository {

    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public SearchHits<CategorySuggestionDocument> findByKeywordAndLimit(String keyword, int size) {
        Query multiMatchQuery = MultiMatchQuery.of(m -> m
            .query(keyword)
            .fields("categoryName","categoryName._2gram", "categoryName._3gram")
            .type(TextQueryType.BestFields)
            .fuzziness("1")
        )._toQuery();

        NativeQuery nativeQuery = NativeQuery.builder()
            .withQuery(multiMatchQuery)
            .withPageable(PageRequest.of(0, size))
            .build();

        try {
            return elasticsearchOperations.search(nativeQuery, CategorySuggestionDocument.class);
        } catch (Exception e) {
            throw new GlobalException(ELASTIC_INTERNAL_ERROR);
        }
    }
}
