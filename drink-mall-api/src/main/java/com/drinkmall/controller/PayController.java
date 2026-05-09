package com.drinkmall.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.drinkmall.common.Result;
import com.drinkmall.entity.Order;
import com.drinkmall.entity.PointsLog;
import com.drinkmall.entity.User;
import com.drinkmall.mapper.OrderMapper;
import com.drinkmall.mapper.PointsLogMapper;
import com.drinkmall.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/pay")
@RequiredArgsConstructor
public class PayController {

    private final OrderMapper orderMapper;
    private final UserMapper userMapper;
    private final PointsLogMapper pointsLogMapper;

    @PostMapping("/callback")
    public String handleWeChatPayCallback(@RequestBody Map<String, Object> data) {
        log.info("Received WeChat Pay callback: {}", data);
        String orderNo = (String) data.get("out_trade_no");
        if (orderNo == null) {
            return "FAIL";
        }
        Order order = orderMapper.selectOne(new LambdaQueryWrapper<Order>().eq(Order::getOrderNo, orderNo));
        if (order == null || !"pending".equals(order.getStatus())) {
            return "SUCCESS";
        }
        order.setStatus("paid");
        order.setPaymentMethod("wechat");
        order.setPaymentTime(LocalDateTime.now());
        orderMapper.updateById(order);
        addPointsForOrder(order);
        return "SUCCESS";
    }

    private void addPointsForOrder(Order order) {
        User user = userMapper.selectById(order.getUserId());
        if (user == null) return;
        int pointsRate = 1;
        int points = order.getPayAmount().multiply(BigDecimal.valueOf(pointsRate)).intValue();
        if (points > 0) {
            user.setPoints((user.getPoints() != null ? user.getPoints() : 0) + points);
            userMapper.updateById(user);
            PointsLog log = new PointsLog();
            log.setUserId(user.getId());
            log.setPoints(points);
            log.setType("purchase");
            log.setRemark("订单" + order.getOrderNo() + "获得积分");
            log.setCreatedAt(LocalDateTime.now());
            pointsLogMapper.insert(log);
        }
    }
}