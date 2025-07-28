package com.ureca.uble.domain.store.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ureca.uble.entity.enums.SuggestionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description="지도 내 자동완성 조회 응답 DTO")
public class GetGlobalSuggestionRes {

    @JsonInclude(JsonInclude.Include.ALWAYS)
    @Schema(description = "자동완성", example = "할리스")
    private String suggestion;

    @Schema(description = "카테고리", example = "카페")
    private String category;

    @Schema(description = "결과 id", example = "12")
    private Long id;

    @Schema(description = "위도", example = "37.123")
    private Double latitude;

    @Schema(description = "경도", example = "127.123")
    private Double longitude;

    @Schema(description = "주소", example = "서울시 강남구 123")
    private String address;

    @Schema(description = "자동완성 타입", example = "BRAND")
    private SuggestionType type;


    public static GetGlobalSuggestionRes of(String suggestion, String category, String address, SuggestionType type, Long id, Double latitude, Double longitude) {
        return GetGlobalSuggestionRes.builder()
            .suggestion(suggestion)
            .category(category)
            .address(address)
            .type(type)
            .id(id)
            .latitude(latitude)
            .longitude(longitude)
            .build();
    }
}
