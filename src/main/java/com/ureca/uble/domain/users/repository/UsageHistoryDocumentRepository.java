package com.ureca.uble.domain.users.repository;

import com.ureca.uble.entity.document.UsageHistoryDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface UsageHistoryDocumentRepository extends ElasticsearchRepository<UsageHistoryDocument, String>, CustomUsageHistoryDocumentRepository {
}
