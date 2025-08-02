package com.ureca.uble.domain.store.repository;

import com.ureca.uble.entity.Store;
import com.ureca.uble.entity.enums.BenefitType;
import com.ureca.uble.entity.enums.Season;

import java.util.List;

public interface CustomStoreRepository {
    List<Store> findStoresInBox(double swLng, double swLat, double neLng, double neLat,
                                Long categoryId, Long brandId, Season season, BenefitType type);

    List<Store> findClusterRepresentatives(double swLng, double swLat, double neLng, double neLat,
                                           Long categoryId, Long brandId, Season season, BenefitType type,
                                           double gridSize);

    Store findNearestByBrandId(Long brandId, Double latitude, Double longitude);
}
