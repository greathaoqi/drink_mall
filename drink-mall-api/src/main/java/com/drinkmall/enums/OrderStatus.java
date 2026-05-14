package com.drinkmall.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PENDING("pending"),
    PAID("paid"),
    SHIPPED("shipped"),
    COMPLETED("completed"),
    CANCELLED("cancelled"),
    AFTERSALE("aftersale");

    private final String code;

    OrderStatus(String code) {
        this.code = code;
    }
}
