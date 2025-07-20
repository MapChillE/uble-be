package com.ureca.uble.domain.category.repository;

import com.ureca.uble.entity.document.CategorySuggestionDocument;
import org.springframework.data.elasticsearch.core.SearchHits;

public interface CustomCategorySuggestionDocumentRepository {
    SearchHits<CategorySuggestionDocument> findByKeywordAndLimit(String keyword, int size);
}
