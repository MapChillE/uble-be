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
import com.ureca.uble.entity.enums.Rank;
import com.ureca.uble.entity.enums.RankType;
import com.ureca.uble.global.exception.GlobalException;
import com.ureca.uble.global.response.CursorPageRes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsageHistoryServiceTest {

	@InjectMocks
	private UsageHistoryService usageHistoryService;

	@Mock
	private UserRepository userRepository;
	@Mock
	private StoreRepository storeRepository;
	@Mock
	private UsageHistoryRepository usageHistoryRepository;
	@Mock
	private BenefitRepository benefitRepository;
	@Mock
	private UsageCountRepository usageCountRepository;

	@Test
	@DisplayName("사용자 ID로 이용내역을 조회한다.")
	void getUsageHistorySuccess(){
		//given
		Long userId = 1L;
		Long lastHistoryId = null;
		int size = 10;

		List<UsageHistoryRes> content = List.of(
			UsageHistoryRes.of(1L, "스타벅스 선릉점", LocalDateTime.now())
		);
		CursorPageRes<UsageHistoryRes> expectedResult = CursorPageRes.of(content, false, null);
		when(usageHistoryRepository.findUsagesByUserId(userId, lastHistoryId, size)).thenReturn(expectedResult);

		//when
		CursorPageRes<UsageHistoryRes> result = usageHistoryService.getUsageHistory(userId, lastHistoryId, size);

		//then
		assertThat(result).isNotNull();
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.isHasNext()).isFalse();
		verify(usageHistoryRepository).findUsagesByUserId(userId, lastHistoryId, size);
	}

	@Test
	@DisplayName("VIP 혜택 사용 기록을 등록한다.")
	void createUsageHistory_vip_success() {
		// given
		Long userId = 1L;
		Long storeId = 100L;

		User vipUser = mock(User.class);
		Store mockStore = mock(Store.class);
		Brand mockBrand = mock(Brand.class);
		when(vipUser.getRank()).thenReturn(Rank.VIP);
		when(mockStore.getBrand()).thenReturn(mockBrand);
		when(vipUser.getIsVipAvailable()).thenReturn(true);
		when(mockBrand.getRankType()).thenReturn(RankType.VIP);
		when(userRepository.findById(userId)).thenReturn(Optional.of(vipUser));
		when(storeRepository.findByIdWithBrand(storeId)).thenReturn(Optional.of(mockStore));

		UsageHistory savedHistory = mock(UsageHistory.class);
		when(savedHistory.getId()).thenReturn(10L);
		when(usageHistoryRepository.save(any())).thenReturn(savedHistory);

		// when
		CreateUsageHistoryRes res = usageHistoryService.createUsageHistory(userId, storeId, new CreateUsageHistoryReq(BenefitType.VIP));

		// then
		assertThat(res.getId()).isEqualTo(10L);
		verify(vipUser).updateVipAvailability(false);
	}


	@Test
	@DisplayName("VIP 혜택을 사용할 수 없는 경우 에러가 발생한다.")
	void createUsageHistory_vip_fail() {
		// given
		Long userId = 1L;
		Long storeId = 100L;

		User vipUser = mock(User.class);
		Store mockStore = mock(Store.class);
		Brand mockBrand = mock(Brand.class);
		when(vipUser.getRank()).thenReturn(Rank.VIP);
		when(mockStore.getBrand()).thenReturn(mockBrand);
		when(mockBrand.getRankType()).thenReturn(RankType.VIP);
		when(userRepository.findById(userId)).thenReturn(Optional.of(vipUser));
		when(storeRepository.findByIdWithBrand(storeId)).thenReturn(Optional.of(mockStore));

		// when
		GlobalException exception = assertThrows(GlobalException.class, () -> {
			usageHistoryService.createUsageHistory(userId, storeId, new CreateUsageHistoryReq(BenefitType.VIP));
		});

		// then
		assertEquals(2001, exception.getResultCode().getCode());
		verify(vipUser, never()).updateVipAvailability(anyBoolean());
		verify(usageHistoryRepository, never()).save(any());
	}

	@Test
	@DisplayName("Local 혜택 사용 기록을 등록한다.")
	void createUsageHistory_local_success() {
		// given
		Long userId = 1L;
		Long storeId = 100L;

		User localUser = mock(User.class);
		Store mockStore = mock(Store.class);
		Brand mockBrand = mock(Brand.class);

		when(localUser.getRank()).thenReturn(Rank.VIP);
		when(localUser.getIsLocalAvailable()).thenReturn(true);
		when(mockStore.getBrand()).thenReturn(mockBrand);
		when(mockBrand.getRankType()).thenReturn(RankType.LOCAL);
		when(userRepository.findById(userId)).thenReturn(Optional.of(localUser));
		when(storeRepository.findByIdWithBrand(storeId)).thenReturn(Optional.of(mockStore));

		UsageHistory savedHistory = mock(UsageHistory.class);
		when(savedHistory.getId()).thenReturn(10L);
		when(usageHistoryRepository.save(any())).thenReturn(savedHistory);

		// when
		CreateUsageHistoryRes res = usageHistoryService.createUsageHistory(userId, storeId, new CreateUsageHistoryReq(BenefitType.LOCAL));

		// then
		assertThat(res.getId()).isEqualTo(10L);
		verify(localUser).updateLocalAvailability(false);
	}

	@Test
	@DisplayName("Local 혜택을 사용할 수 없는 경우 에러가 발생한다.")
	void createUsageHistory_local_fail() {
		// given
		Long userId = 1L;
		Long storeId = 100L;

		User localUser = mock(User.class);
		Store mockStore = mock(Store.class);
		Brand mockBrand = mock(Brand.class);
		when(mockStore.getBrand()).thenReturn(mockBrand);
		when(mockBrand.getRankType()).thenReturn(RankType.LOCAL);
		when(userRepository.findById(userId)).thenReturn(Optional.of(localUser));
		when(storeRepository.findByIdWithBrand(storeId)).thenReturn(Optional.of(mockStore));

		// when
		GlobalException exception = assertThrows(GlobalException.class, () ->
			usageHistoryService.createUsageHistory(userId, storeId, new CreateUsageHistoryReq(BenefitType.LOCAL))
		);

		// then
		assertEquals(2001, exception.getResultCode().getCode()); // BENEFIT_NOT_AVAILABLE
		verify(localUser, never()).updateLocalAvailability(anyBoolean());
		verify(usageHistoryRepository, never()).save(any());
	}

	@Test
	@DisplayName("기본 혜택 사용 기록을 처음으로 등록한다.")
	void createUsageHistory_normal_first_time() {
		// given
		Long userId = 1L;
		Long storeId = 100L;

		User normalUser = mock(User.class);
		Store mockStore = mock(Store.class);
		Brand mockBrand = mock(Brand.class);
		when(mockStore.getBrand()).thenReturn(mockBrand);
		when(mockBrand.getRankType()).thenReturn(RankType.NORMAL);
		when(userRepository.findById(userId)).thenReturn(Optional.of(normalUser));
		when(storeRepository.findByIdWithBrand(storeId)).thenReturn(Optional.of(mockStore));

		Benefit benefit = mock(Benefit.class);
		when(benefit.getNumber()).thenReturn(3);
		when(benefitRepository.findNormalBenefitByStoreId(storeId)).thenReturn(Optional.of(benefit));
		when(usageCountRepository.findByUserAndBenefit(normalUser, benefit)).thenReturn(Optional.empty());

		UsageHistory savedHistory = mock(UsageHistory.class);
		when(savedHistory.getId()).thenReturn(10L);
		when(usageHistoryRepository.save(any())).thenReturn(savedHistory);

		// when
		CreateUsageHistoryRes res = usageHistoryService.createUsageHistory(userId, storeId, new CreateUsageHistoryReq(BenefitType.NORMAL));

		// then
		assertThat(res.getId()).isEqualTo(10L);
		verify(usageCountRepository).save(any());
		verify(usageHistoryRepository).save(any());
	}

	@Test
	@DisplayName("기본 혜택을 사용할 수 없는 경우 에러가 발생한다.")
	void createUsageHistory_normal_exceed_fail() {
		// given
		Long userId = 1L;
		Long storeId = 100L;

		User normalUser = mock(User.class);
		Store mockStore = mock(Store.class);
		Brand mockBrand = mock(Brand.class);

		when(mockStore.getBrand()).thenReturn(mockBrand);
		when(mockBrand.getRankType()).thenReturn(RankType.NORMAL);
		when(userRepository.findById(userId)).thenReturn(Optional.of(normalUser));
		when(storeRepository.findByIdWithBrand(storeId)).thenReturn(Optional.of(mockStore));

		Benefit benefit = mock(Benefit.class);
		when(benefit.getNumber()).thenReturn(2);
		when(benefitRepository.findNormalBenefitByStoreId(storeId)).thenReturn(Optional.of(benefit));

		UsageCount usageCount = mock(UsageCount.class);
		when(usageCount.getCount()).thenReturn(2);
		when(usageCountRepository.findByUserAndBenefit(normalUser, benefit)).thenReturn(Optional.of(usageCount));

		// when
		GlobalException exception = assertThrows(GlobalException.class, () ->
			usageHistoryService.createUsageHistory(userId, storeId, new CreateUsageHistoryReq(BenefitType.NORMAL))
		);

		// then
		assertEquals(2001, exception.getResultCode().getCode()); // BENEFIT_NOT_AVAILABLE
		verify(usageCount, never()).update(anyInt(), anyBoolean());
		verify(usageHistoryRepository, never()).save(any());
	}

	@Test
	@DisplayName("해당 매장에서 사용할 수 없는 혜택을 선택한 경우 에러가 발생한다.")
	void createUsageHistory_storeBenefitCheck_fails() {
		// given
		Long userId = 1L;
		Long storeId = 2L;

		User mockUser = mock(User.class);
		Store mockStore = mock(Store.class);
		Brand mockBrand = mock(Brand.class);

		when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
		when(storeRepository.findByIdWithBrand(storeId)).thenReturn(Optional.of(mockStore));
		when(mockStore.getBrand()).thenReturn(mockBrand);

		// when
		GlobalException exception = assertThrows(GlobalException.class, () ->
			usageHistoryService.createUsageHistory(userId, storeId, new CreateUsageHistoryReq(BenefitType.NORMAL))
		);

		// then
		assertEquals(2001, exception.getResultCode().getCode()); // BENEFIT_NOT_AVAILABLE
		verify(usageHistoryRepository, never()).save(any());
	}
}
