package com.ureca.uble.entity;

import com.ureca.uble.entity.enums.Gender;
import com.ureca.uble.entity.enums.Rank;
import com.ureca.uble.entity.enums.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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

    @Column(name = "is_vip_available")
    private Boolean isVipAvailable;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Builder(access = PRIVATE)
    private User(String nickname, Rank rank, Role role, String providerId, Boolean isVipAvailable,
                 Boolean isDeleted, LocalDate birthDate, Gender gender) {
        this.nickname = nickname;
        this.rank = rank;
        this.role = role;
        this.providerId = providerId;
        this.isVipAvailable = isVipAvailable;
        this.isDeleted = isDeleted;
        this.birthDate = birthDate;
        this.gender = gender;
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
