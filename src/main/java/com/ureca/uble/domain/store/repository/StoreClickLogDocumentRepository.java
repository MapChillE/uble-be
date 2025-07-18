package com.ureca.uble.domain.store.repository;

import com.ureca.uble.entity.document.StoreClickLogDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface StoreClickLogDocumentRepository extends ElasticsearchRepository<StoreClickLogDocument, String> {
}
