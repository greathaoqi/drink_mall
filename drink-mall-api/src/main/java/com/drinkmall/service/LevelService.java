package com.drinkmall.service;

import com.drinkmall.entity.Order;

import java.math.BigDecimal;

public interface LevelService {
    void recordMainProductPaid(Long userId, BigDecimal amount, String orderNo);

    void upgradeByInvestmentOrder(Order order);

    void upgradeTo(Long userId, String targetLevel, String reason, Long businessId);
}
