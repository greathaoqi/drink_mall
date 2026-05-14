package com.drinkmall.enums;

import lombok.Getter;

@Getter
public enum RealNameStatus {
    NOT_SUBMITTED("not_submitted"),
    PENDING("pending"),
    APPROVED("approved"),
    REJECTED("rejected");

    private final String code;

    RealNameStatus(String code) {
        this.code = code;
    }
}
