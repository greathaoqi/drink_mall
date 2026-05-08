package com.drinkmall.service;

public interface PointsService {
    Integer getPointsBalance(Long userId);
    Long redeemProduct(Long userId, Long productId);
    void addPoints(Long userId, Integer points, Long orderId, String remark);
}
