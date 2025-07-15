package com.ureca.uble.domain.feedback.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ureca.uble.entity.Feedback;
import com.ureca.uble.entity.User;
import org.springframework.data.jpa.repository.Query;


public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
	void deleteByUser(User user);

	@EntityGraph(attributePaths = "user")
	@Query("SELECT f FROM Feedback f")
	Page<Feedback> listFeedbacks(Pageable pageable);

}
