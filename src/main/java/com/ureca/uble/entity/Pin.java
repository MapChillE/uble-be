package com.ureca.uble.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

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

    @Column(columnDefinition = "geography(Point,4326)")
    private Point location;

    @Column(nullable = false)
    private String address;

    @Builder(access = PRIVATE)
    private Pin(User user, String name, Point location, String address) {
        this.user = user;
        this.name = name;
        this.location = location;
        this.address = address;
    }

    public static Pin of(User user, String name, Point location, String address) {
        return Pin.builder()
                .user(user)
                .name(name)
                .location(location)
                .address(address)
                .build();
    }
}
