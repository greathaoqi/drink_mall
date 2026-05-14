package com.drinkmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.drinkmall.common.BusinessException;
import com.drinkmall.dto.CreateOrderRequest;
import com.drinkmall.dto.PayOrderRequest;
import com.drinkmall.entity.Address;
import com.drinkmall.entity.Order;
import com.drinkmall.entity.PointsLog;
import com.drinkmall.entity.Product;
import com.drinkmall.entity.User;
import com.drinkmall.enums.PaymentMethod;
import com.drinkmall.mapper.AddressMapper;
import com.drinkmall.mapper.PointsLogMapper;
import com.drinkmall.mapper.ProductMapper;
import com.drinkmall.mapper.UserMapper;
import com.drinkmall.service.OrderService;
import com.drinkmall.service.PointsService;
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
    private final AddressMapper addressMapper;

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

        int requiredPoints = product.getGiftPointsPrice() == null ? product.getPrice().intValue() : product.getGiftPointsPrice();
        if (user == null || user.getPoints() == null || user.getPoints() < requiredPoints) {
            throw new BusinessException(400, "积分不足");
        }

        CreateOrderRequest request = new CreateOrderRequest();
        request.setAddressId(resolveAddressId(userId));
        request.setPaymentMethod(PaymentMethod.POINTS.getCode());

        CreateOrderRequest.CartItemInfo item = new CreateOrderRequest.CartItemInfo();
        item.setProductId(productId);
        item.setQuantity(1);
        request.setItems(java.util.Collections.singletonList(item));

        Order order = orderService.createOrder(userId, request);
        PayOrderRequest payRequest = new PayOrderRequest();
        payRequest.setPaymentMethod(PaymentMethod.POINTS.getCode());
        orderService.payOrder(userId, order.getId(), payRequest);
        return order.getId();
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

    private Long resolveAddressId(Long userId) {
        Address address = addressMapper.selectOne(new LambdaQueryWrapper<Address>()
                .eq(Address::getUserId, userId)
                .eq(Address::getIsDefault, true)
                .last("LIMIT 1"));
        if (address == null) {
            address = addressMapper.selectOne(new LambdaQueryWrapper<Address>()
                    .eq(Address::getUserId, userId)
                    .last("LIMIT 1"));
        }
        if (address == null) {
            throw new BusinessException(400, "请先添加收货地址");
        }
        return address.getId();
    }
}
