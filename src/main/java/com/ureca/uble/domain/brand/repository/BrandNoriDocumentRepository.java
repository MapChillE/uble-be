package com.ureca.uble.domain.brand.repository;

import com.ureca.uble.entity.document.BrandNoriDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface BrandNoriDocumentRepository extends ElasticsearchRepository<BrandNoriDocument, String>, CustomBrandNoriRepository {
}
