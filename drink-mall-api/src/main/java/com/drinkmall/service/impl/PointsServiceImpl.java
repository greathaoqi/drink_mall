package com.drinkmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.drinkmall.common.BusinessException;
import com.drinkmall.dto.CreateOrderRequest;
import com.drinkmall.entity.*;
import com.drinkmall.mapper.*;
import com.drinkmall.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointsServiceImpl implements PointsService {

    private final UserMapper userMapper;
    private final ProductMapper productMapper;
    private final PointsLogMapper pointsLogMapper;
    private final OrderService orderService;

    @Override
    public Integer getPointsBalance(Long userId) {
        User user = userMapper.selectById(userId);
        return user != null ? user.getPoints() : 0;
    }

    @Override
    @Transactional
    public Long redeemProduct(Long userId, Long productId) {
        User user = userMapper.selectById(userId);
        Product product = productMapper.selectById(productId);

        if (product == null || product.getStatus() != 1) {
            throw new BusinessException(404, "商品不存在");
        }
        if (!"gift".equals(product.getZoneType())) {
            throw new BusinessException(400, "该商品不支持积分兑换");
        }

        int requiredPoints = product.getPrice().intValue();
        if (user.getPoints() < requiredPoints) {
            throw new BusinessException(400, "积分不足");
        }

        int beforePoints = user.getPoints();
        user.setPoints(beforePoints - requiredPoints);
        userMapper.updateById(user);

        PointsLog log = new PointsLog();
        log.setUserId(userId);
        log.setChangeType("redeem");
        log.setPoints(-requiredPoints);
        log.setBeforePoints(beforePoints);
        log.setAfterPoints(user.getPoints());
        log.setRemark("兑换商品: " + product.getName());
        pointsLogMapper.insert(log);

        CreateOrderRequest request = new CreateOrderRequest();
        CreateOrderRequest.CartItemInfo item = new CreateOrderRequest.CartItemInfo();
        item.setProductId(productId);
        item.setQuantity(1);
        request.setItems(java.util.Collections.singletonList(item));

        return orderService.createOrder(userId, request).getId();
    }

    @Override
    @Transactional
    public void addPoints(Long userId, Integer points, Long orderId, String remark) {
        User user = userMapper.selectById(userId);
        if (user == null) return;

        int beforePoints = user.getPoints();
        user.setPoints(beforePoints + points);
        userMapper.updateById(user);

        PointsLog log = new PointsLog();
        log.setUserId(userId);
        log.setChangeType("purchase");
        log.setPoints(points);
        log.setBeforePoints(beforePoints);
        log.setAfterPoints(user.getPoints());
        log.setOrderId(orderId);
        log.setRemark(remark);
        pointsLogMapper.insert(log);
    }
}
