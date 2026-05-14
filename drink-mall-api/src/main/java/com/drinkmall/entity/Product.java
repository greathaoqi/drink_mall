package com.drinkmall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("products")
public class Product {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long categoryId;

    private String name;

    private String subtitle;

    private String description;

    private String mainImage;

    private String images;

    private BigDecimal price;

    private BigDecimal originalPrice;

    private Integer stock;

    private Integer sales;

    private String zoneType;

    @TableField("allowed_payment_methods")
    private String allowedPaymentMethods;

    @TableField("gift_points")
    private Integer giftPoints;

    @TableField("gift_points_price")
    private Integer giftPointsPrice;

    @TableField("investment_level_code")
    private String investmentLevelCode;

    @TableField("wine_bean_payable")
    private Boolean wineBeanPayable;

    private Integer status;

    private Integer sortOrder;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
