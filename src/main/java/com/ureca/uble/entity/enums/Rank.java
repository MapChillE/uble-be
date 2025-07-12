package com.ureca.uble.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Rank {
    NORMAL("일반"),
    PREMIUM("우수"),
    VIP("VIP"),
    VVIP("VVIP"),
    NONE("VIP콕");

    private final String name;
}
