package com.ureca.uble.entity;

import com.ureca.uble.entity.enums.Provider;
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

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rank rank;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider;

    @Column(name = "provider_id", nullable = false)
    private String providerId;

    @Builder(access = PRIVATE)
    private User(String nickname, String email, String password, Rank rank, Role role, Provider provider, String providerId) {
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.rank = rank;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
    }
}
