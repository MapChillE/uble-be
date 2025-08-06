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

    @Column(name = "is_local_available")
    private Boolean isLocalAvailable;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column
    private String barcode;

    @Builder(access = PRIVATE)
    private User(String nickname, Rank rank, Role role, String providerId, Boolean isVipAvailable,
                 Boolean isLocalAvailable, Boolean isDeleted, LocalDate birthDate, Gender gender, String barcode) {
        this.nickname = nickname;
        this.rank = rank;
        this.role = role;
        this.providerId = providerId;
        this.isVipAvailable = isVipAvailable;
        this.isLocalAvailable = isLocalAvailable;
        this.isDeleted = isDeleted;
        this.birthDate = birthDate;
        this.gender = gender;
        this.barcode = barcode;
    }

    public static User createTmpUser(String kakaoId, String nickname){
        return User.builder()
            .providerId(kakaoId)
            .nickname(nickname)
            .rank(Rank.NORMAL)
            .role(Role.TMP_USER)
            .isDeleted(false)
            .isVipAvailable(true)
            .isLocalAvailable(true)
            .birthDate(null)
            .gender(null)
            .barcode(null)
            .build();
    }

    public static User createAdminUser(String kakaoId, String nickname){
        return User.builder()
            .providerId(kakaoId)
            .nickname(nickname)
            .rank(Rank.NORMAL)
            .role(Role.ADMIN)
            .isDeleted(false)
            .isVipAvailable(true)
            .isLocalAvailable(true)
            .birthDate(null)
            .gender(null)
            .barcode(null)
            .build();
    }

    public static User createTmpUser(Rank rank, Role role) {
        return User.builder()
            .nickname("tmp User")
            .rank(rank)
            .role(role)
            .gender(Gender.FEMALE)
            .providerId("abc123")
            .isVipAvailable(true)
            .isLocalAvailable(true)
            .isDeleted(false)
            .build();
    }

    public void updateVipAvailability(boolean isVipAvailable) {
        this.isVipAvailable = isVipAvailable;
    }

    public void updateLocalAvailability(boolean isLocalAvailable) {
        this.isLocalAvailable = isLocalAvailable;
    }

    public void updateUserInfo(Rank rank, Gender gender, LocalDate birthDate, String barcode) {
        this.rank = rank;
        this.gender = gender;
        this.birthDate = birthDate;
        this.barcode = barcode;

        if(this.role == Role.TMP_USER){
            this.role = Role.USER;
        }
    }

    public void updateIsDeletedAndRole() {
        this.isDeleted = true;
        this.role = Role.TMP_USER;
    }

    public void reactivate() { this.isDeleted = false; }
}
