package com.ureca.uble.domain.store.repository;

import com.ureca.uble.entity.document.StoreNgramDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface StoreDocumentRepository extends ElasticsearchRepository<StoreNgramDocument, String> {
}
