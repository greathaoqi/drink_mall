package com.drinkmall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drinkmall.entity.AssetLog;
import com.drinkmall.enums.AssetType;

import java.math.BigDecimal;
import java.util.Map;

public interface AssetService {
    Map<String, Object> getSummary(Long userId);

    Page<AssetLog> getLogs(Long userId, AssetType assetType, Integer page, Integer size);

    void add(Long userId, AssetType assetType, BigDecimal amount, String changeType, Long businessId,
             String businessType, String idempotencyKey, String remark);

    void deduct(Long userId, AssetType assetType, BigDecimal amount, String changeType, Long businessId,
                String businessType, String idempotencyKey, String remark);

    void freezeBalance(Long userId, BigDecimal amount, String changeType, Long businessId,
                       String businessType, String idempotencyKey, String remark);

    void releaseFrozenBalance(Long userId, BigDecimal amount, String changeType, Long businessId,
                              String businessType, String idempotencyKey, String remark);

    void consumeFrozenBalance(Long userId, BigDecimal amount, String changeType, Long businessId,
                              String businessType, String idempotencyKey, String remark);

    void exchangeDfToWineBean(Long userId, BigDecimal amount);

    void transferDf(Long fromUserId, Long toUserId, BigDecimal amount);

    void adminAdjust(Long adminId, Long userId, AssetType assetType, BigDecimal amount, String reason);
}
