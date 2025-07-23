package com.ureca.uble.domain.pin.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ureca.uble.entity.Pin;
import com.ureca.uble.entity.User;

import java.util.List;
import java.util.Optional;

public interface PinRepository extends JpaRepository<Pin, Long> {
	void deleteByUser(User user);
	List<Pin> findByUserIdOrderByIdAsc(Long userId);
	long countByUserId(Long userId);
	Optional<Pin> findByIdAndUserId(Long pinId, Long userId);
	void deleteByIdAndUserId(Long pinId, Long userId);
}
