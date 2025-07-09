package com.ureca.uble.domain.brand.repository;

import static com.ureca.uble.entity.QBrand.*;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ureca.uble.entity.Brand;
import com.ureca.uble.entity.enums.Season;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CustomBrandRepositoryImpl implements CustomBrandRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public List<Brand> findWithFilterAndCursor(Long categoryId, Season season, Boolean isLocal, Long lastBrandId, int size) {
		return jpaQueryFactory
			.selectFrom(brand)
			.leftJoin(brand.benefits).fetchJoin()
			.where(
				categoryIdEq(categoryId),
				seasonEq(season),
				isLocalEq(isLocal),
				gtBrandId(lastBrandId)
			)
			.orderBy(brand.id.asc())
			.limit(size)
			.fetch();
	}

	private BooleanExpression categoryIdEq(Long categoryId) {
		return categoryId == null ? null : brand.category.id.eq(categoryId);
	}

	private BooleanExpression seasonEq(Season season) {
		return season == null ? null : brand.season.eq(season);
	}

	private BooleanExpression isLocalEq(Boolean isLocal) {
		return isLocal == null ? null : brand.isLocal.eq(isLocal);
	}

	private BooleanExpression gtBrandId(Long lastBrandId) { return lastBrandId == null ? null : brand.id.gt(lastBrandId); }
}
