package com.ureca.uble.domain.bookmark.repository;

import com.ureca.uble.entity.Bookmark;
import com.ureca.uble.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long>, CustomBookmarkRepository {

    boolean existsByBrand_IdAndUser_Id(Long brandId, Long userId);

    Optional<Bookmark> findByUserIdAndBrandId(Long userId, Long brandId);

    void deleteByUser(User user);

    @Query("SELECT b FROM Bookmark b JOIN FETCH b.brand WHERE b.user.id = :userId AND b.brand.id IN :brandIds")
    List<Bookmark> findWithBrandByUserIdAndBrandIdIn(Long userId, List<Long> brandIds);
}
