package com.drinkmall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("stock_logs")
public class StockLog {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long productId;

    private String changeType;

    private Integer changeQuantity;

    private Integer beforeStock;

    private Integer afterStock;

    private Long orderId;

    private String operator;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
