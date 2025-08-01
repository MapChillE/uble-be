package com.ureca.uble.domain.admin.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.ureca.uble.domain.admin.dto.response.AdminCodeRes;
import com.ureca.uble.domain.admin.exception.AdminErrorCode;
import com.ureca.uble.domain.users.repository.UserRepository;
import com.ureca.uble.entity.User;
import com.ureca.uble.entity.enums.Gender;
import com.ureca.uble.entity.enums.Rank;
import com.ureca.uble.entity.enums.Role;
import com.ureca.uble.global.exception.GlobalException;
import com.ureca.uble.global.security.jwt.JwtProvider;

import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {

	@InjectMocks
	private AdminService adminService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private JwtProvider jwtProvider;

	@Mock
	private HttpServletResponse response;

	private final String adminCode = "adminCode";

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(adminService, "adminCode", adminCode);
	}

	@Test
	@DisplayName("관리자 코드 검증에 성공하면 토큰이 헤더 및 쿠키에 설정된다.")
	void verifyAdminSuccess() {
		//given
		Long userId = 1L;
		String providedCode = "adminCode";
		String accessToken = "access.token.value";
		String refreshToken = "refresh.token.value";

		User adminUser = User.createAdminUser("admin-kakao-id", "관리자");

		when(userRepository.findById(userId)).thenReturn(Optional.of(adminUser));
		when(jwtProvider.createAccessToken(adminUser)).thenReturn(accessToken);
		when(jwtProvider.createRefreshToken(adminUser)).thenReturn(refreshToken);

		//when
		AdminCodeRes res = adminService.verifyAdmin(userId, providedCode, response);

		//then
		verify(jwtProvider).addAccessTokenHeader(response, accessToken);
		verify(jwtProvider).addRefreshTokenCookie(response, refreshToken);
	}

	@Test
	@DisplayName("유저가 ADMIN이 아닌 경우 관리자 코드 검증에 실패한다.")
	void verifyAdminFailNotAdmin() {
		//given
		Long userId = 1L;
		String providedCode = "adminCode";
		User user = User.createTmpUser("user-id", "일반 유저");

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));

		// when
		GlobalException exception = catchThrowableOfType(
			() -> adminService.verifyAdmin(userId, providedCode, response),
			GlobalException.class
		);

		// then
		assertThat(exception.getResultCode()).isEqualTo(AdminErrorCode.NOT_ADMIN);
	}

	@Test
	@DisplayName("관리자 코드가 일치하지 않는 경우 관리자 코드 검증에 실패한다.")
	void verifyAdminFailInvalidCode(){
		//given
		Long userId = 1L;
		String providedCode = "Code";

		User adminUser = User.createAdminUser("admin-kakao-id", "관리자");

		when(userRepository.findById(userId)).thenReturn(Optional.of(adminUser));

		// when
		GlobalException exception = catchThrowableOfType(
			() -> adminService.verifyAdmin(userId, providedCode, response),
			GlobalException.class
		);

		// then
		assertThat(exception.getResultCode()).isEqualTo(AdminErrorCode.INVALID_ADMIN_CODE);
	}

}
