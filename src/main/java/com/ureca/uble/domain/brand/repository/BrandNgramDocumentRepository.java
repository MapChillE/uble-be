package com.ureca.uble.domain.brand.repository;

import com.ureca.uble.entity.document.BrandNgramDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface BrandNgramDocumentRepository extends ElasticsearchRepository<BrandNgramDocument, String> {
}
