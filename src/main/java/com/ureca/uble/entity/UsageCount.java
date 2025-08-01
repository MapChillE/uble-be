package com.ureca.uble.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@Entity
@Table(name = "usage_count", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "benefit_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UsageCount extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "benefit_id", nullable = false)
    private Benefit benefit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Boolean isAvailable;

    @Column(nullable = false)
    private Integer count;

    @Builder(access = PRIVATE)
    private UsageCount(Benefit benefit, User user, Boolean isAvailable, Integer count) {
        this.benefit = benefit;
        this.user = user;
        this.isAvailable = isAvailable;
        this.count = count;
    }

    public static UsageCount of(User user, Benefit benefit, boolean isAvailable, int count) {
        return UsageCount.builder()
            .user(user)
            .benefit(benefit)
            .isAvailable(isAvailable)
            .count(count)
            .build();
    }

    public void update(int count, boolean isAvailable) {
        this.count = count;
        this.isAvailable = isAvailable;
    }
}
