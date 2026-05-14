package com.drinkmall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("asset_accounts")
public class AssetAccount {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String assetType;
    private BigDecimal availableAmount;
    private BigDecimal frozenAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
