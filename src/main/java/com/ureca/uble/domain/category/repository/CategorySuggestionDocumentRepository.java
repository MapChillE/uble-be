package com.ureca.uble.domain.category.repository;

import com.ureca.uble.entity.document.CategorySuggestionDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface CategorySuggestionDocumentRepository extends ElasticsearchRepository<CategorySuggestionDocument, String>, CustomCategorySuggestionDocumentRepository {
}
