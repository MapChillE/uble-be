package com.ureca.uble.domain.brand.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "오프라인 제휴처 응답 DTO")
public class OfflineBrandRes {
    @Schema(description = "브랜드 ID", example = "51")
    private Long id;

    @Schema(description = "브랜드 이름", example = "스타벅스")
    private String name;

    @Schema(description = "브랜드 이미지 URL", example = "https://.../starbucks.png")
    private String imageUrl;

    public static OfflineBrandRes of(Long id, String name, String imageUrl) {
        return OfflineBrandRes.builder()
                .id(id)
                .name(name)
                .imageUrl(imageUrl)
                .build();
    }
}
