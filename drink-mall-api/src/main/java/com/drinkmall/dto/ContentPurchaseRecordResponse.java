package com.drinkmall.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO for admin purchase record list view.
 * Per D-ANALYTICS-02: shows user, content title, price, payment method, time.
 */
@Data
@Builder
public class ContentPurchaseRecordResponse {
    private Long id;
    private Long userId;
    private String userNickname;
    private String userPhone;
    private String contentType;
    private Long contentId;
    private String contentTitle;
    private BigDecimal price;
    private String paymentMethod;
    private String status;
    private String orderNo;
    private String paymentNo;
    private LocalDateTime paymentTime;
    private LocalDateTime createdAt;
}
