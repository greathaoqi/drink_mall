package com.drinkmall.enums;

import lombok.Getter;

@Getter
public enum AftersaleStatus {
    PENDING("pending"),
    APPROVED("approved"),
    REJECTED("rejected"),
    CLOSED("closed"),
    COMPLETED("completed"),
    OFFLINE_PROCESSED("offline_processed");

    private final String code;

    AftersaleStatus(String code) {
        this.code = code;
    }
}
