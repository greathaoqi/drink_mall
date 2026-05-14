package com.drinkmall.enums;

import lombok.Getter;

@Getter
public enum AssetType {
    BALANCE("balance", true),
    FROZEN_BALANCE("frozen_balance", false),
    DF("df", false),
    WINE_BEAN("wine_bean", false),
    POINTS("points", false),
    OPTION("option", false);

    private final String code;
    private final boolean withdrawable;

    AssetType(String code, boolean withdrawable) {
        this.code = code;
        this.withdrawable = withdrawable;
    }

    public static AssetType fromCode(String code) {
        for (AssetType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unsupported asset type: " + code);
    }
}
