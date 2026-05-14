package com.drinkmall.enums;

import lombok.Getter;

@Getter
public enum RewardType {
    MAIN_FIRST_ORDER("main_first_order"),
    MAIN_REPURCHASE("main_repurchase"),
    RETAIL_DIRECT("retail_direct"),
    INVESTMENT("investment"),
    SUPPORT_MERCHANT("support_merchant"),
    ADVERTISING("advertising");

    private final String code;

    RewardType(String code) {
        this.code = code;
    }
}
