package com.ureca.uble.domain.feedback.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ureca.uble.entity.Feedback;
import com.ureca.uble.entity.User;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
	void deleteByUser(User user);
}
