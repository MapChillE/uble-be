package com.ureca.uble.domain.store.repository;

import com.ureca.uble.entity.document.SearchLogDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SearchLogDocumentRepository extends ElasticsearchRepository<SearchLogDocument, String> {
}
