package com.drinkmall.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PayOrderRequest {
    @NotBlank(message = "支付方式不能为空")
    private String paymentMethod;
    private String paymentNo;
}
