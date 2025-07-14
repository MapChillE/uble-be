package com.ureca.uble.domain.feedback.service;

import com.ureca.uble.domain.feedback.dto.request.CreateFeedbackReq;
import com.ureca.uble.domain.feedback.dto.response.CreateFeedbackRes;
import com.ureca.uble.domain.feedback.repository.FeedbackRepository;
import com.ureca.uble.domain.users.repository.UserRepository;
import com.ureca.uble.entity.Feedback;
import com.ureca.uble.entity.User;
import com.ureca.uble.global.exception.GlobalException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.ureca.uble.domain.users.exception.UserErrorCode.USER_NOT_FOUND;


@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;

    /**
     * 피드백 생성 후 생성된 ID 반환
     */
    @Transactional
    public CreateFeedbackRes createFeedback(Long userId, CreateFeedbackReq req) {
        User user = findUser(userId);
        Feedback feedback = Feedback.of(user, req);
        Long feedbackId = feedbackRepository.save(feedback).getId();

        return new CreateFeedbackRes(feedbackId);
    }

    /**
     * userId로 User 조회, 없으면 USER_NOT_FOUND 예외 던짐
     */
    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(USER_NOT_FOUND));
    }
}
