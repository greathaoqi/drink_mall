package com.drinkmall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("withdrawals")
public class Withdrawal {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private BigDecimal amount;
    private BigDecimal feeAmount;
    private BigDecimal actualAmount;
    private String bankName;
    private String bankAccount;
    @TableField("account_name")
    private String accountName;
    private String offlineTransferNo;
    private Long reviewerAdminId;
    private String status;
    private String rejectReason;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
}
