package com.drinkmall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("balance_logs")
public class BalanceLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String changeType;
    private BigDecimal amount;
    private BigDecimal beforeBalance;
    private BigDecimal afterBalance;
    private Long orderId;
    private String remark;
    private LocalDateTime createdAt;
}
