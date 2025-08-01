package com.ureca.uble.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

import static lombok.AccessLevel.PRIVATE;

@Entity
@Table(name="store")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Store extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @Column(nullable = false)
    private String name;

    @Column
    private String address;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(columnDefinition = "geography(Point,4326)")
    private Point location;

    @Column(name = "visit_count", nullable = false)
    private int visitCount;

    @Builder(access = PRIVATE)
    private Store(Brand brand, String name, String address, String phoneNumber, Point location, int visitCount) {
        this.brand = brand;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.location = location;
        this.visitCount = visitCount;
    }

    public static Store createTmpStore(Brand brand) {
        return Store.builder()
            .brand(brand)
            .name("tmp Store")
            .visitCount(0)
            .build();
    }
}
