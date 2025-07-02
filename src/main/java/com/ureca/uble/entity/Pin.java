package com.ureca.uble.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@Entity
@Table(name="pin")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Pin extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Builder(access = PRIVATE)
    private Pin(User user, String name, Double latitude, Double longitude) {
        this.user = user;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
