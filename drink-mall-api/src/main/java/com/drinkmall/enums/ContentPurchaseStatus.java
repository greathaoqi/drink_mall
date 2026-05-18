package com.drinkmall.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ContentPurchaseStatus {
    PENDING("pending", "待支付"),
    PAID("paid", "已支付"),
    CANCELLED("cancelled", "已取消"),
    EXPIRED("expired", "已过期");

    private final String code;
    private final String description;

    public static ContentPurchaseStatus fromCode(String code) {
        for (ContentPurchaseStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}