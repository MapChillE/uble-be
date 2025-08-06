package com.ureca.uble.domain.store.service;

import co.elastic.clients.elasticsearch.core.MsearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.google.common.geometry.S2CellId;
import com.google.common.geometry.S2LatLng;
import com.google.common.geometry.S2LatLngRect;
import com.google.common.geometry.S2RegionCoverer;
import com.ureca.uble.domain.bookmark.repository.BookmarkRepository;
import com.ureca.uble.domain.common.repository.CustomElasticRepository;
import com.ureca.uble.domain.store.dto.response.*;
import com.ureca.uble.domain.store.repository.LocationCoordinationDocumentRepository;
import com.ureca.uble.domain.store.repository.StoreClickLogDocumentRepository;
import com.ureca.uble.domain.store.repository.StoreRepository;
import com.ureca.uble.domain.users.repository.UsageCountRepository;
import com.ureca.uble.domain.users.repository.UserRepository;
import com.ureca.uble.entity.*;
import com.ureca.uble.entity.document.StoreClickLogDocument;
import com.ureca.uble.entity.enums.*;
import com.ureca.uble.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
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
    private final CustomElasticRepository customElasticRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final int MAP_MAX_ZOOM = 21;
    private static final int FULL_RETURN_ZOOM_THRESHOLD = 16;
    private static final int MIN_ZOOM_LEVEL = 12;

    /**
     * 근처 매장 정보 조회
     */
    @Transactional(readOnly = true)
    public GetStoreListRes getStores(int zoomLevel, double swLat, double swLng, double neLat, double neLng,
                                     Long categoryId, Long brandId, Season season, BenefitType type) {

        // 입력값 검증 (줌 레벨, 좌표 범위)
        validateZoomAndRange(zoomLevel, swLat, swLng, neLat, neLng);

        // 클러스터링 없이 모든 매장 반환
        if (zoomLevel >= FULL_RETURN_ZOOM_THRESHOLD) {
            List<Store> rawStores = storeRepository.findStoresInBox(
                    swLng, swLat, neLng, neLat,
                    categoryId, brandId, season, type
            );
            List<GetStoreRes> fullList = rawStores.stream()
                    .map(GetStoreRes::from)
                    .toList();
            return new GetStoreListRes(zoomLevel, fullList);
        }

        // 낮은 줌 레벨
        return createClusteredStoreListResponseFromDB(zoomLevel, swLat, swLng, neLat, neLng,
                categoryId, brandId, season, type);
    }

    /**
     * S2 셀 기반 클러스터링 후 대표 매장 반환 (DB 접근 1회)
     */
    private GetStoreListRes createClusteredStoreListResponseFromDB(int zoomLevel, double swLat, double swLng,
                                                                   double neLat, double neLng, Long categoryId,
                                                                   Long brandId, Season season, BenefitType type) {
        // 커버링 셀 계산
        int cellLevel = getCellLevelFromZoom(zoomLevel);
        List<S2CellId> coveringCells = getCoveringCells(swLat, swLng, neLat, neLng, cellLevel);
        if (coveringCells.isEmpty()) {
            return new GetStoreListRes(zoomLevel, List.of());
        }

        // 캐시 조회 및 분류
        List<String> cacheKeys = buildCacheKeys(coveringCells, categoryId, brandId, season, type);
        StoreCacheResult scan = scanCache(coveringCells, cacheKeys);

        List<GetStoreRes> finalStoreList = new ArrayList<>(scan.getHitStores());
        // 캐시 미스 셀에 대해서만 DB 조회 및 대표 매장 선출
        if (!scan.getMissCells().isEmpty()) {
            Map<Long, Store> representatives = findRepresentativesByCells(scan.getMissCells(), cellLevel, categoryId, brandId, season, type);

            // 대표 매장 응답 변환 및 캐시 저장
            if (!representatives.isEmpty()) {
                Map<String, GetStoreRes> toCache = new HashMap<>();
                for (Map.Entry<Long, Store> e : representatives.entrySet()) {
                    Store store = e.getValue();
                    String key = buildCacheKey(new S2CellId(e.getKey()), categoryId, brandId, season, type);
                    toCache.put(key, GetStoreRes.from(store));
                    finalStoreList.add(GetStoreRes.from(store));
                }
                cacheMultiSetWithTtl(toCache);
            }

            // 빈 셀 마커 캐시 저장
            Map<String, EmptyStoreRes> emptyMarkers = new HashMap<>();
            for (S2CellId missed : scan.getMissCells()) {
                if (!representatives.containsKey(missed.id())) {
                    String key = buildCacheKey(missed, categoryId, brandId, season, type);
                    emptyMarkers.put(key, new EmptyStoreRes());
                }
            }
            if (!emptyMarkers.isEmpty()) {
                cacheMultiSetEmptyWithTtl(emptyMarkers);
            }
        }

        return new GetStoreListRes(zoomLevel, finalStoreList);
    }

    // 줌 레벨에 맞는 s2 셀로 영역 생성
    private List<S2CellId> getCoveringCells(double swLat, double swLng, double neLat, double neLng, int cellLevel) {
        S2LatLngRect rect = S2LatLngRect.fromPointPair(
                S2LatLng.fromDegrees(swLat, swLng),
                S2LatLng.fromDegrees(neLat, neLng)
        );
        S2RegionCoverer coverer = S2RegionCoverer.builder()
                .setMinLevel(cellLevel)
                .setMaxLevel(cellLevel)
                .build();

        ArrayList<S2CellId> cells = new ArrayList<>();
        coverer.getCovering(rect, cells);
        return cells;
    }

    //각 셀 ID에 Redis 캐시 키 생성
    private List<String> buildCacheKeys(List<S2CellId> cells, Long categoryId, Long brandId, Season season, BenefitType type) {
        return cells.stream()
                .map(cell -> buildCacheKey(cell, categoryId, brandId, season, type))
                .toList();
    }

    //Hit/Miss된 셀 목록으로 분류
    private StoreCacheResult scanCache(List<S2CellId> coveringCells, List<String> cacheKeys) {
        List<Object> cached = redisTemplate.opsForValue().multiGet(cacheKeys);
        List<GetStoreRes> hits = new ArrayList<>();
        List<S2CellId> misses = new ArrayList<>();
        List<Object> safeCached = Objects.requireNonNullElseGet(cached, List::of);

        for (int i = 0; i < safeCached.size(); i++) {
            Object obj = safeCached.get(i);
            if (obj instanceof GetStoreRes res) {
                hits.add(res);
                log.info("Map-Cache Hit for key: {}", cacheKeys.get(i));
            } else if (obj instanceof EmptyStoreRes) {
                log.info("Map-Cache Empty hit for cellId: {}", coveringCells.get(i).toToken());
            } else {
                misses.add(coveringCells.get(i));
                log.info("Map-Cache Miss for cellId: {}", coveringCells.get(i).toToken());
            }
        }
        return new StoreCacheResult(hits, misses);
    }

    //대표 매장 선정
    private Map<Long, Store> findRepresentativesByCells(
            List<S2CellId> missedCells, int cellLevel,
            Long categoryId, Long brandId, Season season, BenefitType type
    ) {
        List<Store> stores = storeRepository.findStoresInCellRanges(missedCells, categoryId, brandId, season, type);

        Map<Long, Store> representatives = new HashMap<>();
        for (Store store : stores) {
            S2LatLng latLng = S2LatLng.fromDegrees(store.getLocation().getY(), store.getLocation().getX());
            S2CellId parent = S2CellId.fromLatLng(latLng).parent(cellLevel);

            Store current = representatives.get(parent.id());
            if (current == null || store.getVisitCount() > current.getVisitCount()) {
                representatives.put(parent.id(), store);
            }
        }
        return representatives;
    }

    private void cacheMultiSetWithTtl(Map<String, GetStoreRes> data) {
        redisTemplate.opsForValue().multiSet(data);
        redisTemplate.executePipelined((RedisCallback<Object>) conn -> {
            for (String key : data.keySet()) {
                conn.keyCommands().expire(key.getBytes(), Duration.ofDays(7).getSeconds());
            }
            return null;
        });
    }

    private void cacheMultiSetEmptyWithTtl(Map<String, EmptyStoreRes> empties) {
        redisTemplate.opsForValue().multiSet(empties);
        redisTemplate.executePipelined((RedisCallback<Object>) conn -> {
            for (String key : empties.keySet()) {
                conn.keyCommands().expire(key.getBytes(), Duration.ofMinutes(10).getSeconds());
            }
            return null;
        });
    }

    private String buildCacheKey(S2CellId cellId, Long categoryId, Long brandId, Season season, BenefitType type) {
        return String.format("stores:cell:%d:cat:%s:brand:%s:season:%s:type:%s",
                cellId.id(), categoryId, brandId, season, type);
    }

    private int getCellLevelFromZoom(int zoomLevel) {
        return zoomLevel;
    }

    private void validateZoomAndRange(int zoomLevel, double swLat, double swLng, double neLat, double neLng) {
        if (swLat >= neLat || swLng >= neLng) {
            throw new GlobalException(OUT_OF_RANGE_INPUT);
        }
        if (zoomLevel < MIN_ZOOM_LEVEL || zoomLevel > MAP_MAX_ZOOM) {
            throw new GlobalException(OUT_OF_RANGE_INPUT);
        }
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

        // 위경도 설정 + 브랜드, 카테고리 결과 받기
        int CATEGORY_SUGGESTION_SIZE = 2;
        int BRAND_SUGGESTION_SIZE = 2;
        MsearchResponse<Map> firstResponse;
        try {
            firstResponse = customElasticRepository.findCoordinationAndBrandAndCategoryWithMSearch(keyword, CATEGORY_SUGGESTION_SIZE, BRAND_SUGGESTION_SIZE);
        } catch (Exception e) {
            throw new GlobalException(ELASTIC_INTERNAL_ERROR);
        }

        // 카테고리 매핑
        List<GetGlobalSuggestionRes> res = new ArrayList<>();
        res.addAll(firstResponse.responses().get(0).result().hits().hits().stream()
            .map(Hit::source).filter(Objects::nonNull)
            .map(source -> GetGlobalSuggestionRes.of(
                (String) source.get("categoryName"),
                null, null,
                SuggestionType.CATEGORY,
                ((Number) source.get("categoryId")).longValue(),
                null, null
            ))
            .toList());

        // brand 조회
        res.addAll(firstResponse.responses().get(1).result().hits().hits().stream()
            .map(Hit::source).filter(Objects::nonNull)
            .map(source -> GetGlobalSuggestionRes.of(
                (String) source.get("brandName"),
                (String) source.get("category"),
                null,
                SuggestionType.BRAND,
                ((Number) source.get("brandId")).longValue(),
                null, null
            ))
            .toList());

        // 위경도 재설정
        Map<String, Object> locationMap = firstResponse.responses().get(2).result().hits().hits().stream()
            .map(Hit::source)
            .filter(Objects::nonNull)
            .map(src -> (Map<String, Object>) src.get("location"))
            .findFirst()
            .orElse(null);

        if(locationMap != null) {
            latitude = (Double) locationMap.get("lat");
            longitude = (Double) locationMap.get("lon");
        }

        // 최종 검색 실행
        MsearchResponse<Map> secondResponse;
        try {
            secondResponse = customElasticRepository.findMapSuggestionsByKeywordWithMsearch(keyword, size - (res.size()), latitude, longitude, res);
        } catch (Exception e) {
            throw new GlobalException(ELASTIC_INTERNAL_ERROR);
        }

        // brand/category 위경도 추가
        for (int i = 1; i < secondResponse.responses().size(); i++) {
            Map<String, Object> locRes = secondResponse.responses().get(i).result().hits().hits().stream()
                .map(Hit::source).filter(Objects::nonNull)
                .map(src -> (Map<String, Object>) src.get("location"))
                .findFirst()
                .orElse(null);

            if(locRes != null) {
                res.get(i - 1).update((Double) locRes.get("lat"), (Double) locRes.get("lon"));
            }
        }

        // store조회
        res.addAll(secondResponse.responses().get(0).result().hits().hits().stream()
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
