package com.ureca.uble.domain.users.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ureca.uble.entity.User;
import com.ureca.uble.entity.UserCategory;

public interface UserCategoryRepository extends JpaRepository<UserCategory, Long> {
	List<UserCategory> findByUser(User user);
	void deleteByUserId(Long userId);
}
