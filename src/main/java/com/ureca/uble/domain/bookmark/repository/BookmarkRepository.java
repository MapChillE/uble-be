package com.ureca.uble.domain.bookmark.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ureca.uble.entity.Bookmark;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long>, CustomBookmarkRepository {
    boolean existsByBrand_IdAndUser_Id(Long brandId, Long userId);
    Optional<Bookmark> findByUserIdAndBrandId(Long userId, Long brandId);
}
