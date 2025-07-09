package com.ureca.uble.domain.store.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ureca.uble.entity.Store;
import com.ureca.uble.entity.enums.Season;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.ureca.uble.entity.QBrand.brand;
import static com.ureca.uble.entity.QStore.store;

@Repository
@RequiredArgsConstructor
public class CustomStoreRepositoryImpl implements CustomStoreRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Store> findStoresByFiltering(Point curPoint, int distance, Long categoryId, Long brandId, Season season, Boolean isLocal) {
        return jpaQueryFactory
            .select(store)
            .from(store)
            .innerJoin(store.brand, brand)
            .where(
                withinRadius(curPoint, distance),
                categoryIdEq(categoryId),
                brandIdEq(brandId),
                seasonEq(season),
                isLocalEq(isLocal)
            )
            .fetch();
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

    private BooleanExpression isLocalEq(Boolean isLocal) {
        return isLocal == null ? null : brand.isLocal.eq(isLocal);
    }
}
