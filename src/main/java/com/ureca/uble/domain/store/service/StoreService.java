package com.ureca.uble.domain.store.service;

import static com.ureca.uble.domain.store.exception.StoreErrorCode.*;
import static com.ureca.uble.domain.users.exception.UserErrorCode.*;

import java.util.List;
import java.util.Optional;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;

import com.ureca.uble.domain.bookmark.repository.BookmarkRepository;
import com.ureca.uble.domain.store.dto.response.GetBenefitInfoRes;
import com.ureca.uble.domain.store.dto.response.GetStoreDetailRes;
import com.ureca.uble.domain.store.dto.response.GetStoreListRes;
import com.ureca.uble.domain.store.dto.response.GetStoreRes;
import com.ureca.uble.domain.store.dto.response.GetStoreSummaryRes;
import com.ureca.uble.domain.store.repository.StoreClickLogDocumentRepository;
import com.ureca.uble.domain.store.repository.StoreRepository;
import com.ureca.uble.domain.users.repository.UsageCountRepository;
import com.ureca.uble.domain.users.repository.UserRepository;
import com.ureca.uble.entity.Benefit;
import com.ureca.uble.entity.Brand;
import com.ureca.uble.entity.Store;
import com.ureca.uble.entity.UsageCount;
import com.ureca.uble.entity.User;
import com.ureca.uble.entity.document.StoreClickLogDocument;
import com.ureca.uble.entity.enums.BenefitType;
import com.ureca.uble.entity.enums.Period;
import com.ureca.uble.entity.enums.Rank;
import com.ureca.uble.entity.enums.RankType;
import com.ureca.uble.entity.enums.Season;
import com.ureca.uble.global.exception.GlobalException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreService {

    private final UserRepository userRepository;
    private final UsageCountRepository usageCountRepository;
    private final StoreRepository storeRepository;
    private final BookmarkRepository bookmarkRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
    private final StoreClickLogDocumentRepository storeClickLogDocumentRepository;

    /**
     * 근처 매장 정보 조회
     */
    @Transactional(readOnly = true)
    public GetStoreListRes getStores(double latitude, double longitude, int distance, Long categoryId, Long brandId, Season season, BenefitType type) {
        validateRange(latitude, longitude, distance);

        Point curPoint = getPoint(latitude, longitude);
        List<GetStoreRes> storeList = storeRepository.findStoresByFiltering(curPoint, distance, categoryId, brandId, season, type)
            .stream().map(GetStoreRes::from).toList();

        return new GetStoreListRes(storeList);
    }

    /**
     * 매장 소모달 정보 조회
     */
    @Transactional(readOnly = true)
    public GetStoreSummaryRes getStoreSummary(double latitude, double longitude, Long userId, Long storeId) {
        User user = findUser(userId);
        Store store = findByIdWithBrandAndCategoryAndBenefits(storeId);

        // 좌표 기준 거리 계산
        Double distance = calculateDistance(store.getLocation().getY(), store.getLocation().getX(), latitude, longitude);

        // 북마크 여부
        boolean isBookmarked = bookmarkRepository.existsByBrand_IdAndUser_Id(store.getBrand().getId(), userId);
        return GetStoreSummaryRes.of(store, distance, isBookmarked);
    }

    /**
     * 매장 상세 정보 조회
     */
    @Transactional(readOnly = true)
    public GetStoreDetailRes getStoreDetail(Double latitude, Double longitude, Long userId, Long storeId) {
        User user = findUser(userId);
        Store store = findByIdWithBrandAndCategoryAndBenefits(storeId);

        // 좌표 기준 거리 계산
        Double distance = calculateDistance(store.getLocation().getY(), store.getLocation().getX(), latitude, longitude);

        // 혜택 사용 가능 여부 검증
        RankType type = store.getBrand().getRankType();
        boolean isNormalAvailable = (type == RankType.NORMAL || type == RankType.VIP_NORMAL) && handleNormalBenefit(user, store);
        boolean isVipAvailable = (type == RankType.VIP || type == RankType.VIP_NORMAL) && handleVipBenefit(user);
        boolean isLocalAvailable = (type == RankType.LOCAL) && handleLocalBenefit(user);

        // 북마크 여부
        boolean isBookmarked = bookmarkRepository.existsByBrand_IdAndUser_Id(store.getBrand().getId(), userId);

        // 혜택 List 계산
        List<GetBenefitInfoRes> benefitList = store.getBrand().getBenefits().stream()
            .map(b -> GetBenefitInfoRes.of(b, getBenefitType(store.getBrand(), b)))
            .toList();

        // 로그 기록
        try {
            storeClickLogDocumentRepository.save(StoreClickLogDocument.of(user, store));
        } catch (Exception e) {
            log.warn("매장 상세 조회 로그 저장에 실패하였습니다 : {}", e.getMessage());
        }

        return GetStoreDetailRes.of(store, distance, isNormalAvailable, isVipAvailable, isLocalAvailable, isBookmarked, benefitList);
    }

    private BenefitType getBenefitType(Brand brand, Benefit benefit) {
        return brand.getIsLocal() ? BenefitType.LOCAL :
            benefit.getRank() == Rank.NONE ? BenefitType.VIP :
                BenefitType.NORMAL;
    }

    private void validateRange(double latitude, double longitude, int distance) {
        if ((latitude < -90 || latitude > 90) || (longitude < -180 || longitude > 180) || (distance <= 0 || distance > 10000)) {
            throw new GlobalException(OUT_OF_RANGE_INPUT);
        }
    }

    private Point getPoint(double latitude, double longitude) {
        return geometryFactory.createPoint(new Coordinate(longitude, latitude));
    }

    private static double calculateDistance(double latitude1, double longitude1, double latitude2, double longitude2) {
        final int RADIUS = 6371000;

        double dLat = Math.toRadians(latitude2 - latitude1);
        double dLon = Math.toRadians(longitude2 - longitude1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
            + Math.cos(Math.toRadians(latitude1)) * Math.cos(Math.toRadians(latitude2))
            * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return RADIUS * c;
    }

    private boolean handleVipBenefit(User user) {
        return (user.getRank() == Rank.VIP || user.getRank() == Rank.VVIP) && user.getIsVipAvailable();
    }

    private boolean handleLocalBenefit(User user) {
        return user.getRank() != Rank.NORMAL && user.getIsLocalAvailable();
    }

    private boolean handleNormalBenefit(User user, Store store) {
        Benefit benefit = store.getBrand().getBenefits().stream()
            .filter(b -> getBenefitType(store.getBrand(), b) == BenefitType.NORMAL)
            .findFirst()
            .orElse(null);

        if (benefit == null) return false;

        Optional<UsageCount> optionalCount = usageCountRepository.findByUserAndBenefit(user, benefit);

        return optionalCount.map(uc -> benefit.getPeriod() == Period.NONE || benefit.getNumber() > uc.getCount()).orElse(true);
    }

    private Store findByIdWithBrandAndCategoryAndBenefits(Long storeId) {
        return storeRepository.findByIdWithBrandAndCategoryAndBenefits(storeId).orElseThrow(() -> new GlobalException(STORE_NOT_FOUND));
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new GlobalException(USER_NOT_FOUND));
    }


}
