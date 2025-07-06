package com.ureca.uble.domain.users.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ureca.uble.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByProviderId(String ProviderId);
}
