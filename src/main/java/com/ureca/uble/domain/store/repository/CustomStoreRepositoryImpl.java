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
    public List<Store> findClusterRepresentatives(
            double swLng, double swLat, double neLng, double neLat,
            Long categoryId, Long brandId, Season season, BenefitType type,
            double gridSize
    ) {
        String sql = buildClusterQuery(categoryId, brandId, season, type);
        var query = em.createNativeQuery(sql, Store.class)
                .setParameter("minX", swLng)
                .setParameter("minY", swLat)
                .setParameter("maxX", neLng)
                .setParameter("maxY", neLat)
                .setParameter("gridSize", gridSize);

        if (categoryId != null) query.setParameter("categoryId", categoryId);
        if (brandId != null) query.setParameter("brandId", brandId);
        if (season != null) query.setParameter("season", season.name());

        return query.getResultList();
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

    private String buildClusterQuery(Long categoryId, Long brandId, Season season, BenefitType type) {
        StringBuilder sql = new StringBuilder();

        sql.append("""
        SELECT DISTINCT ON (cell)
            s.*,
            ST_SnapToGrid(
                s.location,
                :minX + :gridSize * floor((ST_X(s.location) - :minX) / :gridSize),
                :minY + :gridSize * floor((ST_Y(s.location) - :minY) / :gridSize),
                :gridSize,
                :gridSize
            ) AS cell
        FROM store s
        """);

        if (categoryId != null || brandId != null || season != null || type != null) {
            sql.append("""
            INNER JOIN brand b ON s.brand_id = b.id
            INNER JOIN category c ON b.category_id = c.id
            """);
        }

        sql.append("WHERE s.location && ST_MakeEnvelope(:minX, :minY, :maxX, :maxY, 4326)");
        appendFilterConditions(sql, categoryId, brandId, season, type);
        sql.append(" ORDER BY cell, s.visit_count DESC");

        return sql.toString();
    }

    private void appendFilterConditions(StringBuilder sql, Long categoryId, Long brandId, Season season, BenefitType type) {
        if (categoryId != null) sql.append(" AND c.id = :categoryId");
        if (brandId != null) sql.append(" AND b.id = :brandId");
        if (season != null) sql.append(" AND b.season = :season");
        if (type != null) {
            switch (type) {
                case VIP -> sql.append(" AND (b.rank_type = 'VIP' OR b.rank_type = 'VIP_NORMAL')");
                case NORMAL -> sql.append(" AND (b.rank_type = 'NORMAL' OR b.rank_type = 'VIP_NORMAL')");
                case LOCAL -> sql.append(" AND b.is_local = true");
            }
        }
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
