package com.ureca.uble.domain.store.service;

import com.ureca.uble.domain.store.dto.response.GetStoreListRes;
import com.ureca.uble.domain.store.dto.response.GetStoreRes;
import com.ureca.uble.domain.store.repository.StoreRepository;
import com.ureca.uble.entity.Store;
import com.ureca.uble.entity.enums.Season;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StoreServiceTest {

    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private StoreService storeService;

    @Test
    @DisplayName("반경 500m 내의 매장 중 필터링 조건에 맞는 매장 리스트를 조회합니다.")
    void getStores_get_check() {
        MockitoAnnotations.openMocks(this);
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

        // given
        Point testPoint = geometryFactory.createPoint(new Coordinate(127.0, 37.5));
        Point storeLocation = geometryFactory.createPoint(new Coordinate(127.001, 37.501));

        int distance = 500;
        Long categoryId = null;
        Season season = null;
        Boolean isLocal = null;

        Store mockStore = mock(Store.class);
        when(mockStore.getId()).thenReturn(1L);
        when(mockStore.getName()).thenReturn("테스트 선릉점");
        when(mockStore.getLocation()).thenReturn(storeLocation);

        when(storeRepository.findStoresByFiltering(
            any(Point.class), anyInt(), any(), any(), any()))
            .thenReturn(List.of(mockStore));

        // when
        GetStoreListRes result = storeService.getStores(testPoint.getY(), testPoint.getX(), distance, categoryId, season, isLocal);

        // then
        assertNotNull(result);
        assertEquals(1, result.getStoreList().size());
        assertEquals("테스트 선릉점", result.getStoreList().get(0).getStoreName());
    }
}
