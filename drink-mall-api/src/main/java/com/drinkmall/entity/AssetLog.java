package com.drinkmall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("asset_logs")
public class AssetLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String assetType;
    private String changeType;
    private BigDecimal changeAmount;
    private BigDecimal beforeAmount;
    private BigDecimal afterAmount;
    private Long businessId;
    private String businessType;
    private String idempotencyKey;
    private String remark;
    private LocalDateTime createdAt;
}
