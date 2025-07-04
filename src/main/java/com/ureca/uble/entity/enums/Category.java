package com.ureca.uble.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category {
    ACTIVITY("액티비티"),
    BEAUTY_HEALTH("뷰티/건강"),
    SHOPPING("쇼핑"),
    LIFE_CONVENIENCE("생활/건강"),
    FOOD("푸드"),
    CULTURE_LEISURE("문화/여가"),
    EDUCATION("교육"),
    TRAVEL_TRANSPORTATION("여행/교통"),
    ;

    private final String name;
}
