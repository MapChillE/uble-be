package com.ureca.uble.entity;

import com.ureca.uble.domain.feedback.dto.request.CreateFeedbackReq;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@Entity
@Table(name="feedback")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Feedback extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private int score;

    @Builder(access = PRIVATE)
    private Feedback(User user, String title, String content, int score) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.score = score;
    }

    public static Feedback of(User user, CreateFeedbackReq req) {
        return Feedback.builder()
                .user(user)
                .title(req.getTitle())
                .content(req.getContent())
                .score(req.getScore())
                .build();
    }
}
