package com.ureca.uble.domain.store.repository;

import com.ureca.uble.entity.document.LocationCoordinationDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface LocationCoordinationDocumentRepository extends ElasticsearchRepository<LocationCoordinationDocument, String> {
}
