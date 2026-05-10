package com.drinkmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drinkmall.common.BusinessException;
import com.drinkmall.entity.Aftersale;
import com.drinkmall.entity.Order;
import com.drinkmall.mapper.AftersaleMapper;
import com.drinkmall.mapper.OrderMapper;
import com.drinkmall.service.AftersaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AftersaleServiceImpl implements AftersaleService {

    private final AftersaleMapper aftersaleMapper;
    private final OrderMapper orderMapper;

    @Override
    public Aftersale applyAftersale(Long userId, Long orderId, String type, String reason, String description) {
        Order order = orderMapper.selectOne(
            new LambdaQueryWrapper<Order>()
                .eq(Order::getId, orderId)
                .eq(Order::getUserId, userId)
        );
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        if (!"paid".equals(order.getStatus()) && !"shipped".equals(order.getStatus()) && !"completed".equals(order.getStatus())) {
            throw new BusinessException(400, "当前订单状态不支持申请售后");
        }
        long existing = aftersaleMapper.selectCount(
            new LambdaQueryWrapper<Aftersale>()
                .eq(Aftersale::getOrderId, orderId)
                .in(Aftersale::getStatus, "pending", "processing")
        );
        if (existing > 0) {
            throw new BusinessException(400, "该订单已有处理中的售后申请");
        }

        Aftersale aftersale = new Aftersale();
        aftersale.setOrderId(orderId);
        aftersale.setUserId(userId);
        aftersale.setType(type);
        aftersale.setReason(reason);
        aftersale.setDescription(description);
        aftersale.setStatus("pending");
        aftersale.setCreatedAt(LocalDateTime.now());
        aftersaleMapper.insert(aftersale);
        return aftersale;
    }

    @Override
    public Page<Aftersale> getUserAftersales(Long userId, Integer page, Integer size) {
        return aftersaleMapper.selectPage(
            new Page<>(page, size),
            new LambdaQueryWrapper<Aftersale>()
                .eq(Aftersale::getUserId, userId)
                .orderByDesc(Aftersale::getCreatedAt)
        );
    }

    @Override
    public Aftersale getAftersaleDetail(Long userId, Long aftersaleId) {
        Aftersale aftersale = aftersaleMapper.selectById(aftersaleId);
        if (aftersale == null || !aftersale.getUserId().equals(userId)) {
            throw new BusinessException(404, "售后申请不存在");
        }
        return aftersale;
    }
}
