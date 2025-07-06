package com.ureca.uble.entity;

import com.ureca.uble.entity.enums.Rank;
import com.ureca.uble.entity.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import static lombok.AccessLevel.PRIVATE;

@Entity
@Table(name="users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Column(nullable = false)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rank rank;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(name = "provider_id", nullable = false)
    private String providerId;

    @Builder(access = PRIVATE)
    private User(String nickname, Rank rank, Role role, String providerId) {
        this.nickname = nickname;
        this.rank = rank;
        this.role = role;
        this.providerId = providerId;
    }

    public static User createTmpUser(String kakaoId, String nickname){
        return User.builder()
            .providerId(kakaoId)
            .nickname(nickname)
            .rank(Rank.NORMAL)
            .role(Role.TMP_USER)
            .build();
    }
}
