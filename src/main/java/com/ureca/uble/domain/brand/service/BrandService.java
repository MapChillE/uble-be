package com.ureca.uble.domain.brand.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ureca.uble.domain.bookmark.repository.BookmarkRepository;
import com.ureca.uble.domain.brand.dto.response.BenefitRes;
import com.ureca.uble.domain.brand.dto.response.BrandDetailRes;
import com.ureca.uble.domain.brand.exception.BrandErrorCode;
import com.ureca.uble.domain.brand.repository.BenefitRepository;
import com.ureca.uble.domain.brand.repository.BrandRepository;
import com.ureca.uble.entity.Bookmark;
import com.ureca.uble.entity.Brand;
import com.ureca.uble.global.exception.GlobalException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BrandService {

	private final BrandRepository brandRepository;
	private final BenefitRepository benefitRepository;
	private final BookmarkRepository bookmarkRepository;

	public BrandDetailRes getBrandDetail(Long userId, Long brandId) {
		Brand brand = brandRepository.findWithBenefitsById(brandId)
			.orElseThrow(() -> new GlobalException(BrandErrorCode.BRAND_NOT_FOUND));

		Optional<Bookmark> optionalBookmark = bookmarkRepository.findByUserIdAndBrandId(userId, brandId);
		boolean isBookmarked = optionalBookmark.isPresent();
		Long bookmarkId = optionalBookmark.map(Bookmark::getId).orElse(null);

		List<BenefitRes> benefits = brand.getBenefits().stream()
			.map(BenefitRes::from)
			.toList();

		return BrandDetailRes.of(brand, isBookmarked, bookmarkId, benefits);
	}
}
