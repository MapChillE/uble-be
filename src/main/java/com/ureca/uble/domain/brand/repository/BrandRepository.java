package com.ureca.uble.domain.brand.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ureca.uble.entity.Brand;

public interface BrandRepository extends JpaRepository<Brand, Long>, CustomBrandRepository {

	@Query("""
		SELECT b from Brand b
		JOIN FETCH b.category c
		LEFT JOIN FETCH b.benefits bene
		WHERE b.id = :brandId
	""")
	Optional<Brand> findWithBenefitsById(@Param("brandId") Long brandId);

	@Query("""
    select distinct b
    from Brand b
    left join fetch b.category
    left join fetch b.benefits
    """)
	List<Brand> findAllWithCategoryAndBenefits();
}
