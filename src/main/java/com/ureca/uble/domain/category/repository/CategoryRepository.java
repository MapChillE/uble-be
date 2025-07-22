package com.ureca.uble.domain.category.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ureca.uble.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
	List<Category> findAllByIdIn(List<Long> ids);
	List<Category> findByOrderByIdAsc();
}
