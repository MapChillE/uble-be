package com.ureca.uble.entity.document;

import com.ureca.uble.entity.Store;
import com.ureca.uble.entity.User;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@Document(indexName = "store-click-log")
public class StoreClickLogDocument {

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
    private Long storeId;

    @Field(type = FieldType.Keyword)
    private String storeName;

    @MultiField(
        mainField = @Field(type = FieldType.Text, analyzer = "standard"),
        otherFields = { @InnerField(suffix = "raw", type = FieldType.Keyword) }
    )
    private String storeAddress;

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

    public static StoreClickLogDocument of(User user, Store store) {
        return StoreClickLogDocument.builder()
            .userId(user.getId())
            .userBirthDate(user.getBirthDate())
            .userRank(user.getRank().toString())
            .userGender(user.getGender().toString())
            .storeId(store.getId())
            .storeName(store.getName())
            .storeAddress(store.getAddress())
            .brandId(store.getBrand().getId())
            .brandName(store.getBrand().getName())
            .brandIsOnline(store.getBrand().getIsOnline())
            .brandBenefitType(store.getBrand().getRankList())
            .category(store.getBrand().getCategory().getName())
            .createdAt(LocalDateTime.now())
            .build();
    }
}
