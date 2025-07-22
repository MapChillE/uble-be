package com.ureca.uble.domain.users.repository;

import com.ureca.uble.entity.User;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;

public interface CustomUsageHistoryDocumentRepository {
    ElasticsearchAggregations getUsageDateAndDiffAndCount(User user);
}
