package com.ureca.uble.entity.document;

import com.ureca.uble.entity.Store;
import com.ureca.uble.entity.User;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Builder
@Document(indexName = "usage-history-log")
public class UsageHistoryDocument {

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

    @Field(type = FieldType.Keyword)
    private String storeLocal;

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

    @Field(type = FieldType.Date, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSZZ")
    private ZonedDateTime createdAt;

    public static UsageHistoryDocument of(User user, Store store) {
        return UsageHistoryDocument.builder()
            .userId(user.getId())
            .userBirthDate(user.getBirthDate())
            .userRank(user.getRank().toString())
            .userGender(user.getGender().toString())
            .storeId(store.getId())
            .storeName(store.getName())
            .storeLocal(getLocal(store.getAddress()))
            .brandId(store.getBrand().getId())
            .brandName(store.getBrand().getName())
            .brandIsOnline(store.getBrand().getIsOnline())
            .brandBenefitType(store.getBrand().getRankList())
            .category(store.getBrand().getCategory().getName())
            .createdAt(ZonedDateTime.now())
            .build();
    }

    private static String getLocal(String address) {
        if(!address.startsWith("서울")) return null;
        return address.split(" ")[1];
    }
}
