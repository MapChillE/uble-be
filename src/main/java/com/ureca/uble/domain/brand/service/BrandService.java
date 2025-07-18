package com.ureca.uble.domain.brand.service;

import com.ureca.uble.domain.bookmark.repository.BookmarkRepository;
import com.ureca.uble.domain.brand.dto.response.BenefitDetailRes;
import com.ureca.uble.domain.brand.dto.response.BrandDetailRes;
import com.ureca.uble.domain.brand.dto.response.BrandListRes;
import com.ureca.uble.domain.brand.dto.response.SearchBrandListRes;
import com.ureca.uble.domain.brand.exception.BrandErrorCode;
import com.ureca.uble.domain.brand.repository.BrandClickLogDocumentRepository;
import com.ureca.uble.domain.brand.repository.BrandNoriDocumentRepository;
import com.ureca.uble.domain.brand.repository.BrandRepository;
import com.ureca.uble.domain.common.dto.response.CursorPageRes;
import com.ureca.uble.domain.store.repository.SearchLogDocumentRepository;
import com.ureca.uble.domain.users.repository.UserRepository;
import com.ureca.uble.entity.Bookmark;
import com.ureca.uble.entity.Brand;
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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

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

	/**
	 * 제휴처 상세 조회
	 */
	@Transactional(readOnly = true)
	public BrandDetailRes getBrandDetail(Long userId, Long brandId) {
		User user = findUser(userId);
		Brand brand = brandRepository.findWithBenefitsById(brandId)
			.orElseThrow(() -> new GlobalException(BrandErrorCode.BRAND_NOT_FOUND));

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
	public CursorPageRes<BrandListRes> getBrandList(Long userId, Long categoryId, Season season, BenefitType type, Long lastBrandId, int size) {

		List<RankType> rankTypes = null;

		if (type != null) {
			switch (type) {
				case VIP -> rankTypes = List.of(RankType.VIP, RankType.VIP_NORMAL);
				case LOCAL -> rankTypes = List.of(RankType.LOCAL);
				case NORMAL -> rankTypes = List.of(RankType.NORMAL, RankType.VIP_NORMAL);
			}
		}

		List<Brand> brands = brandRepository.findWithFilterAndCursor(categoryId, season, rankTypes, lastBrandId, size+1);

		boolean hasNext = brands.size() > size;
		if (hasNext) {
			brands.remove(size);
		}

		List<BrandListRes> brandList = brands.stream().map(brand -> {
			Optional<Bookmark> optionalBookmark = bookmarkRepository.findByUserIdAndBrandId(userId, brand.getId());
			boolean isBookmarked = optionalBookmark.isPresent();
			Long bookmarkId = optionalBookmark.map(Bookmark::getId).orElse(null);

			boolean isVIPcock = brand.isVIPcock();
			Rank minRank = brand.getMinRank();

			return BrandListRes.of(brand, isBookmarked, bookmarkId, isVIPcock, minRank);
		}).toList();

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
		List<Long> brandIds = searchHits.stream()
			.map(hit -> hit.getContent().getBrandId())
			.toList();

		List<Bookmark> bookmarks = bookmarkRepository.findWithBrandByUserIdAndBrandIdIn(userId, brandIds);

		Map<Long, Bookmark> bookmarkMap = bookmarks.stream()
			.collect(Collectors.toMap(b ->
				b.getBrand().getId(),
				Function.identity()
			));

		// 최종 결과 반환
		long totalCnt = searchHits.getTotalHits();
		long totalPage = totalCnt % size == 0 ? totalCnt / size : totalCnt / size + 1;
		List<BrandListRes> brandList = searchHits.stream()
			.map(hit -> {
				BrandNoriDocument document = hit.getContent();

				Bookmark bookmark = bookmarkMap.get(document.getBrandId());
				boolean isBookmarked = (bookmark != null);
				Long bookmarkId = (bookmark != null) ? bookmark.getId() : null;

				return BrandListRes.of(document, isBookmarked, bookmarkId);
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

	private User findUser(Long userId) {
		return userRepository.findById(userId).orElseThrow(() -> new GlobalException(USER_NOT_FOUND));
	}
}
