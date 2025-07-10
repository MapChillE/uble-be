package com.ureca.uble.domain.brand.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ureca.uble.domain.bookmark.repository.BookmarkRepository;
import com.ureca.uble.domain.brand.dto.response.BenefitDetailRes;
import com.ureca.uble.domain.brand.dto.response.BenefitListRes;
import com.ureca.uble.domain.brand.dto.response.BrandDetailRes;
import com.ureca.uble.domain.brand.dto.response.BrandListRes;
import com.ureca.uble.domain.brand.exception.BrandErrorCode;
import com.ureca.uble.domain.brand.repository.BrandRepository;
import com.ureca.uble.entity.Bookmark;
import com.ureca.uble.entity.Brand;
import com.ureca.uble.entity.enums.Season;
import com.ureca.uble.global.exception.GlobalException;
import com.ureca.uble.global.response.CursorPageRes;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BrandService {

	private final BrandRepository brandRepository;
	private final BookmarkRepository bookmarkRepository;

	@Transactional(readOnly = true)
	public BrandDetailRes getBrandDetail(Long userId, Long brandId) {
		Brand brand = brandRepository.findWithBenefitsById(brandId)
			.orElseThrow(() -> new GlobalException(BrandErrorCode.BRAND_NOT_FOUND));

		Optional<Bookmark> optionalBookmark = bookmarkRepository.findByUserIdAndBrandId(userId, brandId);
		boolean isBookmarked = optionalBookmark.isPresent();
		Long bookmarkId = optionalBookmark.map(Bookmark::getId).orElse(null);

		List<BenefitDetailRes> benefits = brand.getBenefits().stream()
			.map(BenefitDetailRes::from)
			.toList();

		return BrandDetailRes.of(brand, isBookmarked, bookmarkId, benefits);
	}

	@Transactional(readOnly = true)
	public CursorPageRes<BrandListRes> getBrandList(Long userId, Long categoryId, Season season, Boolean isLocal, Long lastBrandId, int size) {

		List<Brand> brands = brandRepository.findWithFilterAndCursor(categoryId, season, isLocal, lastBrandId, size+1);

		boolean hasNext = brands.size() > size;
		if (hasNext) {
			brands.remove(size);
		}

		List<BrandListRes> brandList = brands.stream().map(brand -> {
			Optional<Bookmark> optionalBookmark = bookmarkRepository.findByUserIdAndBrandId(userId, brand.getId());
			boolean isBookmarked = optionalBookmark.isPresent();
			Long bookmarkId = optionalBookmark.map(Bookmark::getId).orElse(null);

			List<BenefitListRes> benefits = brand.getBenefits().stream()
				.map(BenefitListRes::from)
				.toList();

			return BrandListRes.of(brand, isBookmarked, bookmarkId, benefits);
		}).toList();

		Long lastCursorId = brandList.isEmpty() ? null : brandList.get(brandList.size() - 1).getBrandId();

		return CursorPageRes.of(brandList, hasNext, lastCursorId);
	}
}
