package com.ureca.uble.entity.document;

import com.ureca.uble.entity.Store;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

@Getter
@Builder
@Document(indexName = "store-ngram")
@Setting(settingPath = "/elasticsearch/store-ngram-settings.json")
public class StoreNgramDocument {
    @Id
    private String id;

    @Field(type = FieldType.Long)
    private Long storeId;

    @Field(type = FieldType.Text, analyzer = "store_ngram_analyzer")
    private String storeName;

    @Field(type = FieldType.Text, analyzer = "brand_ngram_synonym_analyzer")
    private String brandName;

    @Field(type = FieldType.Text, analyzer = "category_ngram_synonym_analyzer")
    private String category;

    @Field(type = FieldType.Text, analyzer = "season_ngram_synonym_analyzer")
    private String season;

    @GeoPointField
    private GeoPoint location;

    public static StoreNgramDocument from(Store store) {
        return StoreNgramDocument.builder()
            .storeId(store.getId())
            .storeName(store.getName())
            .brandName(store.getBrand().getName())
            .category(store.getBrand().getCategory().getName())
            .season(store.getBrand().getSeason().toString())
            .location(new GeoPoint(store.getLocation().getY(), store.getLocation().getX()))
            .build();
    }
}
