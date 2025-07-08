package com.ureca.uble.domain.bookmark.service;

import com.ureca.uble.domain.bookmark.dto.response.CreateBookmarkRes;
import com.ureca.uble.domain.bookmark.dto.response.DeleteBookmarkRes;
import com.ureca.uble.domain.bookmark.repository.BookmarkRepository;
import com.ureca.uble.entity.Bookmark;
import com.ureca.uble.entity.Brand;
import com.ureca.uble.entity.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookmarkServiceTest {

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
        when(em.getReference(Brand.class, brandId)).thenReturn(mockBrand);
        when(bookmarkRepository.save(any(Bookmark.class))).thenReturn(savedBookmark);

        // when
        CreateBookmarkRes res = bookmarkService.createBookmark(userId, brandId);

        // then
        assertNotNull(res);
        assertEquals(bookmarkId, res.getBookmarkId());

        verify(bookmarkRepository).existsByBrand_IdAndUser_Id(brandId, userId);
        verify(em).getReference(User.class, userId);
        verify(em).getReference(Brand.class, brandId);
        verify(bookmarkRepository).save(any(Bookmark.class));
    }

    /**
     * 즐겨찾기 삭제 Test
     */
    @Test
    void deleteBookmark_delete_check() {
        // given
        Long userId = 1L;
        Long bookmarkId = 10L;

        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(userId);

        Bookmark mockBookmark = mock(Bookmark.class);
        when(mockBookmark.getUser()).thenReturn(mockUser);

        when(bookmarkRepository.findById(bookmarkId)).thenReturn(Optional.of(mockBookmark));

        // when
        DeleteBookmarkRes res = bookmarkService.deleteBookmark(userId, bookmarkId);

        // then
        assertNotNull(res);
        verify(bookmarkRepository).findById(bookmarkId);
        verify(bookmarkRepository).delete(mockBookmark);
    }
}
