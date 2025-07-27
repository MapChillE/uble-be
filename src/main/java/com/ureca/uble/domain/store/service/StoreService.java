package com.ureca.uble.domain.store.service;

import co.elastic.clients.elasticsearch.core.MsearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.ureca.uble.domain.bookmark.repository.BookmarkRepository;
import com.ureca.uble.domain.common.repository.CustomSuggestionRepository;
import com.ureca.uble.domain.store.dto.response.*;
import com.ureca.uble.domain.store.repository.LocationCoordinationDocumentRepository;
import com.ureca.uble.domain.store.repository.StoreClickLogDocumentRepository;
import com.ureca.uble.domain.store.repository.StoreRepository;
import com.ureca.uble.domain.users.repository.UsageCountRepository;
import com.ureca.uble.domain.users.repository.UserRepository;
import com.ureca.uble.entity.*;
import com.ureca.uble.entity.document.LocationCoordinationDocument;
import com.ureca.uble.entity.document.StoreClickLogDocument;
import com.ureca.uble.entity.enums.*;
import com.ureca.uble.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.ureca.uble.domain.common.exception.CommonErrorCode.ELASTIC_INTERNAL_ERROR;
import static com.ureca.uble.domain.store.exception.StoreErrorCode.OUT_OF_RANGE_INPUT;
import static com.ureca.uble.domain.store.exception.StoreErrorCode.STORE_NOT_FOUND;
import static com.ureca.uble.domain.users.exception.UserErrorCode.USER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreService {

    private final UserRepository userRepository;
    private final UsageCountRepository usageCountRepository;
    private final StoreRepository storeRepository;
    private final BookmarkRepository bookmarkRepository;
    private final StoreClickLogDocumentRepository storeClickLogDocumentRepository;
    private final LocationCoordinationDocumentRepository locationCoordinationDocumentRepository;
    private final CustomSuggestionRepository customSuggestionRepository;

    /**
     * 근처 매장 정보 조회
     */
    @Transactional(readOnly = true)
    public GetStoreListRes getStores(double swLat, double swLng, double neLat, double neLng,
                                     Long categoryId, Long brandId, Season season, BenefitType type) {
        // 입력 범위 검증: 남서 좌표가 북동 좌표보다 작아야 함
        if (swLat >= neLat || swLng >= neLng){
            throw new GlobalException(OUT_OF_RANGE_INPUT);
        }

        List<GetStoreRes> storeList = storeRepository.findStoresInBox(swLng, swLat, neLng, neLat, categoryId, brandId, season, type)
                .stream().map(GetStoreRes::from).toList();
        return new GetStoreListRes(storeList);
    }

    /**
     * 매장 소모달 정보 조회
     */
    @Transactional(readOnly = true)
    public GetStoreSummaryRes getStoreSummary(double latitude, double longitude, Long userId, Long storeId) {
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

    /**
     * (자동완성) 지도 전체 검색 자동완성
     */
    public GetGlobalSuggestionListRes getGlobalSuggestionList(String keyword, double latitude, double longitude, int size) {
        if(keyword == null || keyword.trim().isEmpty()) {
            return new GetGlobalSuggestionListRes(List.of());
        }

        // 위경도 설정
        List<String> keywordList = List.of(keyword.split(" "));
        SearchHits<LocationCoordinationDocument> locationHits = locationCoordinationDocumentRepository.findCoordinationByLocation(keywordList);

        if(locationHits.getTotalHits() > 0) {
            latitude = locationHits.getSearchHit(0).getContent().getLocation().getLat();
            longitude = locationHits.getSearchHit(0).getContent().getLocation().getLon();
        }

        // 최종 검색 실행
        MsearchResponse<Map> response;
        try {
            int CATEGORY_SUGGESTION_SIZE = 2;
            int BRAND_SUGGESTION_SIZE = 2;
            response = customSuggestionRepository.findMapSuggestionsByKeywordWithMsearch(keyword, CATEGORY_SUGGESTION_SIZE, BRAND_SUGGESTION_SIZE,
                size - (CATEGORY_SUGGESTION_SIZE + BRAND_SUGGESTION_SIZE), latitude, longitude);
        } catch (Exception e) {
            throw new GlobalException(ELASTIC_INTERNAL_ERROR);
        }

        List<GetGlobalSuggestionRes> res = new ArrayList<>();

        // 카테고리 매핑
        res.addAll(response.responses().get(0).result().hits().hits().stream()
            .map(Hit::source).filter(Objects::nonNull)
            .map(source -> GetGlobalSuggestionRes.of(
                (String) source.get("categoryName"),
                null, null,
                SuggestionType.CATEGORY,
                null, null, null
            ))
            .toList());

        // brand 조회
        res.addAll(response.responses().get(1).result().hits().hits().stream()
            .map(Hit::source).filter(Objects::nonNull)
            .map(source -> GetGlobalSuggestionRes.of(
                (String) source.get("brandName"),
                (String) source.get("category"),
                null,
                SuggestionType.BRAND,
                null, null, null
            ))
            .toList());

        // store조회
        res.addAll(response.responses().get(2).result().hits().hits().stream()
            .map(Hit::source).filter(Objects::nonNull)
            .map(source -> {
                Map<String, Object> locMap = (Map<String, Object>) source.get("location");
                Double lat = null, lon = null;
                if (locMap != null) {
                    lat = ((Number) locMap.get("lat")).doubleValue();
                    lon = ((Number) locMap.get("lon")).doubleValue();
                }

                return GetGlobalSuggestionRes.of(
                    (String) source.get("storeName"),
                    (String) source.get("category"),
                    (String) source.get("address"),
                    SuggestionType.STORE,
                    ((Number) source.get("storeId")).longValue(),
                    lat,
                    lon
                );
            })
            .toList());

        return new GetGlobalSuggestionListRes(res);
    }

    private BenefitType getBenefitType(Brand brand, Benefit benefit) {
        return brand.getIsLocal() ? BenefitType.LOCAL :
            benefit.getRank() == Rank.NONE ? BenefitType.VIP :
                BenefitType.NORMAL;
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
