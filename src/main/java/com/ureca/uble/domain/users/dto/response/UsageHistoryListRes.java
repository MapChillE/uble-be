package com.ureca.uble.domain.users.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description="매장 이용내역 응답")
public class UsageHistoryListRes {

    @Schema(description = "이용내역 수")
    private Long totalCount;

    @Schema(description = "이용내역 List")
    private List<UsageHistoryRes> historyList;

    public static UsageHistoryListRes of(Long totalCount, List<UsageHistoryRes> historyList) {
        return UsageHistoryListRes.builder()
            .totalCount(totalCount)
            .historyList(historyList)
            .build();
    }
}
