package com.drinkmall.enums;

import lombok.Getter;

@Getter
public enum UserLevel {
    NORMAL("normal"),
    PROMOTER("promoter"),
    COUNTY("county"),
    CITY("city"),
    PROVINCE("province");

    private final String code;

    UserLevel(String code) {
        this.code = code;
    }
}
