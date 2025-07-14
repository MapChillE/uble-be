package com.ureca.uble.domain.feedback.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ureca.uble.entity.Feedback;
import com.ureca.uble.entity.User;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
	void deleteByUser(User user);

	@EntityGraph(attributePaths = "user")
	Page<Feedback> findAll(Pageable pageable);
}
