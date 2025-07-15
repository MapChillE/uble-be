package com.ureca.uble.domain.brand.repository;

import com.ureca.uble.entity.document.CategoryNgramDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface CategoryNgramDocumentRepository extends ElasticsearchRepository<CategoryNgramDocument, String> {
}
