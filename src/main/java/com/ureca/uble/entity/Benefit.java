package com.ureca.uble.entity;

import com.ureca.uble.entity.enums.Period;
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Period period;

    @Column
    private Integer number;

    @Column(columnDefinition="TEXT")
    private String manual;

    @Builder(access = PRIVATE)
    private Benefit(Brand brand, Rank rank, String content, Period period,
                    Integer number, String manual) {
        this.brand = brand;
        this.rank = rank;
        this.content = content;
        this.period = period;
        this.number = number;
        this.manual = manual;
    }
}
