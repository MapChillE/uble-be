package com.ureca.uble.domain.feedback;

import com.ureca.uble.domain.feedback.dto.response.AdminFeedbackRes;

import com.ureca.uble.domain.feedback.dto.response.FeedbackInfo;
import com.ureca.uble.domain.feedback.repository.FeedbackRepository;
import com.ureca.uble.domain.feedback.service.FeedbackService;
import com.ureca.uble.entity.Feedback;
import com.ureca.uble.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.domain.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminFeedbackServiceTest {

    @Mock
    private FeedbackRepository feedbackRepository;

    @InjectMocks
    private FeedbackService service;

    @Test
    @DisplayName("getFeedbacks: Pageable에 맞춰 페이징된 DTO를 반환한다")
    void getFeedbacks_success() {
        // given
        int page = 1, size = 2;
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        User mockUser = mock(User.class);
        when(mockUser.getNickname()).thenReturn("tester");

        Feedback f1 = mock(Feedback.class);
        when(f1.getTitle()).thenReturn("테스트1");
        when(f1.getContent()).thenReturn("테스트1");
        when(f1.getScore()).thenReturn(3);
        when(f1.getCreatedAt()).thenReturn(LocalDateTime.of(2025,7,14,10,0));
        when(f1.getUser()).thenReturn(mockUser);

        Feedback f2 = mock(Feedback.class);
        when(f2.getTitle()).thenReturn("테스트2");
        when(f2.getContent()).thenReturn("테스트2");
        when(f2.getScore()).thenReturn(5);
        when(f2.getCreatedAt()).thenReturn(LocalDateTime.of(2025,7,14,9,0));
        when(f2.getUser()).thenReturn(mockUser);

        long totalElements = 5L;
        Page<Feedback> pageResult = new PageImpl<>(
                List.of(f1, f2),
                pageable,
                totalElements
        );

        when(feedbackRepository.listFeedbacks(pageable)).thenReturn(pageResult);

        // when
        AdminFeedbackRes res = service.getFeedbacks(pageable);

        // then
        assertEquals(totalElements, res.getTotalCount());
        assertEquals((int)Math.ceil((double)totalElements / size), res.getTotalPages());

        List<FeedbackInfo> items = res.getContent();
        assertEquals(2, items.size());

        FeedbackInfo item1 = items.get(0);
        assertEquals("테스트1", item1.getTitle());
        assertEquals("테스트1", item1.getContent());
        assertEquals(3, item1.getScore());
        assertEquals(LocalDateTime.of(2025,7,14,10,0), item1.getCreatedAt());
        assertEquals("tester", item1.getNickname());

        FeedbackInfo item2 = items.get(1);
        assertEquals("테스트2", item2.getTitle());
        assertEquals("테스트2", item2.getContent());
        assertEquals(5, item2.getScore());
        assertEquals(LocalDateTime.of(2025,7,14,9,0), item2.getCreatedAt());
        assertEquals("tester", item2.getNickname());

        // verify repository call
        verify(feedbackRepository, times(1)).listFeedbacks(pageable);
    }

    @Test
    @DisplayName("getFeedbacks: 데이터가 없으면 빈 리스트와 0건, 0페이지를 반환한다")
    void getFeedbacks_empty() {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Page<Feedback> emptyPage = new PageImpl<>(List.of(), pageable, 0L);
        when(feedbackRepository.listFeedbacks(pageable)).thenReturn(emptyPage);

        // when
        AdminFeedbackRes res = service.getFeedbacks(pageable);

        // then
        assertTrue(res.getContent().isEmpty());
        assertEquals(0L, res.getTotalCount());
        assertEquals(0, res.getTotalPages());
        verify(feedbackRepository).listFeedbacks(pageable);
    }
}