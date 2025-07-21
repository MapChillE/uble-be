package com.ureca.uble.domain.users.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import com.ureca.uble.domain.category.repository.CategoryRepository;
import com.ureca.uble.domain.users.dto.request.UpdateUserInfoReq;
import com.ureca.uble.domain.users.dto.response.GetRecommmendationListRes;
import com.ureca.uble.domain.users.dto.response.GetUserInfoRes;
import com.ureca.uble.domain.users.dto.response.UpdateUserInfoRes;
import com.ureca.uble.domain.users.exception.UserErrorCode;
import com.ureca.uble.domain.users.repository.UserCategoryRepository;
import com.ureca.uble.domain.users.repository.UserRepository;
import com.ureca.uble.entity.Category;
import com.ureca.uble.entity.User;
import com.ureca.uble.entity.UserCategory;
import com.ureca.uble.global.exception.GlobalException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final UserCategoryRepository userCategoryRepository;
	private final CategoryRepository categoryRepository;
	private final WebClient fastapiWebClient;

	@Transactional(readOnly = true)
	public GetUserInfoRes getUserInfo(Long userId) {
		User user = findUser(userId);

		List<Long> categoryIds = userCategoryRepository.findByUser(user).stream()
			.map(uc -> uc.getCategory().getId())
			.toList();

		return GetUserInfoRes.of(user, categoryIds);
	}

	@Transactional
	public UpdateUserInfoRes updateUserInfo(Long userId, UpdateUserInfoReq request) {
		User user = findUser(userId);

		user.updateUserInfo(
			request.getRank(),
			request.getGender(),
			request.getBirthDate(),
			request.getBarcode()
		);

		userCategoryRepository.deleteByUser(user);

		List<Category> categories = categoryRepository.findAllById(request.getCategoryIds());
		categories.forEach(category -> {
			UserCategory userCategory = UserCategory.of(user, category);
			userCategoryRepository.save(userCategory);
		});

		return UpdateUserInfoRes.of(user, request.getCategoryIds());
	}

	private User findUser(Long userId){
		return userRepository.findById(userId)
			.orElseThrow(() -> new GlobalException(UserErrorCode.USER_NOT_FOUND));
	}

	public GetRecommmendationListRes getRecommendations(Long userId) {
		return fastapiWebClient.get()
			.uri(uriBuilder -> uriBuilder
				.path("api/recommend/hybrid")
				.queryParam("user_id", userId)
				.build())
			.retrieve()
			.bodyToMono(GetRecommmendationListRes.class)
			.block();
	}
}
