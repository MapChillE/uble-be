package com.ureca.uble.domain.brand.repository;

import com.ureca.uble.entity.document.BrandNoriDocument;
import com.ureca.uble.entity.enums.BenefitType;
import com.ureca.uble.entity.enums.Season;
import org.springframework.data.elasticsearch.core.SearchHits;

public interface CustomBrandNoriRepository {
    SearchHits<BrandNoriDocument> findAllByFilteringAndPage(String keyword, String category, Season season, BenefitType type, int page, int size);
}
