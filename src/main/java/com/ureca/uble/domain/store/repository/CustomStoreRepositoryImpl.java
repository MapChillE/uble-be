package com.ureca.uble.domain.store.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ureca.uble.entity.Store;
import com.ureca.uble.entity.enums.BenefitType;
import com.ureca.uble.entity.enums.Rank;
import com.ureca.uble.entity.enums.RankType;
import com.ureca.uble.entity.enums.Season;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.ureca.uble.entity.QBenefit.benefit;
import static com.ureca.uble.entity.QBrand.brand;
import static com.ureca.uble.entity.QCategory.category;
import static com.ureca.uble.entity.QStore.store;

@Repository
@RequiredArgsConstructor
public class CustomStoreRepositoryImpl implements CustomStoreRepository {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 근처 매장 정보 조회
     */
    @Override
    public List<Store> findStoresByFiltering(Point curPoint, int distance, Long categoryId, Long brandId, Season season, BenefitType type) {
        return jpaQueryFactory
            .select(store)
            .from(store)
            .innerJoin(store.brand, brand).fetchJoin()
            .innerJoin(brand.category, category).fetchJoin()
            .where(
                withinRadius(curPoint, distance),
                categoryIdEq(categoryId),
                brandIdEq(brandId),
                seasonEq(season),
                typeEq(type)
            )
            .fetch();
    }

    /**
     * 매장에서 이용 가능한 혜택인지 판별
     */
    public Boolean checkStoreBenefitByType(Long storeId, BenefitType type) {
        return jpaQueryFactory
            .select(store.id)
            .from(store)
            .join(store.brand, brand)
            .where(
                store.id.eq(storeId),
                getCondition(type)
            )
            .fetchFirst() != null;
    }

    public BooleanExpression getCondition(BenefitType type) {
        return switch (type) {
            case VIP -> brand.rankType.eq(RankType.VIP)
                .or(brand.rankType.eq(RankType.VIP_NORMAL).and(
                    JPAExpressions.selectOne()
                        .from(benefit)
                        .where(benefit.brand.eq(brand)
                            .and(benefit.rank.eq(Rank.VIP)))
                        .exists()
                ));
            case NORMAL -> brand.rankType.eq(RankType.NORMAL)
                .or(brand.rankType.eq(RankType.VIP_NORMAL).and(
                    JPAExpressions.selectOne()
                        .from(benefit)
                        .where(benefit.brand.eq(brand)
                            .and(benefit.rank.eq(Rank.VIP)))
                        .notExists()
                ));
            case LOCAL -> brand.isLocal.isTrue();
        };
    }

    private BooleanExpression withinRadius(Point curPoint, int distance) {
        return Expressions.booleanTemplate(
            "function('ST_DWithin', {0}, {1}, {2})", store.location, curPoint, distance
        ).isTrue();
    }

    private BooleanExpression categoryIdEq(Long categoryId) {
        return categoryId == null ? null : brand.category.id.eq(categoryId);
    }

    private BooleanExpression brandIdEq(Long brandId) {
        return brandId == null ? null : brand.id.eq(brandId);
    }

    private BooleanExpression seasonEq(Season season) {
        return season == null ? null : brand.season.eq(season);
    }

    private BooleanExpression typeEq(BenefitType type) {
        return type == null ? null : getCondition(type);
    }
}
