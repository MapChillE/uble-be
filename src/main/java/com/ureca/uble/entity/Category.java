package com.ureca.uble.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@Entity
@Table(name="category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Builder(access = PRIVATE)
    private Category(String name) {
        this.name = name;
    }

    public static Category createTmpCategory() {
        return Category.builder()
            .name("Tmp Category")
            .build();
    }
}
