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
    private String idempotencyKey;
    private LocalDateTime createdAt;
}
