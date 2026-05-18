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
        if (code == null || code.isBlank()) {
            return null;
        }
        for (AssetType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }

    public static AssetType fromCodeOrThrow(String code) {
        AssetType type = fromCode(code);
        if (type == null) {
            throw new IllegalArgumentException("Unsupported asset type: " + code);
        }
        return type;
    }
}
