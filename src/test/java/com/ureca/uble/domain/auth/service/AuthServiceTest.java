package com.ureca.uble.domain.auth.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ureca.uble.domain.auth.api.KakaoOauthClient;
import com.ureca.uble.domain.auth.dto.response.KakaoUserRes;
import com.ureca.uble.domain.token.repository.TokenRepository;
import com.ureca.uble.domain.users.repository.UserRepository;
import com.ureca.uble.entity.Token;
import com.ureca.uble.entity.User;
import com.ureca.uble.global.security.jwt.JwtProvider;
import com.ureca.uble.global.security.jwt.JwtValidator;

import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

	@InjectMocks
	private AuthService authService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private TokenRepository tokenRepository;

	@Mock
	private JwtProvider jwtProvider;

	@Mock
	private JwtValidator jwtValidator;

	@Mock
	private KakaoOauthClient kakaoOauthClient;

	@Mock
	private HttpServletResponse response;

	private final String accessToken = "access-token";
	private final String refreshToken = "refresh-token";
	private final LocalDateTime expiry = LocalDateTime.now().plusDays(7);
	private final Long userId = 1L;

	@Test
	@DisplayName("신규 유저의 경우 유저 저장 후 토큰을 발급한다.")
	void loginNewUser(){
		//given
		String code = "kakao-code";
		Long providerId = 12345L;
		String nickname = "tester";
		User user = User.createTmpUser(providerId.toString(), nickname);

		KakaoUserRes kakaoUserRes = mock(KakaoUserRes.class);
		KakaoUserRes.KakaoAccount account = mock(KakaoUserRes.KakaoAccount.class);
		KakaoUserRes.KakaoAccount.Profile profile = mock(KakaoUserRes.KakaoAccount.Profile.class);

		when(profile.getNickname()).thenReturn(nickname);
		when(account.getProfile()).thenReturn(profile);
		when(kakaoUserRes.getId()).thenReturn(providerId);
		when(kakaoUserRes.getKakao_account()).thenReturn(account);
		when(kakaoOauthClient.getAccessToken(code)).thenReturn(accessToken);
		when(kakaoOauthClient.getUserInfo(accessToken)).thenReturn(kakaoUserRes);
		when(userRepository.findByProviderId(providerId.toString())).thenReturn(Optional.empty());
		when(userRepository.save(any(User.class))).thenReturn(user);
		when(tokenRepository.findByUser(user)).thenReturn(Optional.empty());
		when(jwtProvider.createAccessToken(user)).thenReturn(accessToken);
		when(jwtProvider.createRefreshToken(user)).thenReturn(refreshToken);
		when(jwtProvider.getRefreshTokenExpiry(refreshToken)).thenReturn(expiry);

		//when
		User result = authService.login(code, response);

		//then
		assertThat(result).isEqualTo(user);
		verify(tokenRepository).save(any(Token.class));
		verify(jwtProvider).addAccessTokenHeader(response, accessToken);
		verify(jwtProvider).addRefreshTokenCookie(response, refreshToken);
	}

	@Test
	@DisplayName("유효한 리프레시 토큰으로 토큰을 재발급한다.")
	void reissueValidToken(){
		//given
		User user = User.createTmpUser("12345", "tester");
		Token token = Token.of(user, refreshToken, expiry);
		when(jwtValidator.validateToken(refreshToken)).thenReturn(true);
		when(jwtValidator.getUserIdFromToken(refreshToken)).thenReturn(userId);
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(tokenRepository.findByRefreshToken(refreshToken)).thenReturn(Optional.of(token));
		when(jwtProvider.createAccessToken(user)).thenReturn(accessToken);
		when(jwtProvider.createRefreshToken(user)).thenReturn(refreshToken);
		when(jwtProvider.getRefreshTokenExpiry(refreshToken)).thenReturn(expiry);

		// when
		authService.reissue(refreshToken, response);

		// then
		verify(jwtProvider).addAccessTokenHeader(response, accessToken);
		verify(jwtProvider).addRefreshTokenCookie(response, refreshToken);
		verify(tokenRepository).save(token);
	}

	@Test
	@DisplayName("로그아웃 성공 시 토큰을 삭제하고 쿠키를 제거한다.")
	void logoutSuccess() {
		// given
		User user = User.createTmpUser("12345", "tester");
		when(jwtValidator.validateToken(refreshToken)).thenReturn(true);
		when(jwtValidator.getUserIdFromToken(refreshToken)).thenReturn(userId);
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));

		// when
		authService.logout(refreshToken, response);

		// then
		verify(tokenRepository).deleteByUser(user);
		verify(jwtProvider).deleteRefreshTokenCookie(response);
	}
}
