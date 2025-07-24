package com.ureca.uble.domain.users.service;

import com.ureca.uble.domain.brand.repository.BenefitRepository;
import com.ureca.uble.domain.store.repository.StoreRepository;
import com.ureca.uble.domain.users.dto.request.CreateUsageHistoryReq;
import com.ureca.uble.domain.users.dto.response.CreateUsageHistoryRes;
import com.ureca.uble.domain.users.dto.response.UsageHistoryListRes;
import com.ureca.uble.domain.users.repository.UsageCountRepository;
import com.ureca.uble.domain.users.repository.UsageHistoryDocumentRepository;
import com.ureca.uble.domain.users.repository.UserRepository;
import com.ureca.uble.entity.*;
import com.ureca.uble.entity.document.UsageHistoryDocument;
import com.ureca.uble.entity.enums.BenefitType;
import com.ureca.uble.entity.enums.Gender;
import com.ureca.uble.entity.enums.Rank;
import com.ureca.uble.entity.enums.RankType;
import com.ureca.uble.global.exception.GlobalException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZonedDateTime;
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
	private BenefitRepository benefitRepository;
	@Mock
	private UsageCountRepository usageCountRepository;
	@Mock
	private UsageHistoryDocumentRepository usageHistoryDocumentRepository;

	@Test
	@DisplayName("사용자 ID로 이용내역을 조회한다.")
	void getUsageHistorySuccess() {
		Long userId = 1L;
		int page = 0;
		int size = 10;
		int year = 2025;
		int month = 7;

		LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
		LocalDateTime end = LocalDateTime.of(year, month, YearMonth.of(year, month).lengthOfMonth(), 23, 59);

		UsageHistoryDocument document = UsageHistoryDocument.builder()
			.id("1")
			.userId(userId)
			.storeName("스타벅스 선릉점")
			.createdAt(ZonedDateTime.now())
			.category("tmpCategory")
			.brandImageUrl("example.com")
			.build();

		SearchHit<UsageHistoryDocument> hit = mock(SearchHit.class);
		when(hit.getContent()).thenReturn(document);

		SearchHits<UsageHistoryDocument> searchHits = mock(SearchHits.class);
		when(usageHistoryDocumentRepository.findByUserIdAndCreatedAtBetween(userId, start, end, page, size))
			.thenReturn(searchHits);
		when(searchHits.getTotalHits()).thenReturn(1L);
		when(searchHits.getSearchHits()).thenReturn(List.of(hit));

		UsageHistoryListRes result = usageHistoryService.getUsageHistory(userId, year, month, page, size);

		assertThat(result).isNotNull();
		assertThat(result.getTotalCount()).isEqualTo(1);
		assertThat(result.getHistoryList()).hasSize(1);
		assertThat(result.getHistoryList().get(0).getStoreName()).isEqualTo("스타벅스 선릉점");
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
		Category mockCategory = mock(Category.class);

		when(vipUser.getRank()).thenReturn(Rank.VIP);
		when(vipUser.getIsVipAvailable()).thenReturn(true);
		when(vipUser.getGender()).thenReturn(Gender.FEMALE);
		when(mockStore.getBrand()).thenReturn(mockBrand);
		when(mockStore.getAddress()).thenReturn("서울 강남구");
		when(mockBrand.getRankType()).thenReturn(RankType.VIP);
		when(mockBrand.getCategory()).thenReturn(mockCategory);
		when(mockCategory.getName()).thenReturn("푸드");
		when(userRepository.findById(userId)).thenReturn(Optional.of(vipUser));
		when(storeRepository.findByIdWithBrandAndCategory(storeId)).thenReturn(Optional.of(mockStore));

		UsageHistoryDocument savedHistory = mock(UsageHistoryDocument.class);
		when(savedHistory.getId()).thenReturn("savedId");
		when(usageHistoryDocumentRepository.save(any())).thenReturn(savedHistory);

		// when
		CreateUsageHistoryRes res = usageHistoryService.createUsageHistory(userId, storeId, new CreateUsageHistoryReq(BenefitType.VIP));

		// then
		assertThat(res.getId()).isEqualTo("savedId");
		verify(vipUser).updateVipAvailability(false);
		verify(usageHistoryDocumentRepository).save(any());
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
		when(storeRepository.findByIdWithBrandAndCategory(storeId)).thenReturn(Optional.of(mockStore));

		// when
		GlobalException exception = assertThrows(GlobalException.class, () -> {
			usageHistoryService.createUsageHistory(userId, storeId, new CreateUsageHistoryReq(BenefitType.VIP));
		});

		// then
		assertEquals(2001, exception.getResultCode().getCode());
		verify(vipUser, never()).updateVipAvailability(anyBoolean());
		verify(usageHistoryDocumentRepository, never()).save(any());
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
		Category mockCategory = mock(Category.class);

		when(localUser.getRank()).thenReturn(Rank.VIP);
		when(localUser.getIsLocalAvailable()).thenReturn(true);
		when(localUser.getGender()).thenReturn(Gender.FEMALE);
		when(mockStore.getBrand()).thenReturn(mockBrand);
		when(mockStore.getAddress()).thenReturn("서울 강남구");
		when(mockBrand.getRankType()).thenReturn(RankType.LOCAL);
		when(mockBrand.getCategory()).thenReturn(mockCategory);
		when(mockCategory.getName()).thenReturn("푸드");
		when(userRepository.findById(userId)).thenReturn(Optional.of(localUser));
		when(storeRepository.findByIdWithBrandAndCategory(storeId)).thenReturn(Optional.of(mockStore));

		UsageHistoryDocument savedHistory = mock(UsageHistoryDocument.class);
		when(savedHistory.getId()).thenReturn("savedId");
		when(usageHistoryDocumentRepository.save(any())).thenReturn(savedHistory);

		// when
		CreateUsageHistoryRes res = usageHistoryService.createUsageHistory(userId, storeId, new CreateUsageHistoryReq(BenefitType.LOCAL));

		// then
		assertThat(res.getId()).isEqualTo("savedId");
		verify(localUser).updateLocalAvailability(false);
		verify(usageHistoryDocumentRepository).save(any());
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
		when(storeRepository.findByIdWithBrandAndCategory(storeId)).thenReturn(Optional.of(mockStore));

		// when
		GlobalException exception = assertThrows(GlobalException.class, () ->
			usageHistoryService.createUsageHistory(userId, storeId, new CreateUsageHistoryReq(BenefitType.LOCAL))
		);

		// then
		assertEquals(2001, exception.getResultCode().getCode()); // BENEFIT_NOT_AVAILABLE
		verify(localUser, never()).updateLocalAvailability(anyBoolean());
		verify(usageHistoryDocumentRepository, never()).save(any());
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
		Category mockCategory = mock(Category.class);

		when(mockStore.getBrand()).thenReturn(mockBrand);
		when(mockStore.getAddress()).thenReturn("서울 강남구");
		when(mockBrand.getRankType()).thenReturn(RankType.NORMAL);
		when(normalUser.getGender()).thenReturn(Gender.FEMALE);
		when(normalUser.getRank()).thenReturn(Rank.VIP);
		when(mockBrand.getCategory()).thenReturn(mockCategory);
		when(mockCategory.getName()).thenReturn("푸드");
		when(userRepository.findById(userId)).thenReturn(Optional.of(normalUser));
		when(storeRepository.findByIdWithBrandAndCategory(storeId)).thenReturn(Optional.of(mockStore));

		Benefit benefit = mock(Benefit.class);
		when(benefit.getNumber()).thenReturn(3);
		when(benefitRepository.findNormalBenefitByStoreId(storeId)).thenReturn(Optional.of(benefit));
		when(usageCountRepository.findByUserAndBenefit(normalUser, benefit)).thenReturn(Optional.empty());

		UsageHistoryDocument savedHistory = mock(UsageHistoryDocument.class);
		when(savedHistory.getId()).thenReturn("savedId");
		when(usageHistoryDocumentRepository.save(any())).thenReturn(savedHistory);

		// when
		CreateUsageHistoryRes res = usageHistoryService.createUsageHistory(userId, storeId, new CreateUsageHistoryReq(BenefitType.NORMAL));

		// then
		assertThat(res.getId()).isEqualTo("savedId");
		verify(usageCountRepository).save(any());
		verify(usageHistoryDocumentRepository).save(any());
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
		when(storeRepository.findByIdWithBrandAndCategory(storeId)).thenReturn(Optional.of(mockStore));

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
		verify(usageHistoryDocumentRepository, never()).save(any());
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
		when(storeRepository.findByIdWithBrandAndCategory(storeId)).thenReturn(Optional.of(mockStore));
		when(mockStore.getBrand()).thenReturn(mockBrand);

		// when
		GlobalException exception = assertThrows(GlobalException.class, () ->
			usageHistoryService.createUsageHistory(userId, storeId, new CreateUsageHistoryReq(BenefitType.NORMAL))
		);

		// then
		assertEquals(2001, exception.getResultCode().getCode()); // BENEFIT_NOT_AVAILABLE
		verify(usageHistoryDocumentRepository, never()).save(any());
	}
}
