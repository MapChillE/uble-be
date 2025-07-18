package com.ureca.uble.domain.brand.repository;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import com.ureca.uble.entity.document.BrandNoriDocument;
import com.ureca.uble.entity.enums.BenefitType;
import com.ureca.uble.entity.enums.Season;
import com.ureca.uble.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.ureca.uble.domain.brand.exception.BrandErrorCode.ELASTIC_INTERNAL_ERROR;

@Repository
@RequiredArgsConstructor
public class CustomBrandNoriRepositoryImpl implements CustomBrandNoriRepository {

    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public SearchHits<BrandNoriDocument> findAllByFilteringAndPage(String keyword, String category, Season season, BenefitType type, int page, int size) {
        // 쿼리 작성
        Query multiMatchQuery = MultiMatchQuery.of(m -> m
            .query(keyword)
            .fields("brandName^10","category^20", "season^20")
            .fuzziness("AUTO")
        )._toQuery();

        // filter 적용
        List<Query> filters = new ArrayList<>();
        if (category != null && !category.isEmpty()) {
            Query categoryFilter = TermQuery.of(t -> t
                .field("category.raw")
                .value(category)
            )._toQuery();
            filters.add(categoryFilter);
        }

        if (season != null) {
            Query seasonFilter = TermQuery.of(t -> t
                .field("season.raw")
                .value(season.toString())
            )._toQuery();
            filters.add(seasonFilter);
        }

        if (type != null) {
            List<FieldValue> rankTypeValues = switch (type) {
                case VIP -> List.of(FieldValue.of("VIP"), FieldValue.of("VIP_NORMAL"));
                case NORMAL -> List.of(FieldValue.of("NORMAL"), FieldValue.of("VIP_NORMAL"));
                case LOCAL -> List.of(FieldValue.of("LOCAL"));
            };

            Query typeFilter = TermsQuery.of(t -> t
                .field("rankType")
                .terms(terms -> terms.value(rankTypeValues.stream().map(FieldValue::of).toList()))
            )._toQuery();
            filters.add(typeFilter);
        }

        // 조합
        Query boolQuery = BoolQuery.of(b -> b
            .must(multiMatchQuery)
            .filter(filters)
        )._toQuery();

        NativeQuery nativeQuery = NativeQuery.builder()
            .withQuery(boolQuery)
            .withPageable(PageRequest.of(page, size))
            .build();

        // 요청 및 결과 수집
        try {
            return this.elasticsearchOperations.search(nativeQuery, BrandNoriDocument.class);
        } catch (Exception e) {
            throw new GlobalException(ELASTIC_INTERNAL_ERROR);
        }
    }
}
