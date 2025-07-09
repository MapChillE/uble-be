package com.ureca.uble.domain.brand.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ureca.uble.domain.brand.dto.response.BrandDetailRes;
import com.ureca.uble.entity.Brand;

public interface BrandRepository extends JpaRepository<Brand, Long> {

	@Query("""
		SELECT new com.ureca.uble.domain.brand.dto.response.BrandDetailRes(
			b.id, b.name, b.csrNumber, b.description, b.imageUrl, b.season, c.name,
			CASE WHEN bm.id IS NOT NULL THEN true ELSE false END,
			bm.id
		)
		FROM Brand b
		JOIN b.category c
		LEFT JOIN Bookmark bm ON bm.brand.id = b.id AND bm.user.id = :userId
		WHERE b.id = :brandId
	""")
	Optional<BrandDetailRes> findBrandDetailById(@Param("brandId") Long brandId, @Param("userId") Long userId);



}
