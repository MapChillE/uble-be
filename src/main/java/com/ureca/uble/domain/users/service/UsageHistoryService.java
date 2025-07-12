package com.ureca.uble.domain.users.service;

import com.ureca.uble.domain.brand.repository.BenefitRepository;
import com.ureca.uble.domain.store.repository.StoreRepository;
import com.ureca.uble.domain.users.dto.request.CreateUsageHistoryReq;
import com.ureca.uble.domain.users.dto.response.CreateUsageHistoryRes;
import com.ureca.uble.domain.users.dto.response.UsageHistoryRes;
import com.ureca.uble.domain.users.repository.UsageCountRepository;
import com.ureca.uble.domain.users.repository.UsageHistoryRepository;
import com.ureca.uble.domain.users.repository.UserRepository;
import com.ureca.uble.entity.*;
import com.ureca.uble.entity.enums.BenefitType;
import com.ureca.uble.entity.enums.Period;
import com.ureca.uble.entity.enums.Rank;
import com.ureca.uble.entity.enums.RankType;
import com.ureca.uble.global.exception.GlobalException;
import com.ureca.uble.global.response.CursorPageRes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.ureca.uble.domain.brand.exception.BrandErrorCode.BENEFIT_NOT_FOUND;
import static com.ureca.uble.domain.store.exception.StoreErrorCode.STORE_NOT_FOUND;
import static com.ureca.uble.domain.users.exception.UserErrorCode.BENEFIT_NOT_AVAILABLE;
import static com.ureca.uble.domain.users.exception.UserErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UsageHistoryService {

	private final UsageHistoryRepository usageRepository;
	private final UsageCountRepository usageCountRepository;
	private final UserRepository userRepository;
	private final BenefitRepository benefitRepository;
	private final StoreRepository storeRepository;

	/**
	 * 매장 이용 내역 전체 조회
	 */
	@Transactional(readOnly = true)
	public CursorPageRes<UsageHistoryRes> getUsageHistory(Long userId, Long lastHistoryId, int size) {
		return usageRepository.findUsagesByUserId(userId, lastHistoryId, size);
	}

	/**
	 * 매장 이용 내역 추가
	 */
	@Transactional
	public CreateUsageHistoryRes createUsageHistory(Long userId, Long storeId, CreateUsageHistoryReq req) {
		// 정보 검증
		User user = findUser(userId);
		Store store = findByIdWithBrand(storeId);

		// store 검증
		if(!checkStoreBenefitByType(store, req.getBenefitType())) {
			throw new GlobalException(BENEFIT_NOT_AVAILABLE);
		}

		// 등급에 따른 추가 작업 처리
		switch (req.getBenefitType()) {
			case VIP -> handleVipBenefit(user);
			case LOCAL -> handleLocalBenefit(user);
			case NORMAL -> handleNormalBenefit(user, storeId);
			default -> throw new GlobalException(BENEFIT_NOT_AVAILABLE);
		}

		// 로그 저장
		UsageHistory savedHistory = usageRepository.save(UsageHistory.of(user, store));

		return new CreateUsageHistoryRes(savedHistory.getId());
	}

	private void handleVipBenefit(User user) {
		if ((user.getRank() != Rank.VIP && user.getRank() != Rank.VVIP) || !user.getIsVipAvailable()) {
			throw new GlobalException(BENEFIT_NOT_AVAILABLE);
		}
		user.updateVipAvailability(false); // 사용 완료 처리
	}

	private void handleLocalBenefit(User user) {
		if (user.getRank() == Rank.NORMAL || !user.getIsLocalAvailable()) {
			throw new GlobalException(BENEFIT_NOT_AVAILABLE);
		}
		user.updateLocalAvailability(false); // 사용 완료 처리
	}

	private void handleNormalBenefit(User user, Long storeId) {
		Benefit benefit = findBenefitByStoreId(storeId);
		Optional<UsageCount> optionalCount = usageCountRepository.findByUserAndBenefit(user, benefit);

		if (optionalCount.isEmpty()) { // 새로운 count 생성
			UsageCount usageCount = UsageCount.of(user, benefit, benefit.getNumber() > 1, 1);
			usageCountRepository.save(usageCount);
		} else {
			UsageCount usageCount = optionalCount.get();
			if (benefit.getPeriod() != Period.NONE && benefit.getNumber() <= usageCount.getCount()) {
				throw new GlobalException(BENEFIT_NOT_AVAILABLE);
			}

			boolean isAvailable = (benefit.getPeriod() == Period.NONE) || (benefit.getNumber() > usageCount.getCount() + 1);
			usageCount.update(usageCount.getCount() + 1, isAvailable);
		}
	}

	private boolean checkStoreBenefitByType(Store store, BenefitType type) {
		RankType rankType = store.getBrand().getRankType();
		return switch (type) {
			case VIP -> rankType == RankType.VIP || rankType == RankType.VIP_NORMAL;
			case LOCAL -> rankType == RankType.LOCAL;
			case NORMAL -> rankType == RankType.NORMAL || rankType == RankType.VIP_NORMAL;
		};
	}

	private Benefit findBenefitByStoreId(Long storeId) {
		return benefitRepository.findNormalBenefitByStoreId(storeId).orElseThrow(() -> new GlobalException(BENEFIT_NOT_FOUND));
	}

	private User findUser(Long userId) {
		return userRepository.findById(userId).orElseThrow(() -> new GlobalException(USER_NOT_FOUND));
	}

	private Store findByIdWithBrand(Long storeId) {
		return storeRepository.findByIdWithBrand(storeId).orElseThrow(() -> new GlobalException(STORE_NOT_FOUND));
	}
}
