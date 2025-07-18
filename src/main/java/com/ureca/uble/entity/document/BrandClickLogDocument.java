package com.ureca.uble.entity.document;

import com.ureca.uble.entity.Brand;
import com.ureca.uble.entity.User;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@Document(indexName = "brand-click-log")
public class BrandClickLogDocument {

    @Id
    private String id;

    @Field(type = FieldType.Long)
    private Long userId;

    @Field(type = FieldType.Date, pattern = "yyyy-MM-dd")
    private LocalDate userBirthDate;

    @Field(type = FieldType.Keyword)
    private String userRank;

    @Field(type = FieldType.Keyword)
    private String userGender;

    @Field(type = FieldType.Long)
    private Long brandId;

    @Field(type = FieldType.Keyword)
    private String brandName;

    @Field(type = FieldType.Boolean)
    private Boolean brandIsOnline;

    @Field(type = FieldType.Keyword)
    private List<String> brandBenefitType;

    @Field(type = FieldType.Keyword)
    private String category;

    @Field(type = FieldType.Date, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZ")
    private LocalDateTime createdAt;

    public static BrandClickLogDocument of(User user, Brand brand) {
        return BrandClickLogDocument.builder()
            .userId(user.getId())
            .userBirthDate(user.getBirthDate())
            .userRank(user.getRank().toString())
            .userGender(user.getGender().toString())
            .brandId(brand.getId())
            .brandName(brand.getName())
            .brandIsOnline(brand.getIsOnline())
            .brandBenefitType(brand.getRankList())
            .category(brand.getCategory().getName())
            .createdAt(LocalDateTime.now())
            .build();
    }
}
