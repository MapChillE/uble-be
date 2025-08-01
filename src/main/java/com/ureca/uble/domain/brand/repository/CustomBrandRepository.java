package com.ureca.uble.domain.brand.repository;

import java.util.List;

import com.ureca.uble.entity.Brand;
import com.ureca.uble.entity.enums.BenefitCategory;
import com.ureca.uble.entity.enums.RankType;
import com.ureca.uble.entity.enums.Season;

public interface CustomBrandRepository {
	List<Brand> findWithFilterAndCursor(Long categoryId, Season season, List<RankType> rankTypes, BenefitCategory benefitCategory, Long lastBrandId, int size);
	List<Brand> findOfflineAfterCursor(Long lastBrandId, int size);
}
