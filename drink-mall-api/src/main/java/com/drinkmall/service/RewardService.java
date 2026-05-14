package com.drinkmall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drinkmall.entity.Order;
import com.drinkmall.entity.RewardRecord;

import java.time.LocalDateTime;

public interface RewardService {
    void settleOrderRewards(Order order);

    void unfreezeDueRewards(LocalDateTime now);

    void rollbackOrderRewards(Long orderId, String reason);

    Page<RewardRecord> getRecords(Long userId, String status, Integer page, Integer size);
}
