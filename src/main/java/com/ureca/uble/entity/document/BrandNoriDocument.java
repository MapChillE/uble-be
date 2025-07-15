package com.ureca.uble.entity.document;

import com.ureca.uble.entity.Brand;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

@Getter
@Builder
@Document(indexName = "brand-nori")
@Setting(settingPath = "/elasticsearch/brand-nori-settings.json")
public class BrandNoriDocument {

    @Id
    private String id;

    @Field(type = FieldType.Long)
    private Long brandId;

    @MultiField(
        mainField = @Field(type = FieldType.Text, analyzer = "brand_nori_synonym_analyzer"),
        otherFields = { @InnerField(suffix = "raw", type = FieldType.Keyword) }
    )
    private String brandName;

    @MultiField(
        mainField = @Field(type = FieldType.Text, analyzer = "category_nori_synonym_analyzer"),
        otherFields = { @InnerField(suffix = "raw", type = FieldType.Keyword) }
    )
    private String category;

    @Field(type = FieldType.Keyword)
    private String rankType;

    @Field(type = FieldType.Boolean)
    private Boolean isVipCock;

    @MultiField(
        mainField = @Field(type = FieldType.Text, analyzer = "season_nori_synonym_analyzer"),
        otherFields = { @InnerField(suffix = "raw", type = FieldType.Keyword) }
    )
    private String season;

    @Field(type = FieldType.Text, index = false)
    private String description;

    @Field(type = FieldType.Keyword)
    private String minRank;

    @Field(type = FieldType.Keyword, index = false)
    private String imageUrl;

    public static BrandNoriDocument from(Brand brand) {
        return BrandNoriDocument.builder()
            .brandId(brand.getId())
            .brandName(brand.getName())
            .category(brand.getCategory().getName())
            .rankType(brand.getRankType().toString())
            .isVipCock(brand.isVIPcock())
            .season(brand.getSeason().toString())
            .description(brand.getDescription())
            .minRank(brand.getMinRank().toString())
            .imageUrl(brand.getImageUrl())
            .build();
    }
}
