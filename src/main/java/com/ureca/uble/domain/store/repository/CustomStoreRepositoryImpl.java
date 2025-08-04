package com.ureca.uble.domain.store.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ureca.uble.entity.Store;
import com.ureca.uble.entity.enums.BenefitType;
import com.ureca.uble.entity.enums.RankType;
import com.ureca.uble.entity.enums.Season;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.ureca.uble.entity.QBrand.brand;
import static com.ureca.uble.entity.QCategory.category;
import static com.ureca.uble.entity.QStore.store;

@Repository
@RequiredArgsConstructor
public class CustomStoreRepositoryImpl implements CustomStoreRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final EntityManager em;

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
            .fetch();
    }

    @Override
    public Optional<Store> findRepresentativeStoreInCell(
            long rangeMin, long rangeMax, Long categoryId,
            Long brandId, Season season, BenefitType type) {
        Store result = jpaQueryFactory
                .selectFrom(store)
                .innerJoin(store.brand, brand).fetchJoin()
                .innerJoin(brand.category, category).fetchJoin()
                .where(
                        store.s2CellId.between(rangeMin, rangeMax),
                        categoryIdEq(categoryId),
                        brandIdEq(brandId),
                        seasonEq(season),
                        typeEq(type)
                )
                .orderBy(store.visitCount.desc(), store.id.asc())
                .limit(1)
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Store findNearestByBrandId(Long brandId, Double latitude, Double longitude) {
        return jpaQueryFactory
                .selectFrom(store)
                .where(store.brand.id.eq(brandId))
                .orderBy(distanceOrderSpec(latitude, longitude))
                .limit(1)
                .fetchOne();
    }

    private OrderSpecifier<Double> distanceOrderSpec(Double latitude, Double longitude) {
        return Expressions.numberTemplate(
                Double.class,
                "ST_Distance({0}, ST_SetSRID(ST_MakePoint({1}, {2}), 4326))",
                store.location,
                longitude,
                latitude
        ).asc();
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
