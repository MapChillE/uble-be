package com.ureca.uble.domain.store.repository;

import com.ureca.uble.entity.document.LocationCoordinationDocument;
import org.springframework.data.elasticsearch.core.SearchHits;

import java.util.List;

public interface CustomLocationCoordinationDocumentRepository {
    SearchHits<LocationCoordinationDocument> findCoordinationByLocation(List<String> keywordList);
}
