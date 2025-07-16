package com.ureca.uble.domain.users.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ureca.uble.entity.Token;
import com.ureca.uble.entity.User;

public interface TokenRepository extends JpaRepository<Token, Long> {
	Optional<Token> findByUser(User user);
	Optional<Token> findByRefreshToken(String refreshToken);
	void deleteByUser(User user);
}
