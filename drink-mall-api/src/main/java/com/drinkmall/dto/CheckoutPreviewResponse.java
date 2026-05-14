package com.drinkmall.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class CheckoutPreviewResponse {
    private String zoneType;
    private String paymentMethod;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    private Integer pointsAmount;
    private Integer giftPoints;
    private List<OrderItemResponse> items;
}
