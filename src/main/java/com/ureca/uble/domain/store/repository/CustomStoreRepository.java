package com.ureca.uble.domain.store.repository;

import com.ureca.uble.entity.Store;
import com.ureca.uble.entity.enums.Season;
import org.locationtech.jts.geom.Point;

import java.util.List;

public interface CustomStoreRepository {
    List<Store> findStoresByFiltering(Point curPoint, int distance, Long categoryId, Long brandId, Season season, Boolean isLocal);
}
