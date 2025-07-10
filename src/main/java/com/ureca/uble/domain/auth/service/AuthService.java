package com.ureca.uble.domain.auth.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.ureca.uble.domain.auth.api.KakaoOauthClient;
import com.ureca.uble.domain.auth.dto.response.KakaoUserRes;
import com.ureca.uble.domain.auth.dto.response.WithdrawRes;
import com.ureca.uble.domain.auth.exception.AuthErrorCode;
import com.ureca.uble.domain.bookmark.repository.BookmarkRepository;
import com.ureca.uble.domain.brand.repository.BenefitRepository;
import com.ureca.uble.domain.feedback.repository.FeedbackRepository;
import com.ureca.uble.domain.token.repository.TokenRepository;
import com.ureca.uble.domain.users.exception.UserErrorCode;
import com.ureca.uble.domain.users.repository.PinRepository;
import com.ureca.uble.domain.users.repository.UsageCountRepository;
import com.ureca.uble.domain.users.repository.UsageHistoryRepository;
import com.ureca.uble.domain.users.repository.UserCategoryRepository;
import com.ureca.uble.domain.users.repository.UserRepository;
import com.ureca.uble.entity.Token;
import com.ureca.uble.entity.User;
import com.ureca.uble.global.exception.GlobalException;
import com.ureca.uble.global.security.jwt.JwtProvider;
import com.ureca.uble.global.security.jwt.JwtValidator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final TokenRepository tokenRepository;
	private final JwtProvider jwtProvider;
	private final JwtValidator jwtValidator;
	private final KakaoOauthClient kakaoOauthClient;
	private final UserCategoryRepository userCategoryRepository;
	private final UsageHistoryRepository usageHistoryRepository;
	private final BookmarkRepository bookmarkRepository;
	private final PinRepository pinRepository;
	private final UsageCountRepository usageCountRepository;
	private final FeedbackRepository feedbackRepository;

	@Transactional
	public User login(String code, HttpServletResponse response) {
		String kakaoAccessToken = kakaoOauthClient.getAccessToken(code);
		KakaoUserRes userInfo = kakaoOauthClient.getUserInfo(kakaoAccessToken);

		String kakaoId = String.valueOf(userInfo.getId());
		String nickname = userInfo.getKakao_account().getProfile().getNickname();

		User user = userRepository.findByProviderId(kakaoId)
			.orElseGet(() -> userRepository.save(User.createTmpUser(kakaoId, nickname)));

		String accessToken = jwtProvider.createAccessToken(user);
		String refreshToken = jwtProvider.createRefreshToken(user);
		LocalDateTime expiryTime = jwtProvider.getRefreshTokenExpiry(refreshToken);

		tokenRepository.findByUser(user)
			.ifPresentOrElse(
				token -> token.updateRefreshToken(refreshToken, expiryTime),
				() -> tokenRepository.save(Token.of(user, refreshToken, expiryTime))
			);

		jwtProvider.addAccessTokenHeader(response, accessToken);
		jwtProvider.addRefreshTokenCookie(response, refreshToken);

		return user;
	}

	public void reissue(String refreshToken, HttpServletResponse response){
		if (!jwtValidator.validateToken(refreshToken)) {
			throw new GlobalException(AuthErrorCode.INVALID_TOKEN);
		}
		Long userId = jwtValidator.getUserIdFromToken(refreshToken);

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new GlobalException(UserErrorCode.USER_NOT_FOUND));

		Token token = tokenRepository.findByRefreshToken(refreshToken)
			.orElseThrow(() -> new GlobalException(AuthErrorCode.INVALID_TOKEN));


		String newAccessToken = jwtProvider.createAccessToken(user);
		String newRefreshToken = jwtProvider.createRefreshToken(user);
		LocalDateTime newExpiry = jwtProvider.getRefreshTokenExpiry(newRefreshToken);

		token.updateRefreshToken(newRefreshToken, newExpiry);
		tokenRepository.save(token);

		jwtProvider.addAccessTokenHeader(response, newAccessToken);
		jwtProvider.addRefreshTokenCookie(response, newRefreshToken);
	}

	@Transactional
	public void logout(String refreshToken, HttpServletResponse response){

		if (!jwtValidator.validateToken(refreshToken)) {
			throw new GlobalException(AuthErrorCode.INVALID_TOKEN);
		}

		Long userId = jwtValidator.getUserIdFromToken(refreshToken);
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new GlobalException(UserErrorCode.USER_NOT_FOUND));

		tokenRepository.deleteByUser(user);

		jwtProvider.deleteRefreshTokenCookie(response);
	}

	@Transactional
	public WithdrawRes withdraw(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new GlobalException(UserErrorCode.USER_NOT_FOUND));

		if(user.getIsDeleted()){
			throw new GlobalException(UserErrorCode.USER_ALREADY_DELETED);
		}

		user.updateIsDeleted();

		tokenRepository.deleteByUser(user);
		pinRepository.deleteByUser(user);
		userCategoryRepository.deleteByUser(user);
		usageHistoryRepository.deleteByUser(user);
		usageCountRepository.deleteByUser(user);
		feedbackRepository.deleteByUser(user);
		bookmarkRepository.deleteByUser(user);

		return new WithdrawRes();
	}
}
