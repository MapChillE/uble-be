package com.ureca.uble.entity.document;

import com.ureca.uble.entity.Category;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

@Getter
@Builder
@Document(indexName = "category-suggestion")
@Setting(settingPath = "/elasticsearch/category-suggestion-settings.json")
public class CategorySuggestionDocument {

    @Id
    private String id;

    @Field(type = FieldType.Long)
    private Long categoryId;

    @Field(type = FieldType.Search_As_You_Type, analyzer = "category_synonym_analyzer")
    private String categoryName;

    public static CategorySuggestionDocument from(Category category) {
        return CategorySuggestionDocument.builder()
            .categoryId(category.getId())
            .categoryName(category.getName())
            .build();
    }
}
