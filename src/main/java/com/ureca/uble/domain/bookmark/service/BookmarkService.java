package com.ureca.uble.domain.bookmark.service;

import com.ureca.uble.domain.bookmark.dto.response.CreateBookmarkRes;
import com.ureca.uble.domain.bookmark.dto.response.DeleteBookmarkRes;
import com.ureca.uble.domain.bookmark.repository.BookmarkRepository;
import com.ureca.uble.entity.Bookmark;
import com.ureca.uble.entity.Brand;
import com.ureca.uble.entity.User;
import com.ureca.uble.global.exception.GlobalException;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ureca.uble.domain.auth.exception.AuthErrorCode.UNAUTHORIZED_ACCESS;
import static com.ureca.uble.domain.bookmark.exception.BookmarkErrorCode.BOOKMARK_NOT_FOUND;
import static com.ureca.uble.domain.bookmark.exception.BookmarkErrorCode.DUPLICATED_BOOKMARK;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final EntityManager em;

    /**
     * 즐겨찾기 생성
     */
    @Transactional
    public CreateBookmarkRes createBookmark(Long userId, Long brandId) {
        // 즐겨찾기 중복 생성 방지
        if (bookmarkRepository.existsByBrand_IdAndUser_Id(brandId, userId)) {
            throw new GlobalException(DUPLICATED_BOOKMARK);
        }

        // 즐겨찾기 생성
        Brand brandRef = em.getReference(Brand.class, brandId);
        User userRef = em.getReference(User.class, userId);

        Bookmark savedBookmark = bookmarkRepository.save(Bookmark.of(userRef, brandRef));

        return new CreateBookmarkRes(savedBookmark.getId());
    }

    /**
     * 즐겨찾기 삭제
     */
    @Transactional
    public DeleteBookmarkRes deleteBookmark(Long userId, Long bookmarkId) {
        Bookmark bookmark = findBookmark(bookmarkId);
        checkAuthority(userId, bookmark);

        bookmarkRepository.delete(bookmark);

        return new DeleteBookmarkRes();
    }

    private void checkAuthority(Long userId, Bookmark bookmark) {
        if (!bookmark.getUser().getId().equals(userId)) {
            throw new GlobalException(UNAUTHORIZED_ACCESS);
        }
    }

    private Bookmark findBookmark(Long bookmarkId) {
        return bookmarkRepository.findById(bookmarkId)
            .orElseThrow(() -> new GlobalException(BOOKMARK_NOT_FOUND));
    }
}
