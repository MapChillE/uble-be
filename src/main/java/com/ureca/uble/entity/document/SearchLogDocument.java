package com.ureca.uble.entity.document;

import com.ureca.uble.entity.User;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@Document(indexName = "search-log")
@Setting(settingPath = "/elasticsearch/search-log-settings.json")
public class SearchLogDocument {
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

    @Field(type = FieldType.Keyword)
    private String searchType;

    @MultiField(
        mainField = @Field(type = FieldType.Text, analyzer = "nori_custom"),
        otherFields = { @InnerField(suffix = "raw", type = FieldType.Keyword) }
    )
    private String searchKeyword;

    @Field(type = FieldType.Boolean)
    private Boolean isResultExists;

    @Field(type = FieldType.Date, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZ")
    private LocalDateTime createdAt;

    public static SearchLogDocument of(User user, String searchType, String searchKeyword, Boolean isResultExists) {
        return SearchLogDocument.builder()
            .userId(user.getId())
            .userBirthDate(user.getBirthDate())
            .userRank(user.getRank().toString())
            .userGender(user.getGender().toString())
            .searchType(searchType)
            .searchKeyword(searchKeyword)
            .isResultExists(isResultExists)
            .createdAt(LocalDateTime.now())
            .build();
    }
}
