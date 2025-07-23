package com.ureca.uble.domain.bookmark.service;

import com.ureca.uble.domain.bookmark.dto.response.CreateBookmarkRes;
import com.ureca.uble.domain.bookmark.dto.response.DeleteBookmarkRes;
import com.ureca.uble.domain.bookmark.dto.response.GetBookmarkRes;
import com.ureca.uble.domain.bookmark.repository.BookmarkRepository;
import com.ureca.uble.domain.brand.repository.BrandRepository;
import com.ureca.uble.domain.common.dto.response.CursorPageRes;
import com.ureca.uble.entity.Bookmark;
import com.ureca.uble.entity.Brand;
import com.ureca.uble.entity.User;
import com.ureca.uble.global.exception.GlobalException;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.ureca.uble.domain.auth.exception.AuthErrorCode.UNAUTHORIZED_ACCESS;
import static com.ureca.uble.domain.bookmark.exception.BookmarkErrorCode.BOOKMARK_NOT_FOUND;
import static com.ureca.uble.domain.bookmark.exception.BookmarkErrorCode.DUPLICATED_BOOKMARK;
import static com.ureca.uble.domain.brand.exception.BrandErrorCode.BRAND_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final EntityManager em;
    private final BrandRepository brandRepository;

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
        Brand brand = findBrand(brandId);
        User userRef = em.getReference(User.class, userId);

        Bookmark savedBookmark = bookmarkRepository.save(Bookmark.of(userRef, brand));

        return new CreateBookmarkRes(savedBookmark.getId());
    }

    /**
     * 즐겨찾기 삭제
     */
    @Transactional
    public DeleteBookmarkRes deleteBookmark(Long userId, Long brandId) {
        Bookmark bookmark = findBookmarkByUserAndBrand(userId, brandId);
        checkAuthority(userId, bookmark);

        bookmarkRepository.delete(bookmark);

        return new DeleteBookmarkRes();
    }

    /**
     * 즐겨찾기 조회
     */
    @Transactional(readOnly = true)
    public CursorPageRes<GetBookmarkRes> getBookmarks(Long userId, Long lastBookmarkId, int size) {
        List<GetBookmarkRes> bookmarkList = new ArrayList<>(bookmarkRepository.getBookmarksByPage(userId, size + 1, lastBookmarkId)
            .stream().map(GetBookmarkRes::from).toList());

        // 다음 페이지 여부 확인
        boolean hasNext = (bookmarkList.size() > size);
        if (hasNext) bookmarkList.remove(bookmarkList.size() - 1);

        long lastCursorId = bookmarkList.get(bookmarkList.size() - 1).getBookmarkId();

        return CursorPageRes.of(bookmarkList, hasNext, lastCursorId);
    }

    private void checkAuthority(Long userId, Bookmark bookmark) {
        if (!bookmark.getUser().getId().equals(userId)) {
            throw new GlobalException(UNAUTHORIZED_ACCESS);
        }
    }

    private Bookmark findBookmarkByUserAndBrand(Long userId, Long brandId) {
        return bookmarkRepository.findByUserIdAndBrandId(userId, brandId).orElseThrow(() -> new GlobalException(BOOKMARK_NOT_FOUND));
    }

    private Brand findBrand(Long brandId) {
        return brandRepository.findById(brandId).orElseThrow(() -> new GlobalException(BRAND_NOT_FOUND));
    }
}
