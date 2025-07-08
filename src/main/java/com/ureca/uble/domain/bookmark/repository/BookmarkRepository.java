package com.ureca.uble.domain.bookmark.repository;

import com.ureca.uble.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long>, CustomBookmarkRepository {
    boolean existsByBrand_IdAndUser_Id(Long brandId, Long userId);
}
