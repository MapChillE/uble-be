package com.ureca.uble.domain.brand.repository;

import com.ureca.uble.entity.document.BrandClickLogDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface BrandClickLogDocumentRepository extends ElasticsearchRepository<BrandClickLogDocument, String>, CustomBrandClickLogDocumentRepository {
}
