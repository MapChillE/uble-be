package com.ureca.uble.entity;

import com.ureca.uble.entity.enums.Category;
import com.ureca.uble.entity.enums.RankType;
import com.ureca.uble.entity.enums.Season;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@Entity
@Table(name="brand")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Brand extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(name = "csr_number", nullable = false)
    private String csrNumber;

    @Column(nullable = false)
    private String description;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Season season;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(name = "is_online", nullable = false)
    private Boolean isOnline;

    @Column(name = "is_local", nullable = false)
    private Boolean isLocal;

    @Column(name = "reservation_url")
    private String reservationUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "rank_type", nullable = false)
    private RankType rankType;

    @Builder(access = PRIVATE)
    private Brand(String name, String csrNumber, String description, String imageUrl,
                  Season season, Category category, Boolean isOnline, Boolean isLocal,
                  String reservationUrl, RankType rankType) {
        this.name = name;
        this.csrNumber = csrNumber;
        this.description = description;
        this.imageUrl = imageUrl;
        this.season = season;
        this.category = category;
        this.isOnline = isOnline;
        this.isLocal = isLocal;
        this.reservationUrl = reservationUrl;
        this.rankType = rankType;
    }
}
