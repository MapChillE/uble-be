package com.ureca.uble.domain.store.service;

import com.ureca.uble.domain.bookmark.repository.BookmarkRepository;
import com.ureca.uble.domain.store.dto.response.GetStoreDetailRes;
import com.ureca.uble.domain.store.dto.response.GetStoreListRes;
import com.ureca.uble.domain.store.dto.response.GetStoreSummaryRes;
import com.ureca.uble.domain.store.repository.StoreClickLogDocumentRepository;
import com.ureca.uble.domain.store.repository.StoreRepository;
import com.ureca.uble.domain.users.repository.UserRepository;
import com.ureca.uble.entity.Brand;
import com.ureca.uble.entity.Category;
import com.ureca.uble.entity.Store;
import com.ureca.uble.entity.User;
import com.ureca.uble.entity.document.StoreClickLogDocument;
import com.ureca.uble.entity.enums.Gender;
import com.ureca.uble.entity.enums.Rank;
import com.ureca.uble.entity.enums.RankType;
import com.ureca.uble.global.exception.GlobalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.ureca.uble.domain.store.exception.StoreErrorCode.STORE_NOT_FOUND;
import static com.ureca.uble.domain.users.exception.UserErrorCode.USER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoreServiceTest {

    @InjectMocks
    private StoreService storeService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private StoreRepository storeRepository;
    @Mock
    private BookmarkRepository bookmarkRepository;
    @Mock
    private StoreClickLogDocumentRepository storeClickLogDocumentRepository;

    private Store mockStore;
    private User mockUser;
    private Brand mockBrand;
    private Category mockCategory;
    private final GeometryFactory geometryFactory = new GeometryFactory();

    @BeforeEach
    void setUp() {
        Point location = geometryFactory.createPoint(new Coordinate(127.0276, 37.4979));

        mockUser = mock(User.class);
        mockBrand = mock(Brand.class);
        mockStore = mock(Store.class);
        mockCategory = mock(Category.class);

        lenient().when(mockStore.getId()).thenReturn(1L);
        lenient().when(mockStore.getName()).thenReturn("테스트 매장");
        lenient().when(mockStore.getLocation()).thenReturn(location);
        lenient().when(mockStore.getBrand()).thenReturn(mockBrand);
        lenient().when(mockBrand.getId()).thenReturn(10L);
    }

    private void setupCompleteDtoDependencies() {
        lenient().when(mockStore.getAddress()).thenReturn("서울시 테스트구 테스트동 123");
        lenient().when(mockStore.getPhoneNumber()).thenReturn("02-0000-0000");
        lenient().when(mockBrand.getDescription()).thenReturn("테스트 브랜드 설명입니다.");
        lenient().when(mockBrand.getImageUrl()).thenReturn("https://example.com/image.jpg");
        lenient().when(mockBrand.getCategory()).thenReturn(mockCategory);
        lenient().when(mockCategory.getName()).thenReturn("테스트 카테고리");
    }

    @Test
    @DisplayName("줌 레벨 16 이상: 클러스터링 없이 전체 매장을 반환한다")
    void getStores_HighZoom_ReturnsAllStores() {
        // given
        setupCompleteDtoDependencies();
        int zoomLevel = 17;
        double swLat = 37.0, swLng = 127.0, neLat = 38.0, neLng = 128.0;
        List<Store> storesInBox = List.of(mockStore);

        when(storeRepository.findStoresInBox(swLng, swLat, neLng, neLat, null, null, null, null))
                .thenReturn(storesInBox);

        // when
        GetStoreListRes result = storeService.getStores(zoomLevel, swLat, swLng, neLat, neLng, null, null, null, null);

        // then
        assertThat(result.getZoomLevel()).isEqualTo(zoomLevel);
        assertThat(result.getStoreList()).hasSize(1);
    }

    @Test
    @DisplayName("줌 레벨 16 미만: S2 셀 기반으로 클러스터링된 매장을 반환한다")
    void getStores_LowZoom_ReturnsClusteredStores() {
        // given
        setupCompleteDtoDependencies();
        int zoomLevel = 14;
        double swLat = 37.0, swLng = 127.0, neLat = 38.0, neLng = 128.0;
        List<Store> storesInCells = List.of(mockStore);

        when(storeRepository.findStoresInCellRanges(anyList(), any(), any(), any(), any()))
                .thenReturn(storesInCells);

        // when
        GetStoreListRes result = storeService.getStores(zoomLevel, swLat, swLng, neLat, neLng, null, null, null, null);

        // then
        assertThat(result.getZoomLevel()).isEqualTo(zoomLevel);
        assertThat(result.getStoreList()).hasSize(1);
        assertThat(result.getStoreList().get(0).getStoreId()).isEqualTo(mockStore.getId());
    }

    @Test
    @DisplayName("매장 요약 정보 조회 성공")
    void getStoreSummary_Success() {
        // given
        setupCompleteDtoDependencies();
        Long userId = 1L;
        Long storeId = 1L;

        when(storeRepository.findByIdWithBrandAndCategoryAndBenefits(storeId)).thenReturn(Optional.of(mockStore));
        when(bookmarkRepository.existsByBrand_IdAndUser_Id(mockBrand.getId(), userId)).thenReturn(true);

        // when
        GetStoreSummaryRes result = storeService.getStoreSummary(37.5, 127.0, userId, storeId);

        // then
        assertThat(result.getStoreId()).isEqualTo(storeId);
        assertThat(result.isBookmarked()).isTrue();
    }

    @Test
    @DisplayName("매장 상세 정보 조회 성공")
    void getStoreDetail_Success() {
        // given
        setupCompleteDtoDependencies();
        Long userId = 1L;
        Long storeId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(storeRepository.findByIdWithBrandAndCategoryAndBenefits(storeId)).thenReturn(Optional.of(mockStore));
        when(bookmarkRepository.existsByBrand_IdAndUser_Id(mockBrand.getId(), userId)).thenReturn(false);
        lenient().when(mockBrand.getRankType()).thenReturn(RankType.VIP_NORMAL);
        lenient().when(mockBrand.getBenefits()).thenReturn(Collections.emptyList());
        lenient().when(mockUser.getRank()).thenReturn(Rank.VIP);
        lenient().when(mockUser.getIsVipAvailable()).thenReturn(true);
        lenient().when(mockUser.getGender()).thenReturn(Gender.MALE);

        // when
        GetStoreDetailRes result = storeService.getStoreDetail(37.5, 127.0, userId, storeId);

        // then
        assertThat(result.getStoreId()).isEqualTo(storeId);
        verify(storeClickLogDocumentRepository, times(1)).save(any(StoreClickLogDocument.class));
    }

    @Test
    @DisplayName("존재하지 않는 매장 조회 시 예외 발생")
    void getStore_NotFound_ThrowsException() {
        // given
        Long storeId = 999L;
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(storeRepository.findByIdWithBrandAndCategoryAndBenefits(storeId)).thenReturn(Optional.empty());

        // when
        GlobalException exception = assertThrows(GlobalException.class, () -> {
            storeService.getStoreDetail(37.5, 127.0, userId, storeId);
        });

        // then
        assertThat(exception.getResultCode()).isEqualTo(STORE_NOT_FOUND);
    }

    @Test
    @DisplayName("상세 정보 조회 시 존재하지 않는 유저이면 예외 발생")
    void getStoreDetail_UserNotFound_ThrowsException() {
        // given
        Long userId = 999L;
        Long storeId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when
        GlobalException exception = assertThrows(GlobalException.class, () -> {
            storeService.getStoreDetail(37.5, 127.0, userId, storeId);
        });

        // then
        assertThat(exception.getResultCode()).isEqualTo(USER_NOT_FOUND);
    }
}
