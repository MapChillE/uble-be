package com.ureca.uble.entity;

import static lombok.AccessLevel.*;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="user_category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCategory extends BaseEntity{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Builder(access = PRIVATE)
    private UserCategory(User user, Category category) {
        this.user = user;
        this.category = category;
    }

    public static UserCategory of(User user, Category category) {
        return UserCategory.builder()
            .user(user)
            .category(category)
            .build();
    }
}
