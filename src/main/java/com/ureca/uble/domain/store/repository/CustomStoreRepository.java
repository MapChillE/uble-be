package com.ureca.uble.domain.store.repository;

import com.google.common.geometry.S2CellId;
import com.ureca.uble.entity.Store;
import com.ureca.uble.entity.enums.BenefitType;
import com.ureca.uble.entity.enums.Season;

import java.util.List;
import java.util.Optional;

public interface CustomStoreRepository {
    List<Store> findStoresInBox(double swLng, double swLat, double neLng, double neLat,
                                Long categoryId, Long brandId, Season season, BenefitType type);

    List<Store> findStoresInCellRanges(List<S2CellId> cellIds, Long categoryId, Long brandId, Season season, BenefitType type);

    Store findNearestByBrandId(Long brandId, Double latitude, Double longitude);

}
