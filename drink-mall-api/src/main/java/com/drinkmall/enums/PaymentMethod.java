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
        if (code == null || code.isBlank()) {
            return null;
        }
        for (PaymentMethod method : values()) {
            if (method.code.equals(code)) {
                return method;
            }
        }
        return null;
    }

    public static PaymentMethod fromCodeOrDefault(String code) {
        PaymentMethod method = fromCode(code);
        return method == null ? ONLINE : method;
    }
}
