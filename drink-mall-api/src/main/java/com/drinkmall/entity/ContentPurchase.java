package com.drinkmall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("content_purchases")
public class ContentPurchase {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String contentType;
    private Long contentId;
    private BigDecimal price;
    private String paymentMethod;
    private String idempotencyKey;
    private String status = "pending";  // pending, paid, cancelled, expired
    private String orderNo;  // CP-prefixed order number
    private String paymentNo;  // WeChat Pay transaction ID
    private LocalDateTime paymentTime;  // Payment confirmation time
    private LocalDateTime createdAt;
}
