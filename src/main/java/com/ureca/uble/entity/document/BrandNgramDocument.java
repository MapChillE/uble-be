package com.ureca.uble.entity.document;

import com.ureca.uble.entity.Brand;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

@Getter
@Builder
@Document(indexName = "brand-ngram")
@Setting(settingPath = "/elasticsearch/brand-ngram-settings.json")
public class BrandNgramDocument {

    @Id
    private String id;

    @Field(type = FieldType.Long)
    private Long brandId;

    @Field(type = FieldType.Text, analyzer = "brand_ngram_synonym_analyzer")
    private String brandName;

    @Field(type = FieldType.Text, analyzer = "category_ngram_synonym_analyzer")
    private String category;

    @Field(type = FieldType.Text, analyzer = "season_ngram_synonym_analyzer")
    private String season;

    public static BrandNgramDocument from(Brand brand) {
        return BrandNgramDocument.builder()
            .brandId(brand.getId())
            .brandName(brand.getName())
            .category(brand.getCategory().getName())
            .season(brand.getSeason().toString())
            .build();
    }
}
