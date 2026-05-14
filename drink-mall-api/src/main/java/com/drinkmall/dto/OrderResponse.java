package com.drinkmall.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private Long orderId;
    private String orderNo;
    private String status;
    private String zoneType;
    private String paymentMethod;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    private Integer pointsAmount;
    private AddressResponse address;
    private List<OrderItemResponse> items;
    private LocalDateTime createdAt;
    private LocalDateTime paymentTime;
    private LocalDateTime shipTime;
    private String logisticsCompany;
    private String logisticsNo;
}
