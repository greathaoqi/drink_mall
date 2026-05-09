package com.drinkmall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("withdrawal")
public class Withdrawal {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private BigDecimal amount;
    private String bankName;
    private String bankAccount;
    private String accountName;
    private String status;
    private String rejectReason;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
}
