package com.drinkmall.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class CartResponse {
    private List<CartItemResponse> items;
    private Integer totalCount;
    private BigDecimal totalPrice;
    private BigDecimal selectedPrice;
}
