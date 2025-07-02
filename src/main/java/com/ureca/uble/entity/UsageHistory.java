package com.ureca.uble.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@Entity
@Table(name="usage_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UsageHistory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Builder(access = PRIVATE)
    private UsageHistory(User user, Store store) {
        this.user = user;
        this.store = store;
    }
}
