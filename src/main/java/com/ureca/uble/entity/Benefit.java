package com.ureca.uble.entity;

import com.ureca.uble.entity.enums.Rank;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@Entity
@Table(name="benefit")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Benefit extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rank rank;

    @Column(nullable = false)
    private String content;

    @Builder(access = PRIVATE)
    private Benefit(Brand brand, Rank rank, String content) {
        this.brand = brand;
        this.rank = rank;
        this.content = content;
    }
}
