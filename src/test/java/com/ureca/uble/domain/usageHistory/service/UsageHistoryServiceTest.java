package com.ureca.uble.domain.usageHistory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ureca.uble.domain.usageHistory.dto.response.UsageHistoryRes;
import com.ureca.uble.domain.usageHistory.repository.CustomUsageHistoryRepository;
import com.ureca.uble.global.dto.response.CursorPageRes;

@ExtendWith(MockitoExtension.class)
public class UsageHistoryServiceTest {

	@InjectMocks
	private UsageHistoryService usageHistoryService;

	@Mock
	private CustomUsageHistoryRepository usageHistoryRepository;

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
		CursorPageRes<UsageHistoryRes> expectedResult = CursorPageRes.<UsageHistoryRes>builder()
			.content(content)
			.hasNext(false)
			.lastCursorId(null)
			.build();
		when(usageHistoryRepository.findUsagesByUserId(userId, lastHistoryId, size)).thenReturn(expectedResult);

		//when
		CursorPageRes<UsageHistoryRes> result = usageHistoryService.getUsageHistory(userId, lastHistoryId, size);

		//then
		assertThat(result).isNotNull();
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.isHasNext()).isFalse();
		verify(usageHistoryRepository).findUsagesByUserId(userId, lastHistoryId, size);
	}

}
