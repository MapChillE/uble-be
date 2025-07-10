package com.ureca.uble.domain.users.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ureca.uble.domain.brand.repository.CategoryRepository;
import com.ureca.uble.domain.users.dto.request.UpdateUserInfoReq;
import com.ureca.uble.domain.users.dto.response.GetUserInfoRes;
import com.ureca.uble.domain.users.dto.response.UpdateUserInfoRes;
import com.ureca.uble.domain.users.exception.UserErrorCode;
import com.ureca.uble.domain.users.repository.UserCategoryRepository;
import com.ureca.uble.domain.users.repository.UserRepository;
import com.ureca.uble.entity.Category;
import com.ureca.uble.entity.User;
import com.ureca.uble.entity.UserCategory;
import com.ureca.uble.entity.enums.Gender;
import com.ureca.uble.entity.enums.Rank;
import com.ureca.uble.global.exception.GlobalException;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private UserCategoryRepository userCategoryRepository;

	@Mock
	private CategoryRepository categoryRepository;

	@InjectMocks
	private UserService userService;

	@Test
	@DisplayName("사용자 ID로 사용자 정보를 조회한다.")
	void getUserInfoSuccess(){
		//given
		Long userId = 1L;
		User user = mock(User.class);
		Category cat1 = mock(Category.class);
		Category cat2 = mock(Category.class);
		when(cat1.getId()).thenReturn(1L);
		when(cat2.getId()).thenReturn(2L);

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(userCategoryRepository.findByUser(user)).thenReturn(List.of(
			UserCategory.of(user, cat1),
			UserCategory.of(user, cat2)
		));

		//when
		GetUserInfoRes result = userService.getUserInfo(userId);

		//then
		assertThat(result).isNotNull();
		assertThat(result.getCategoryIds()).containsExactly(1L, 2L);
		verify(userRepository).findById(userId);
		verify(userCategoryRepository).findByUser(user);
	}

	@Test
	@DisplayName("사용자가 존재하지 않을 시 사용자 정보 조회에 실패한다.")
	void getUserInfo_userNotFound(){
		//given
		when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

		//when, then
		GlobalException exception = assertThrows(GlobalException.class, () ->
			userService.getUserInfo(99L)
		);
		assertThat(exception.getResultCode()).isEqualTo(UserErrorCode.USER_NOT_FOUND);
	}

	@Test
	@DisplayName("사용자 ID로 사용자 정보를 수정한다.")
	void updateUserInfoSuccess(){
		//given
		Long userId = 1L;
		User user = mock(User.class);
		List<Long> categoryIds = List.of(10L, 20L);
		Category cat1 = mock(Category.class);
		Category cat2 = mock(Category.class);

		UpdateUserInfoReq request = mock(UpdateUserInfoReq.class);
		when(request.getRank()).thenReturn(Rank.VIP);
		when(request.getGender()).thenReturn(Gender.FEMALE);
		when(request.getBirthDate()).thenReturn(LocalDate.of(1999, 1, 1));
		when(request.getCategoryIds()).thenReturn(categoryIds);

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(categoryRepository.findAllById(categoryIds)).thenReturn(List.of(cat1, cat2));

		//when
		UpdateUserInfoRes result = userService.updateUserInfo(userId, request);

		//then
		verify(user).updateUserInfo(Rank.VIP, Gender.FEMALE, LocalDate.of(1999, 1, 1));
		verify(userCategoryRepository).deleteByUser(user);
		verify(userCategoryRepository, times(2)).save(any(UserCategory.class));

		assertThat(result).isNotNull();
		assertThat(result.getCategoryIds()).containsExactly(10L, 20L);
	}

	@Test
	@DisplayName("사용자가 존재하지 않을 시 사용자 정보 갱신에 실패한다.")
	void updateUserInfo_userNotFound(){
		//given
		when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

		UpdateUserInfoReq req = mock(UpdateUserInfoReq.class);

		//when, then
		GlobalException ex = assertThrows(GlobalException.class, () ->
			userService.updateUserInfo(123L, req)
		);
		assertThat(ex.getResultCode()).isEqualTo(UserErrorCode.USER_NOT_FOUND);
	}
}
