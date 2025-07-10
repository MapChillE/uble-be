package com.ureca.uble.domain.bookmark.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ureca.uble.entity.Bookmark;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.ureca.uble.entity.QBookmark.bookmark;

@Repository
@RequiredArgsConstructor
public class CustomBookmarkRepositoryImpl implements CustomBookmarkRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Bookmark> getBookmarksByPage(Long userId, int size, Long lastBookmarkId) {
        return jpaQueryFactory
            .selectFrom(bookmark)
            .where(
                ltBookmarkId(lastBookmarkId),
                bookmark.user.id.eq(userId)
            )
            .orderBy(bookmark.id.desc())
            .limit(size)
            .fetch();
    }

    private BooleanExpression ltBookmarkId(Long lastBookmarkId) {
        return (lastBookmarkId == null) ? null : bookmark.id.lt(lastBookmarkId);
    }
}
