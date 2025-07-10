package com.ureca.uble.domain.users.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ureca.uble.domain.brand.repository.CategoryRepository;
import com.ureca.uble.domain.users.dto.request.UserInfoReq;
import com.ureca.uble.domain.users.dto.response.UserInfoRes;
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

	@Transactional
	public UserInfoRes getUserInfo(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new GlobalException(UserErrorCode.USER_NOT_FOUND));

		List<Long> categoryIds = userCategoryRepository.findByUser(user).stream()
			.map(uc -> uc.getCategory().getId())
			.toList();

		return UserInfoRes.of(user, categoryIds);
	}

	@Transactional
	public UserInfoRes updateUserInfo(Long userId, UserInfoReq request) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new GlobalException(UserErrorCode.USER_NOT_FOUND));

		user.updateUserInfo(
			request.getRank(),
			request.getGender(),
			request.getBirthDate()
		);

		userCategoryRepository.deleteByUser(user);

		List<Category> categories = categoryRepository.findAllById(request.getCategoryIds());
		categories.forEach(category -> {
			UserCategory userCategory = UserCategory.of(user, category);
			userCategoryRepository.save(userCategory);
		});

		return UserInfoRes.of(user, request.getCategoryIds());
	}
}
