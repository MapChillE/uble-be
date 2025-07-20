package com.ureca.uble.domain.brand.repository;

import com.ureca.uble.entity.document.BrandSuggestionDocument;
import org.springframework.data.elasticsearch.core.SearchHits;

public interface CustomBrandSuggestionDocumentRepository {
    SearchHits<BrandSuggestionDocument> findByKeywordAndLimit(String keyword, int size);
}
