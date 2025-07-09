package com.ureca.uble.domain.store.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Schema(description = "매장 리스트 반환 DTO")
public class GetStoreListRes {
    @Schema(description = "매장 리스트", example = "매장 정보 리스트")
    private List<GetStoreRes> storeList;
}
