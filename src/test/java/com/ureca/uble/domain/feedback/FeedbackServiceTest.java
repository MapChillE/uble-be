package com.ureca.uble.domain.feedback.service;

import com.ureca.uble.domain.feedback.dto.request.CreateFeedbackReq;
import com.ureca.uble.domain.feedback.repository.FeedbackRepository;
import com.ureca.uble.domain.users.repository.UserRepository;
import com.ureca.uble.entity.Feedback;
import com.ureca.uble.entity.User;
import com.ureca.uble.global.exception.GlobalException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.ureca.uble.domain.users.exception.UserErrorCode.USER_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedbackServiceTest {

    @Mock private FeedbackRepository feedbackRepository;
    @Mock private UserRepository userRepository;
    @InjectMocks private FeedbackService feedbackService;

    @Test
    @DisplayName("유효한 사용자 ID와 피드백 정보로 피드백을 성공적으로 생성한다.")
    void createFeedback_success_check() {
        // given
        Long userId = 1L;
        CreateFeedbackReq req = mock(CreateFeedbackReq.class);
        when(req.getTitle()).thenReturn("앱 사용성이 좋습니다.");
        when(req.getContent()).thenReturn("지도 기능이 직관적이라 너무 편리해요!");
        when(req.getScore()).thenReturn(5);

        User mockUser = mock(User.class);
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(mockUser));

        Feedback mockFeedback = mock(Feedback.class);
        when(mockFeedback.getId()).thenReturn(1L);
        when(feedbackRepository.save(any(Feedback.class)))
                .thenReturn(mockFeedback);

        // when
        Long result = feedbackService.createFeedback(userId, req);

        // then
        assertNotNull(result);
        assertEquals(1L, result);

    }

    @Test
    @DisplayName("존재하지 않는 사용자 ID로 피드백 생성 시 USER_NOT_FOUND 예외를 던진다.")
    void createFeedback_userNotFound_exception() {
        // given
        Long userId = 999L;
        CreateFeedbackReq req = mock(CreateFeedbackReq.class);
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        // when & then
        GlobalException exception = assertThrows(GlobalException.class,
                () -> feedbackService.createFeedback(userId, req)
        );
        assertEquals(USER_NOT_FOUND, exception.getResultCode());
    }
}