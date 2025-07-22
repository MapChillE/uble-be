package com.ureca.uble.domain.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ureca.uble.entity.Pin;
import com.ureca.uble.entity.User;

import java.util.List;

public interface PinRepository extends JpaRepository<Pin, Long> {
	void deleteByUser(User user);
	List<Pin> findByUserIdOrderByIdAsc(Long userId);
}
