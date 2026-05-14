package com.drinkmall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("orders")
public class Order {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;
    private Long userId;
    private Long addressId;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    private Integer pointsAmount;
    private String zoneType;
    private String status;
    private String paymentMethod;
    private String paymentNo;
    private String offlineTransferNo;
    private Long offlineConfirmedBy;
    private LocalDateTime offlineConfirmedAt;
    private LocalDateTime paymentTime;
    private LocalDateTime shipTime;
    private String logisticsCompany;
    private String logisticsNo;
    private LocalDateTime completeTime;
    private LocalDateTime cancelTime;
    private String cancelReason;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
