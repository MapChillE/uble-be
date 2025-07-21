package com.ureca.uble.domain.brand.repository;

import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import com.ureca.uble.entity.document.BrandSuggestionDocument;
import com.ureca.uble.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Repository;

import static com.ureca.uble.global.exception.GlobalErrorCode.ELASTIC_INTERNAL_ERROR;

@Repository
@RequiredArgsConstructor
public class CustomBrandSuggestionDocumentRepositoryImpl implements CustomBrandSuggestionDocumentRepository {

    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public SearchHits<BrandSuggestionDocument> findByKeywordAndLimit(String keyword, int size) {
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
            .fuzziness("AUTO")
        )._toQuery();

        NativeQuery nativeQuery = NativeQuery.builder()
            .withQuery(multiMatchQuery)
            .withPageable(PageRequest.of(0, size))
            .build();

        try {
            return elasticsearchOperations.search(nativeQuery, BrandSuggestionDocument.class);
        } catch (Exception e) {
            throw new GlobalException(ELASTIC_INTERNAL_ERROR);
        }
    }
}
