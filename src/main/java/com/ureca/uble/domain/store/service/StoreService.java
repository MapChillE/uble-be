package com.ureca.uble.domain.store.service;

import com.ureca.uble.domain.store.dto.response.GetStoreListRes;
import com.ureca.uble.domain.store.dto.response.GetStoreRes;
import com.ureca.uble.domain.store.repository.StoreRepository;
import com.ureca.uble.entity.enums.Season;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    /**
     * 근처 매장 정보 조회
     */
    public GetStoreListRes getStores(double latitude, double longitude, int distance, Long categoryId, Season season, Boolean isLocal) {
        Point curPoint = getPoint(latitude, longitude);

        List<GetStoreRes> storeList = storeRepository.findStoresByFiltering(curPoint, distance, categoryId, season, isLocal)
            .stream().map(GetStoreRes::from).toList();
        for (int i = 0; i < storeList.size(); i++) {
            System.out.println(storeList.get(i).getStoreName());
        }
        return new GetStoreListRes(storeList);
    }

    private Point getPoint(double latitude, double longitude) {
        return geometryFactory.createPoint(new Coordinate(longitude, latitude));
    }
}
