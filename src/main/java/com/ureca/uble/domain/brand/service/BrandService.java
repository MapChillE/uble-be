package com.ureca.uble.domain.brand.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import com.ureca.uble.domain.brand.dto.response.*;
import com.ureca.uble.entity.document.BrandNoriDocument;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ureca.uble.domain.bookmark.repository.BookmarkRepository;
import com.ureca.uble.domain.brand.exception.BrandErrorCode;
import com.ureca.uble.domain.brand.repository.BrandRepository;
import com.ureca.uble.entity.Bookmark;
import com.ureca.uble.entity.Brand;
import com.ureca.uble.entity.enums.BenefitType;
import com.ureca.uble.entity.enums.Rank;
import com.ureca.uble.entity.enums.RankType;
import com.ureca.uble.entity.enums.Season;
import com.ureca.uble.global.exception.GlobalException;
import com.ureca.uble.global.response.CursorPageRes;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BrandService {

	private final BrandRepository brandRepository;
	private final BookmarkRepository bookmarkRepository;
	private final ElasticsearchOperations elasticsearchOperations;


	@Transactional(readOnly = true)
	public BrandDetailRes getBrandDetail(Long userId, Long brandId) {
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

		return BrandDetailRes.of(brand, isBookmarked, bookmarkId, isVIPcock, benefits);
	}

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
	@Transactional
	public SearchBrandListRes getBrandListBySearch(Long userId, String keyword, String category, Season season, BenefitType type, int page, int size) {
		// 쿼리 작성
		Query multiMatchQuery = MultiMatchQuery.of(m -> m
			.query(keyword)
			.fields("brandName^10","category^20", "season^20")
			.fuzziness("AUTO")
		)._toQuery();

		// filter 적용
		List<Query> filters = new ArrayList<>();
		if (category != null && !category.isEmpty()) {
			Query categoryFilter = TermQuery.of(t -> t
				.field("category.raw")
				.value(category)
			)._toQuery();
			filters.add(categoryFilter);
		}

		if (season != null) {
			Query seasonFilter = TermQuery.of(t -> t
				.field("season.raw")
				.value(season.toString())
			)._toQuery();
			filters.add(seasonFilter);
		}

		if (type != null) {
			List<FieldValue> rankTypeValues = switch (type) {
                case VIP -> List.of(FieldValue.of("VIP"), FieldValue.of("VIP_NORMAL"));
                case NORMAL -> List.of(FieldValue.of("NORMAL"), FieldValue.of("VIP_NORMAL"));
                case LOCAL -> List.of(FieldValue.of("LOCAL"));
            };

            Query typeFilter = TermsQuery.of(t -> t
                .field("rankType")
                .terms(terms -> terms.value(rankTypeValues.stream().map(FieldValue::of).toList()))
            )._toQuery();
            filters.add(typeFilter);
        }

		// 조합
		Query boolQuery = BoolQuery.of(b -> b
			.must(multiMatchQuery)
			.filter(filters)
		)._toQuery();

		NativeQuery nativeQuery = NativeQuery.builder()
			.withQuery(boolQuery)
			.withPageable(PageRequest.of(page, size))
			.build();

		// 요청 및 결과 수집
		SearchHits<BrandNoriDocument> searchHits = this.elasticsearchOperations.search(
			nativeQuery, BrandNoriDocument.class
		);

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

		return SearchBrandListRes.of(brandList, totalCnt, totalPage);
	}
}
