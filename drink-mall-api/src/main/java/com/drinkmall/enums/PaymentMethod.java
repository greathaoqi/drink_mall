package com.drinkmall.enums;

import lombok.Getter;

@Getter
public enum PaymentMethod {
    ONLINE("online"),
    BALANCE("balance"),
    WINE_BEAN("wine_bean"),
    POINTS("points"),
    OFFLINE_CORPORATE("offline_corporate");

    private final String code;

    PaymentMethod(String code) {
        this.code = code;
    }

    public static PaymentMethod fromCode(String code) {
        for (PaymentMethod method : values()) {
            if (method.code.equals(code)) {
                return method;
            }
        }
        throw new IllegalArgumentException("Unsupported payment method: " + code);
    }
}
