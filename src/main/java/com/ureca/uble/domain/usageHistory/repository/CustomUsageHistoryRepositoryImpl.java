package com.ureca.uble.domain.usageHistory.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ureca.uble.domain.usageHistory.dto.response.UsageHistoryRes;
import com.ureca.uble.entity.QStore;
import com.ureca.uble.entity.QUsageHistory;
import com.ureca.uble.global.response.CursorPageRes;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CustomUsageHistoryRepositoryImpl implements CustomUsageHistoryRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public CursorPageRes<UsageHistoryRes> findUsagesByUserId(Long userId, Long lastHistoryId, int size) {
		QUsageHistory usage = QUsageHistory.usageHistory;
		QStore store = QStore.store;

		List<UsageHistoryRes> results = queryFactory
			.selectFrom(usage)
			.join(usage.store, store).fetchJoin()
			.where(
				usage.user.id.eq(userId),
				ltHistoryId(lastHistoryId, usage)
			)
			.orderBy(usage.id.desc())
			.limit(size + 1)
			.fetch()
			.stream()
			.map(u -> UsageHistoryRes.of(
				u.getId(),
				u.getStore().getName(),
				u.getCreatedAt()
			))
			.collect(Collectors.toList());

		boolean hasNext = results.size() > size;
		Long nextCursor = hasNext ? results.get(size - 1).getId() : null;

		if(hasNext){
			results = results.subList(0, size);
		}

		return CursorPageRes.<UsageHistoryRes>builder()
			.content(results)
			.hasNext(hasNext)
			.lastCursorId(nextCursor)
			.build();
	}

	private BooleanExpression ltHistoryId(Long lastHistoryId, QUsageHistory usage) {
		return lastHistoryId != null ? usage.id.lt(lastHistoryId) : null;
	}
}
