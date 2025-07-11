package com.ureca.uble.domain.store.service;

import com.ureca.uble.domain.store.dto.response.GetStoreListRes;
import com.ureca.uble.domain.store.repository.StoreRepository;
import com.ureca.uble.entity.Brand;
import com.ureca.uble.entity.Category;
import com.ureca.uble.entity.Store;
import com.ureca.uble.entity.enums.BenefitType;
import com.ureca.uble.entity.enums.Season;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StoreServiceTest {

    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private StoreService storeService;

    @Test
    @DisplayName("반경 500m 내의 매장 중 필터링 조건에 맞는 매장 리스트를 조회한다.")
    void getStores_get_check() {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

        // given
        Point testPoint = geometryFactory.createPoint(new Coordinate(127.0, 37.5));
        Point storeLocation = geometryFactory.createPoint(new Coordinate(127.001, 37.501));

        int distance = 500;
        Long categoryId = null;
        Long brandId = null;
        Season season = null;
        BenefitType type = null;

        Store mockStore = mock(Store.class);
        Brand mockBrand = mock(Brand.class);
        Category mockCategory = mock(Category.class);

        when(mockStore.getId()).thenReturn(1L);
        when(mockStore.getName()).thenReturn("테스트 선릉점");
        when(mockStore.getLocation()).thenReturn(storeLocation);
        when(mockStore.getBrand()).thenReturn(mockBrand);
        when(mockBrand.getCategory()).thenReturn(mockCategory);
        when(mockCategory.getName()).thenReturn("푸드");

        when(storeRepository.findStoresByFiltering(
            any(Point.class), anyInt(), any(), any(), any(), any()))
            .thenReturn(List.of(mockStore));

        // when
        GetStoreListRes result = storeService.getStores(testPoint.getY(), testPoint.getX(), distance, categoryId, brandId, season, type);

        // then
        assertNotNull(result);
        assertEquals(1, result.getStoreList().size());
        assertEquals("테스트 선릉점", result.getStoreList().get(0).getStoreName());
    }
}
