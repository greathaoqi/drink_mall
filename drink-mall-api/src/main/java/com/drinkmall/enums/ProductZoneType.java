package com.drinkmall.enums;

import lombok.Getter;

@Getter
public enum ProductZoneType {
    MAIN("main"),
    INVESTMENT("investment"),
    RETAIL("retail"),
    GIFT("gift");

    private final String code;

    ProductZoneType(String code) {
        this.code = code;
    }
}
