package com.ureca.uble.domain.store.service;

import com.ureca.uble.domain.store.dto.response.GetStoreListRes;
import com.ureca.uble.domain.store.dto.response.GetStoreRes;
import com.ureca.uble.domain.store.repository.StoreRepository;
import com.ureca.uble.entity.enums.BenefitType;
import com.ureca.uble.entity.enums.Season;
import com.ureca.uble.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.ureca.uble.domain.store.exception.StoreErrorCode.OUT_OF_RANGE_INPUT;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    /**
     * 근처 매장 정보 조회
     */
    public GetStoreListRes getStores(double latitude, double longitude, int distance, Long categoryId, Long brandId, Season season, BenefitType type) {
        validateRange(latitude, longitude, distance);

        Point curPoint = getPoint(latitude, longitude);
        List<GetStoreRes> storeList = storeRepository.findStoresByFiltering(curPoint, distance, categoryId, brandId, season, type)
            .stream().map(GetStoreRes::from).toList();

        return new GetStoreListRes(storeList);
    }

    private void validateRange(double latitude, double longitude, int distance) {
        if ((latitude < -90 || latitude > 90) || (longitude < -180 || longitude > 180) || (distance <= 0 || distance > 10000)) {
            throw new GlobalException(OUT_OF_RANGE_INPUT);
        }
    }

    private Point getPoint(double latitude, double longitude) {
        return geometryFactory.createPoint(new Coordinate(longitude, latitude));
    }
}
