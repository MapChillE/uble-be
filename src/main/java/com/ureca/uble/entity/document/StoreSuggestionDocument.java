package com.ureca.uble.entity.document;

import com.ureca.uble.entity.Store;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

@Getter
@Builder
@Document(indexName = "store-suggestion")
@Setting(settingPath = "/elasticsearch/store-suggestion-settings.json")
public class StoreSuggestionDocument {
    @Id
    private String id;

    @Field(type = FieldType.Long)
    private Long storeId;

    @Field(type = FieldType.Search_As_You_Type, analyzer = "address_synonym_analyzer")
    private String storeName;

    @Field(type = FieldType.Search_As_You_Type, analyzer = "brand_synonym_analyzer")
    private String brandName;

    @Field(type = FieldType.Search_As_You_Type, analyzer = "category_synonym_analyzer")
    private String category;

    @Field(type = FieldType.Search_As_You_Type, analyzer = "season_synonym_analyzer")
    private String season;

    @Field(type = FieldType.Text, analyzer = "address_synonym_analyzer")
    private String address;

    @GeoPointField
    private GeoPoint location;

    public static StoreSuggestionDocument from(Store store) {
        return StoreSuggestionDocument.builder()
            .storeId(store.getId())
            .storeName(store.getName())
            .brandName(store.getBrand().getName())
            .category(store.getBrand().getCategory().getName())
            .season(store.getBrand().getSeason().toString())
            .address(store.getAddress())
            .location(new GeoPoint(store.getLocation().getY(), store.getLocation().getX()))
            .build();
    }
}
