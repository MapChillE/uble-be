package com.ureca.uble.domain.brand.service;

import co.elastic.clients.elasticsearch.core.MsearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.ureca.uble.domain.bookmark.repository.BookmarkRepository;
import com.ureca.uble.domain.brand.dto.response.*;
import com.ureca.uble.domain.brand.repository.BrandClickLogDocumentRepository;
import com.ureca.uble.domain.brand.repository.BrandNoriDocumentRepository;
import com.ureca.uble.domain.brand.repository.BrandRepository;
import com.ureca.uble.domain.common.dto.response.CursorPageRes;
import com.ureca.uble.domain.common.repository.CustomElasticRepository;
import com.ureca.uble.domain.store.repository.SearchLogDocumentRepository;
import com.ureca.uble.domain.store.repository.StoreRepository;
import com.ureca.uble.domain.users.repository.UserRepository;
import com.ureca.uble.entity.Bookmark;
import com.ureca.uble.entity.Brand;
import com.ureca.uble.entity.Store;
import com.ureca.uble.entity.User;
import com.ureca.uble.entity.document.BrandClickLogDocument;
import com.ureca.uble.entity.document.BrandNoriDocument;
import com.ureca.uble.entity.document.SearchLogDocument;
import com.ureca.uble.entity.enums.*;
import com.ureca.uble.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.ureca.uble.domain.brand.exception.BrandErrorCode.BRAND_NOT_FOUND;
import static com.ureca.uble.domain.brand.exception.BrandErrorCode.BRAND_NOT_OFFLINE;
import static com.ureca.uble.domain.common.exception.CommonErrorCode.ELASTIC_INTERNAL_ERROR;
import static com.ureca.uble.domain.users.exception.UserErrorCode.USER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrandService {

	private final BrandRepository brandRepository;
	private final BookmarkRepository bookmarkRepository;
	private final BrandNoriDocumentRepository brandNoriDocumentRepository;
	private final BrandClickLogDocumentRepository brandClickLogDocumentRepository;
	private final UserRepository userRepository;
	private final SearchLogDocumentRepository searchLogDocumentRepository;
	private final CustomElasticRepository customElasticRepository;
	private final StoreRepository storeRepository;

	/**
	 * 제휴처 상세 조회
	 */
	@Transactional(readOnly = true)
	public BrandDetailRes getBrandDetail(Long userId, Long brandId) {
		User user = findUser(userId);
		Brand brand = brandRepository.findWithBenefitsById(brandId)
			.orElseThrow(() -> new GlobalException(BRAND_NOT_FOUND));

		Optional<Bookmark> optionalBookmark = bookmarkRepository.findByUserIdAndBrandId(userId, brandId);
		boolean isBookmarked = optionalBookmark.isPresent();
		Long bookmarkId = optionalBookmark.map(Bookmark::getId).orElse(null);

		List<BenefitDetailRes> benefits = brand.getBenefits().stream()
			.map(benefit -> {
				String type = benefit.getRank() == Rank.NONE ? "VIP" : "NORMAL";
				return BenefitDetailRes.of(benefit, type);
			})
			.toList();

		boolean isVIPcock = brand.isVIPcock();

		// 로그 저장
		try {
			brandClickLogDocumentRepository.save(BrandClickLogDocument.of(user, brand));
		} catch (Exception e) {
			log.warn("제휴처 상세 조회 로그 저장에 실패하였습니다 : {}", e.getMessage());
		}

		return BrandDetailRes.of(brand, isBookmarked, bookmarkId, isVIPcock, benefits);
	}

	/**
	 * 제휴처 전체 조회
	 */
	@Transactional(readOnly = true)
	public CursorPageRes<BrandListRes> getBrandList(Long userId, Long categoryId, Season season, BenefitType type, BenefitCategory benefitCategory, Long lastBrandId, int size) {

		List<RankType> rankTypes = null;

		if (type != null) {
			switch (type) {
				case VIP -> rankTypes = List.of(RankType.VIP, RankType.VIP_NORMAL);
				case LOCAL -> rankTypes = List.of(RankType.LOCAL);
				case NORMAL -> rankTypes = List.of(RankType.NORMAL, RankType.VIP_NORMAL);
			}
		}

		List<Brand> brands = brandRepository.findWithFilterAndCursor(categoryId, season, rankTypes, benefitCategory, lastBrandId, size+1);

		boolean hasNext = brands.size() > size;
		if (hasNext) {
			brands.remove(size);
		}

		List<Long> bookmarkedBrandIdList = bookmarkRepository.findAllByUser(userId);
		Set<Long> bookmarkedBrandIdSet = new HashSet<>(bookmarkedBrandIdList);

		List<BrandListRes> brandList = brands.stream()
			.map(brand -> BrandListRes.of(
				brand,
				bookmarkedBrandIdSet.contains(brand.getId()),
				brand.isVIPcock(),
				brand.getMinRank()
			))
			.toList();

		Long lastCursorId = brandList.isEmpty() ? null : brandList.get(brandList.size() - 1).getBrandId();

		return CursorPageRes.of(brandList, hasNext, lastCursorId);
	}

	/**
	 * (검색) 제휴처 전체 조회
	 */
	@Transactional(readOnly = true)
	public SearchBrandListRes getBrandListBySearch(Long userId, String keyword, String category, Season season, BenefitType type, int page, int size) {
		User user = findUser(userId);
		SearchHits<BrandNoriDocument> searchHits = brandNoriDocumentRepository.findAllByFilteringAndPage(keyword, category, season, type, page, size);

		// 북마크 정보 수집
		List<Long> bookmarkedBrandIdList = bookmarkRepository.findAllByUser(userId);
		Set<Long> bookmarkedBrandIdSet = new HashSet<>(bookmarkedBrandIdList);

		// 최종 결과 반환
		long totalCnt = searchHits.getTotalHits();
		long totalPage = totalCnt % size == 0 ? totalCnt / size : totalCnt / size + 1;
		List<BrandListRes> brandList = searchHits.stream()
			.map(hit -> {
				BrandNoriDocument document = hit.getContent();
				boolean isBookmarked = bookmarkedBrandIdSet.contains(document.getBrandId());
				return BrandListRes.of(document, isBookmarked);
			})
			.toList();

		// 로그 기록
		try {
			searchLogDocumentRepository.save(SearchLogDocument.of(user, SearchType.ENTER, keyword, totalCnt > 0));
		} catch (Exception e) {
			log.warn("검색 로그 저장에 실패하였습니다 : {}", e.getMessage());
		}

		return SearchBrandListRes.of(brandList, totalCnt, totalPage);
	}

	/**
	 * 제휴처 검색 자동완성
	 */
	public BrandSuggestionListRes getBrandSuggestionList(String keyword, int size) {
		MsearchResponse<Map> response;
		try {
			response = customElasticRepository.findBrandSuggestionsByKeywordWithMsearch(keyword, 2, size - 2);
		} catch (Exception e) {
			throw new GlobalException(ELASTIC_INTERNAL_ERROR);
		}

		List<SuggestionRes> res = new ArrayList<>();

		// 카테고리 매핑
		res.addAll(response.responses().get(0).result().hits().hits().stream()
			.map(Hit::source).filter(Objects::nonNull)
			.map(source -> SuggestionRes.of(
				(String) source.get("categoryName"),
				SuggestionType.CATEGORY
			))
			.toList());

		// brand 조회
		res.addAll(response.responses().get(1).result().hits().hits().stream()
			.map(Hit::source).filter(Objects::nonNull)
			.map(source -> SuggestionRes.of(
				(String) source.get("brandName"),
				SuggestionType.BRAND
			))
			.toList());

		return new BrandSuggestionListRes(res);
	}

	/**
	 * offline 브랜드 목록(이름+이미지) 조회
	 */
	@Transactional(readOnly = true)
	public CursorPageRes<OfflineBrandRes> getOfflineBrands(Long lastBrandId, int size) {
		List<Brand> brands = brandRepository.findOfflineAfterCursor(lastBrandId, size + 1);

		List<OfflineBrandRes> content = brands.stream()
				.map(b -> OfflineBrandRes.of(b.getId(), b.getName(), b.getImageUrl()))
				.toList();
		boolean hasNext = content.size() > size;
		if (hasNext) {content = content.subList(0, size);}

		Long newLastId = content.isEmpty() ? null : content.get(content.size() - 1).getId();
		return CursorPageRes.of(content, hasNext, newLastId);
	}

	/**
	 * 가장 가까운 제휴처 매장 위경도 조회
	 */
    public GetNearestStoreRes getNearestStoreCoordination(Double latitude, Double longitude, Long brandId) {
		Brand brand = findBrand(brandId);
		if(brand.getIsOnline()) {
			throw new GlobalException(BRAND_NOT_OFFLINE);
		}

		Store store = storeRepository.findNearestByBrandId(brandId, latitude, longitude);
		if (store.getLocation() == null || store.getLocation().isEmpty()) {
			throw new GlobalException(BRAND_NOT_OFFLINE);
		}
		return GetNearestStoreRes.from(store);
    }

	private Brand findBrand(Long brandId) {
		return brandRepository.findById(brandId).orElseThrow(() -> new GlobalException(BRAND_NOT_FOUND));
	}

	private User findUser(Long userId) {
		return userRepository.findById(userId).orElseThrow(() -> new GlobalException(USER_NOT_FOUND));
	}
}
