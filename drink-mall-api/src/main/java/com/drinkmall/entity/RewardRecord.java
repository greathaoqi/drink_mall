package com.drinkmall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("reward_records")
public class RewardRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long sourceUserId;
    private Long beneficiaryUserId;
    private Long orderId;
    private String orderNo;
    private String rewardType;
    private BigDecimal baseAmount;
    private BigDecimal ratio;
    private BigDecimal amount;
    private String status;
    private LocalDateTime frozenUntil;
    private String idempotencyKey;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
