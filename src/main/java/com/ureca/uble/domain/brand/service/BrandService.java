package com.ureca.uble.domain.brand.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ureca.uble.domain.brand.dto.response.BenefitRes;
import com.ureca.uble.domain.brand.dto.response.BrandDetailRes;
import com.ureca.uble.domain.brand.exception.BrandErrorCode;
import com.ureca.uble.domain.brand.repository.BenefitRepository;
import com.ureca.uble.domain.brand.repository.BrandRepository;
import com.ureca.uble.global.exception.GlobalException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BrandService {

	private final BrandRepository brandRepository;
	private final BenefitRepository benefitRepository;

	public BrandDetailRes getBrandDetail(Long userId, Long brandId) {
		BrandDetailRes detail = brandRepository.findBrandDetailById(brandId, userId)
			.orElseThrow(() ->  new GlobalException(BrandErrorCode.BRAND_NOT_FOUND));

		List<BenefitRes> benefits = benefitRepository.findAllByBrandId(brandId);

		return BrandDetailRes.of(detail, benefits);
	}
}
