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
@Document(indexName = "category-ngram")
@Setting(settingPath = "/elasticsearch/category-ngram-settings.json")
public class CategoryNgramDocument {

    @Id
    private String id;

    @Field(type = FieldType.Long)
    private Long categoryId;

    @Field(type = FieldType.Text, analyzer = "category_ngram_synonym_analyzer")
    private String categoryName;

    public static CategoryNgramDocument from(Category category) {
        return CategoryNgramDocument.builder()
            .categoryId(category.getId())
            .categoryName(category.getName())
            .build();
    }
}
