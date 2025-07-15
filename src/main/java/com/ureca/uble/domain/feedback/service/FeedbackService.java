package com.ureca.uble.domain.feedback.service;

import com.ureca.uble.domain.feedback.dto.request.CreateFeedbackReq;
import com.ureca.uble.domain.feedback.dto.response.AdminFeedbackRes;
import com.ureca.uble.domain.feedback.dto.response.CreateFeedbackRes;
import com.ureca.uble.domain.feedback.dto.response.FeedbackInfo;
import com.ureca.uble.domain.feedback.repository.FeedbackRepository;
import com.ureca.uble.domain.users.repository.UserRepository;
import com.ureca.uble.entity.Feedback;
import com.ureca.uble.entity.User;
import com.ureca.uble.global.exception.GlobalException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.ureca.uble.domain.feedback.exception.FeedbackErrorCode.FEEDBACK_SAVE_FAILED;
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
        try {
            Long feedbackId = feedbackRepository.save(feedback).getId();
            return new CreateFeedbackRes(feedbackId);
        } catch (Exception e) {
            throw new GlobalException(FEEDBACK_SAVE_FAILED);
        }
    }

    /**
     * userId로 User 조회, 없으면 USER_NOT_FOUND 예외 던짐
     */
    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(USER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public AdminFeedbackRes getFeedbacks(Pageable pageable) {
        Page<Feedback> page = feedbackRepository.listFeedbacks(pageable);
        return from(page);
    }

    public static AdminFeedbackRes from(Page<Feedback> page) {
        List<FeedbackInfo> items = page.getContent().stream()
                .map(f -> FeedbackInfo.builder()
                        .title(f.getTitle())
                        .content(f.getContent())
                        .score(f.getScore())
                        .createdAt(f.getCreatedAt())
                        .nickname(f.getUser() != null ? f.getUser().getNickname() : "Unknown")
                        .build()
                ).toList();

        return AdminFeedbackRes.builder()
                .content(items)
                .totalCount(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }
}
