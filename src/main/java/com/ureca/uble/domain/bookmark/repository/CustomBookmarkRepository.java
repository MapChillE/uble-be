package com.ureca.uble.domain.bookmark.repository;

import com.ureca.uble.entity.Bookmark;

import java.util.List;

public interface CustomBookmarkRepository {
    List<Bookmark> getBookmarksByPage(Long userId, int size, Long lastBookmarkId);
}
