package com.ureca.uble.domain.store.repository;

import com.ureca.uble.entity.document.StoreSuggestionDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface StoreSuggestionDocumentRepository extends ElasticsearchRepository<StoreSuggestionDocument, String> {
}
