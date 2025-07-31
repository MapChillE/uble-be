package com.ureca.uble.entity.document;

import com.ureca.uble.entity.Brand;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

@Getter
@Builder
@Document(indexName = "brand-suggestion")
@Setting(settingPath = "/elasticsearch/brand-suggestion-settings.json")
public class BrandSuggestionDocument {

    @Id
    private String id;

    @Field(type = FieldType.Long)
    private Long brandId;

    @Field(type = FieldType.Boolean)
    private Boolean isOnline;

    @Field(type = FieldType.Boolean)
    private Boolean isLocal;

    @Field(type = FieldType.Search_As_You_Type, analyzer = "brand_synonym_analyzer")
    private String brandName;

    @Field(type = FieldType.Search_As_You_Type, analyzer = "category_synonym_analyzer")
    private String category;

    @Field(type = FieldType.Search_As_You_Type, analyzer = "season_synonym_analyzer")
    private String season;

    public static BrandSuggestionDocument from(Brand brand) {
        return BrandSuggestionDocument.builder()
            .brandId(brand.getId())
            .isOnline(brand.getIsOnline())
            .isLocal(brand.getIsLocal())
            .brandName(brand.getName())
            .category(brand.getCategory().getName())
            .season(brand.getSeason().toString())
            .build();
    }
}
