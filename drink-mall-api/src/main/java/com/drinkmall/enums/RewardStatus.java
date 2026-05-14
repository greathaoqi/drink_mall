package com.drinkmall.enums;

import lombok.Getter;

@Getter
public enum RewardStatus {
    FROZEN("frozen"),
    UNFROZEN("unfrozen"),
    ROLLED_BACK("rolled_back");

    private final String code;

    RewardStatus(String code) {
        this.code = code;
    }
}
