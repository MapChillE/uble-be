package com.ureca.uble.domain.store.service;

import com.ureca.uble.domain.store.dto.response.GetStoreDetailRes;
import com.ureca.uble.domain.store.dto.response.GetStoreListRes;
import com.ureca.uble.domain.store.repository.StoreClickLogDocumentRepository;
import com.ureca.uble.domain.store.repository.StoreRepository;
import com.ureca.uble.domain.users.repository.UsageCountRepository;
import com.ureca.uble.domain.users.repository.UserRepository;
import com.ureca.uble.entity.*;
import com.ureca.uble.entity.enums.*;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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

    @Mock
    private UserRepository userRepository;

    @Mock
    private UsageCountRepository usageCountRepository;

    @Mock
    private StoreClickLogDocumentRepository storeClickLogDocumentRepository;

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

    @Test
    @DisplayName("매장 상세 정보를 조회한다.")
    void getStoreDetailSuccess() {
        // given
        Double latitude = 37.5;
        Double longitude = 127.0;
        Long userId = 1L;
        Long storeId = 10L;

        User mockUser = mock(User.class);
        Brand mockBrand = mock(Brand.class);
        Category mockCategory = mock(Category.class);
        Store mockStore = mock(Store.class);
        Point mockLocation = mock(Point.class);
        Benefit mockBenefit = mock(Benefit.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(storeRepository.findByIdWithBrandAndCategoryAndBenefits(storeId)).thenReturn(Optional.of(mockStore));
        when(usageCountRepository.findByUserAndBenefit(any(), any())).thenReturn(Optional.empty());

        when(mockUser.getRank()).thenReturn(Rank.VIP);
        when(mockUser.getGender()).thenReturn(Gender.FEMALE);
        when(mockStore.getBrand()).thenReturn(mockBrand);
        when(mockStore.getId()).thenReturn(storeId);
        when(mockStore.getName()).thenReturn("스타벅스 선릉점");
        when(mockStore.getAddress()).thenReturn("서울 강남구 테헤란로64길 18");
        when(mockStore.getPhoneNumber()).thenReturn("02-1234-5678");
        when(mockStore.getLocation()).thenReturn(mockLocation);
        when(mockLocation.getY()).thenReturn(37.0);
        when(mockLocation.getX()).thenReturn(127.0);
        when(mockBrand.getId()).thenReturn(123L);
        when(mockBrand.getDescription()).thenReturn("커피가 맛있는 스타벅스");
        when(mockBrand.getImageUrl()).thenReturn("https://example.com");
        when(mockBrand.getCategory()).thenReturn(mockCategory);
        when(mockCategory.getName()).thenReturn("음식점");
        when(mockBrand.getRankType()).thenReturn(RankType.NORMAL);

        when(mockBrand.getBenefits()).thenReturn(List.of(mockBenefit));
        when(mockBenefit.getId()).thenReturn(1001L);
        when(mockBenefit.getRank()).thenReturn(com.ureca.uble.entity.enums.Rank.NORMAL);
        when(mockBenefit.getContent()).thenReturn("아메리카노 무료");
        when(mockBenefit.getManual()).thenReturn("매장 방문 시 쿠폰 제시");
        when(mockBenefit.getPeriod()).thenReturn(com.ureca.uble.entity.enums.Period.MONTHLY);
        when(mockBenefit.getNumber()).thenReturn(1);

        // when
        GetStoreDetailRes result = storeService.getStoreDetail(latitude, longitude, userId, storeId);

        // then
        assertThat(result.getBrandId()).isEqualTo(123L);
        assertThat(result.getStoreId()).isEqualTo(storeId);
        assertThat(result.getStoreName()).isEqualTo("스타벅스 선릉점");
        assertThat(result.getDescription()).isEqualTo("커피가 맛있는 스타벅스");
        assertThat(result.getAddress()).isEqualTo("서울 강남구 테헤란로64길 18");
        assertThat(result.getPhoneNumber()).isEqualTo("02-1234-5678");
        assertThat(result.getCategory()).isEqualTo("음식점");
        assertThat(result.getImageUrl()).isEqualTo("https://example.com");
        assertThat(result.getBenefitList()).hasSize(1);
        assertThat(result.getBenefitList().get(0).getBenefitId()).isEqualTo(1001L);
        assertThat(result.getBenefitList().get(0).getType()).isEqualTo("NORMAL");
        assertThat(result.getBenefitList().get(0).getContent()).isEqualTo("아메리카노 무료");
    }
}
