package com.ureca.uble.domain.bookmark.service;

import com.ureca.uble.domain.bookmark.dto.response.CreateBookmarkRes;
import com.ureca.uble.domain.bookmark.dto.response.DeleteBookmarkRes;
import com.ureca.uble.domain.bookmark.dto.response.GetBookmarkRes;
import com.ureca.uble.domain.bookmark.repository.BookmarkRepository;
import com.ureca.uble.domain.brand.repository.BrandRepository;
import com.ureca.uble.domain.common.dto.response.CursorPageRes;
import com.ureca.uble.entity.Bookmark;
import com.ureca.uble.entity.Brand;
import com.ureca.uble.entity.Category;
import com.ureca.uble.entity.User;
import com.ureca.uble.entity.enums.Rank;
import com.ureca.uble.global.exception.GlobalException;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookmarkServiceTest {

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private BookmarkRepository bookmarkRepository;

    @Mock
    private EntityManager em;

    @InjectMocks
    private BookmarkService bookmarkService;

    /**
     * 즐겨찾기 생성 Test
     */
    @Test
    @DisplayName("즐겨찾기를 새롭게 생성한다.")
    void createBookmark_create_check() {
        // given
        Long userId = 1L;
        Long brandId = 2L;
        Long bookmarkId = 3L;

        User mockUser = mock(User.class);
        Brand mockBrand = mock(Brand.class);

        Bookmark savedBookmark = mock(Bookmark.class);
        when(savedBookmark.getId()).thenReturn(bookmarkId);

        when(bookmarkRepository.existsByBrand_IdAndUser_Id(brandId, userId)).thenReturn(false);
        when(em.getReference(User.class, userId)).thenReturn(mockUser);
        when(brandRepository.findById(brandId)).thenReturn(Optional.ofNullable(mockBrand));
        when(bookmarkRepository.save(any(Bookmark.class))).thenReturn(savedBookmark);

        // when
        CreateBookmarkRes res = bookmarkService.createBookmark(userId, brandId);

        // then
        assertNotNull(res);
        assertEquals(bookmarkId, res.getBookmarkId());

        verify(bookmarkRepository).existsByBrand_IdAndUser_Id(brandId, userId);
        verify(em).getReference(User.class, userId);
        verify(brandRepository).findById(brandId);
        verify(bookmarkRepository).save(any(Bookmark.class));
    }

    @Test
    @DisplayName("이미 존재하는 즐겨찾기를 추가할 경우 에러가 발생한다.")
    void createBookmark_duplicate_check() {
        // given
        Long userId = 1L;
        Long brandId = 2L;

        when(bookmarkRepository.existsByBrand_IdAndUser_Id(brandId, userId)).thenReturn(true);

        // when
        GlobalException exception = assertThrows(GlobalException.class, () -> {
            bookmarkService.createBookmark(userId, brandId);
        });

        // then
        assertEquals(6001, exception.getResultCode().getCode());
    }

    /**
     * 즐겨찾기 삭제 Test
     */
    @Test
    @DisplayName("존재하는 즐겨찾기를 삭제한다.")
    void deleteBookmark_delete_check() {
        // given
        Long userId = 1L;
        Long brandId = 10L;

        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(userId);

        Bookmark mockBookmark = mock(Bookmark.class);
        when(mockBookmark.getUser()).thenReturn(mockUser);

        when(bookmarkRepository.findByUser_IdAndBrand_Id(userId, brandId)).thenReturn(Optional.of(mockBookmark));

        // when
        DeleteBookmarkRes res = bookmarkService.deleteBookmark(userId, brandId);

        // then
        assertNotNull(res);
        verify(bookmarkRepository).findByUser_IdAndBrand_Id(userId, brandId);
        verify(bookmarkRepository).delete(mockBookmark);
    }

    /**
     * 즐겨찾기 전체 조회 Test
     */
    @Test
    @DisplayName("페이지네이션을 통해 즐겨찾기 목록을 조회한다.")
    void getBookmarks_get_first_check() {
        //given
        Long userId = 1L;
        Long lastBookmarkId = null;
        int size = 5;

        Brand mockBrand = mock(Brand.class);
        when(mockBrand.getId()).thenReturn(1L);
        when(mockBrand.getMinRank()).thenReturn(Rank.NORMAL);

        Category mockCategory = mock(Category.class);
        when(mockCategory.getName()).thenReturn("tmpCategory");

        Bookmark mockBookmark1 = mock(Bookmark.class);
        Bookmark mockBookmark2 = mock(Bookmark.class);

        when(mockBookmark1.getBrand()).thenReturn(mockBrand);
        when(mockBookmark2.getBrand()).thenReturn(mockBrand);
        when(mockBookmark1.getBrand().getCategory()).thenReturn(mockCategory);
        when(mockBookmark2.getBrand().getCategory()).thenReturn(mockCategory);

        List<Bookmark> content = List.of(mockBookmark1, mockBookmark2);
        when(bookmarkRepository.getBookmarksByPage(userId, size + 1, lastBookmarkId)).thenReturn(content);

        //when
        CursorPageRes<GetBookmarkRes> result = bookmarkService.getBookmarks(userId, lastBookmarkId, size);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.isHasNext()).isFalse();
        verify(bookmarkRepository).getBookmarksByPage(userId, size + 1, lastBookmarkId);
    }
}
