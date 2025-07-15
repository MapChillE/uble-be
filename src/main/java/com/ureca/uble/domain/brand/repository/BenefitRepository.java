package com.ureca.uble.domain.brand.repository;

import com.ureca.uble.entity.Benefit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BenefitRepository extends JpaRepository<Benefit, Long> {
    @Query(value = """
        SELECT *
        FROM benefit b
        WHERE b.rank = 'NORMAL'
          AND b.brand_id = (
            SELECT s.brand_id
            FROM store s
            WHERE s.id = :storeId
        )
        LIMIT 1
    """, nativeQuery = true)
    Optional<Benefit> findNormalBenefitByStoreId(@Param("storeId") Long storeId);
}
