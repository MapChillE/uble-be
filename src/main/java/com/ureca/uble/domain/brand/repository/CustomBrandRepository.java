package com.ureca.uble.domain.brand.repository;

import java.util.List;

import com.ureca.uble.entity.Brand;
import com.ureca.uble.entity.enums.Season;

public interface CustomBrandRepository {
	List<Brand> findWithFilterAndCursor(Long categoryId, Season season, Boolean isLocal, Long lastBrandId, int size);
}
