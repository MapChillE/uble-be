package com.ureca.uble.domain.brand.repository;

import com.ureca.uble.entity.document.BrandSuggestionDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface BrandSuggestionDocumentRepository extends ElasticsearchRepository<BrandSuggestionDocument, String>, CustomBrandSuggestionDocumentRepository {
}
