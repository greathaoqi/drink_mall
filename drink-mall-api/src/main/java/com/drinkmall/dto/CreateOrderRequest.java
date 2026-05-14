package com.drinkmall.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreateOrderRequest {
    private Long addressId;
    private List<CartItemInfo> items;
    private List<Long> cartIds;
    private String paymentMethod;
    private String remark;

    @Data
    public static class CartItemInfo {
        private Long productId;
        private Integer quantity;
    }
}
