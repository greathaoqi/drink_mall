package com.drinkmall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("aftersale")
public class Aftersale {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orderId;
    private Long userId;
    private String type;
    private String reason;
    private String description;
    private String images;
    private BigDecimal refundAmount;
    private String status;
    private String adminRemark;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
}
