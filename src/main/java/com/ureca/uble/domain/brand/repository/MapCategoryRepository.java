package com.ureca.uble.domain.brand.repository;

import com.ureca.uble.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MapCategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByOrderByIdAsc();
}
