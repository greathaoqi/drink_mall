package com.drinkmall.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class MemberCenterResponse {
    private Profile profile;
    private Summary summary;
    private Assets assets;
    private OrderSummary orderSummary;

    @Data
    @Builder
    public static class Profile {
        private Long userId;
        private String nickname;
        private String avatarUrl;
        private String phone;
        private String maskedPhone;
        private String memberLevelCode;
        private String memberLevelName;
        private String memberTitle;
        private Boolean ageVerified;
    }

    @Data
    @Builder
    public static class Summary {
        private BigDecimal withdrawableBalance;
        private BigDecimal teamPerformance;
        private Integer points;
    }

    @Data
    @Builder
    public static class Assets {
        private BigDecimal withdrawableBalance;
        private BigDecimal frozenBalance;
        private BigDecimal dfBalance;
        private Integer points;
    }

    @Data
    @Builder
    public static class OrderSummary {
        private Long pendingPayment;
        private Long pendingShipment;
        private Long pendingReceipt;
        private Long completed;
        private Long afterSale;
    }
}
