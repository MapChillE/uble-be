package com.ureca.uble.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Period {
    DAILY("일"),
    WEEKLY("주"),
    MONTHLY("월"),
    YEARLY("연"),
    NONE("없음");

    private final String name;

    public String formatProvisionCount(Integer count){
        if (count == null) {
           return this == NONE ? "제한없음" : name;
        }
        return this == NONE ? "제한없음" : String.format("%s %d회", name, count);
    }
}
