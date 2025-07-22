package com.ureca.uble.domain.users.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "가장 많이 사용한 일시 정보")
public class BenefitUsagePatternRes {

    @Schema(description = "가장 많이 사용된 날(일)", example = "21")
    private String mostUsedDay;

    @Schema(description = "가장 많이 사용된 요일", example = "월")
    private String mostUsedWeekday;

    @Schema(description = "가장 많이 사용된 시간", example = "13")
    private String mostUsedTime;

    public static BenefitUsagePatternRes of(String mostUsedDay, String mostUsedWeekday, String mostUsedTime) {
        return BenefitUsagePatternRes.builder()
            .mostUsedDay(mostUsedDay)
            .mostUsedWeekday(mostUsedWeekday)
            .mostUsedTime(mostUsedTime)
            .build();
    }
}
