package com.ureca.uble.domain.category.repository;

import com.ureca.uble.entity.document.CategoryNgramDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface CategoryNgramDocumentRepository extends ElasticsearchRepository<CategoryNgramDocument, String> {
}
