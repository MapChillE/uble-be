package com.ureca.uble.domain.brand.repository;

import static com.ureca.uble.entity.QBrand.*;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ureca.uble.entity.Brand;
import com.ureca.uble.entity.enums.BenefitCategory;
import com.ureca.uble.entity.enums.RankType;
import com.ureca.uble.entity.enums.Season;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CustomBrandRepositoryImpl implements CustomBrandRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public List<Brand> findWithFilterAndCursor(Long categoryId, Season season, List<RankType> rankTypes, BenefitCategory benefitCategory, Long lastBrandId, int size) {
		return jpaQueryFactory
			.selectFrom(brand)
			.leftJoin(brand.benefits).fetchJoin()
			.where(
				categoryIdEq(categoryId),
				seasonEq(season),
				rankTypeIn(rankTypes),
				gtBrandId(lastBrandId),
				benefitCategoryEq(benefitCategory)
			)
			.orderBy(brand.id.asc())
			.limit(size)
			.fetch();
	}

	@Override
	public List<Brand> findOfflineAfterCursor(Long lastBrandId, int size){
		return jpaQueryFactory
				.selectFrom(brand)
				.where(brand.isOnline.eq(false), gtBrandId(lastBrandId))
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

	private BooleanExpression rankTypeIn(List<RankType> rankTypes) {
		return (rankTypes == null || rankTypes.isEmpty()) ? null : brand.rankType.in(rankTypes);
	}

	private BooleanExpression benefitCategoryEq(BenefitCategory benefitCategory) {
		return benefitCategory != null ? brand.benefitCategory.eq(benefitCategory) : null;
	}

	private BooleanExpression gtBrandId(Long lastBrandId) {
		return lastBrandId == null ? null : brand.id.gt(lastBrandId);
	}
}
