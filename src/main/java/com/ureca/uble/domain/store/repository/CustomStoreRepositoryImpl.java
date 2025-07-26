package com.ureca.uble.domain.store.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ureca.uble.entity.Store;
import com.ureca.uble.entity.enums.BenefitType;
import com.ureca.uble.entity.enums.RankType;
import com.ureca.uble.entity.enums.Season;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

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
    public List<Store> findStoresInBox(double swLng, double swLat, double neLng, double neLat,
                                       Long categoryId, Long brandId, Season season, BenefitType type) {
        return jpaQueryFactory
            .select(store).from(store)
            .innerJoin(store.brand, brand).fetchJoin()
            .innerJoin(brand.category, category).fetchJoin()
            .where(
                Expressions.booleanTemplate(
                    "function('ST_Intersects', {0}, function('ST_MakeEnvelope', {1}, {2}, {3}, {4}, 4326)) = TRUE",
                    store.location,
                    swLng, swLat, neLng, neLat
                ),
                categoryIdEq(categoryId), brandIdEq(brandId), seasonEq(season), typeEq(type)
            )
            .limit(50)
            .fetch();
    }

    public BooleanExpression getCondition(BenefitType type) {
        return switch (type) {
              case VIP -> brand.rankType.eq(RankType.VIP)
                .or(brand.rankType.eq(RankType.VIP_NORMAL));
            case NORMAL -> brand.rankType.eq(RankType.NORMAL)
                .or(brand.rankType.eq(RankType.VIP_NORMAL));
            case LOCAL -> brand.isLocal.isTrue();
        };
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
