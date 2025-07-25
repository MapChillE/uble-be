package com.ureca.uble.entity.document;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

@Getter
@Builder
@Document(indexName = "location-coordination")
@Setting(settingPath = "/elasticsearch/location-coordination-settings.json")
public class LocationCoordinationDocument {
    @Id
    private String id;

    @MultiField(
        mainField = @Field(type = FieldType.Text, analyzer = "address_synonym_analyzer"),
        otherFields = { @InnerField(suffix = "raw", type = FieldType.Keyword) }
    )
    private String name;

    @GeoPointField
    private GeoPoint location;
}
