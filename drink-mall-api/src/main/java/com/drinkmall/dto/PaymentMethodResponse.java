package com.drinkmall.dto;

import com.drinkmall.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethodResponse {
    private String code;
    private String text;

    public static List<PaymentMethodResponse> fromCodes(String codes) {
        if (codes == null || codes.isBlank()) {
            return all();
        }
        return Arrays.stream(codes.split(","))
                .map(String::trim)
                .filter(code -> !code.isBlank())
                .map(PaymentMethodResponse::fromCode)
                .toList();
    }

    public static List<PaymentMethodResponse> all() {
        return Arrays.stream(PaymentMethod.values()).map(method -> fromCode(method.getCode())).toList();
    }

    public static PaymentMethodResponse points() {
        return fromCode(PaymentMethod.POINTS.getCode());
    }

    public static PaymentMethodResponse fromCode(String code) {
        return PaymentMethodResponse.builder()
                .code(code)
                .text(textOf(code))
                .build();
    }

    public static String textOf(String code) {
        if (PaymentMethod.ONLINE.getCode().equals(code)) {
            return "微信支付";
        }
        if (PaymentMethod.BALANCE.getCode().equals(code)) {
            return "余额支付";
        }
        if ("df".equals(code)) {
            return "DF支付";
        }
        if (PaymentMethod.WINE_BEAN.getCode().equals(code)) {
            return "酒豆支付";
        }
        if (PaymentMethod.POINTS.getCode().equals(code)) {
            return "积分兑换";
        }
        if (PaymentMethod.OFFLINE_CORPORATE.getCode().equals(code)) {
            return "线下对公转账";
        }
        return code;
    }
}
