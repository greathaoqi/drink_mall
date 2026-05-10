package com.drinkmall.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateAftersaleRequest {
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    private String type = "refund";

    @NotBlank(message = "问题描述不能为空")
    private String reason;

    private String description;
}
