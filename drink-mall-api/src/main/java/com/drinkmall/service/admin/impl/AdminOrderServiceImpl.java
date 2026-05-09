package com.drinkmall.service.admin.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drinkmall.common.BusinessException;
import com.drinkmall.dto.OrderResponse;
import com.drinkmall.entity.Aftersale;
import com.drinkmall.entity.Order;
import com.drinkmall.entity.OrderItem;
import com.drinkmall.entity.Product;
import com.drinkmall.mapper.*;
import com.drinkmall.service.admin.AdminOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminOrderServiceImpl implements AdminOrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final AftersaleMapper aftersaleMapper;
    private final ProductMapper productMapper;
    private final AddressMapper addressMapper;

    @Override
    public Page<OrderResponse> getOrders(String status, String orderNo, Long userId, String startDate, String endDate, Integer page, Integer size) {
        Page<Order> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        if (status != null) wrapper.eq(Order::getStatus, status);
        if (orderNo != null) wrapper.like(Order::getOrderNo, orderNo);
        if (userId != null) wrapper.eq(Order::getUserId, userId);
        if (startDate != null) wrapper.ge(Order::getCreatedAt, LocalDateTime.parse(startDate + "T00:00:00"));
        if (endDate != null) wrapper.le(Order::getCreatedAt, LocalDateTime.parse(endDate + "T23:59:59"));
        wrapper.orderByDesc(Order::getCreatedAt);
        Page<Order> orders = orderMapper.selectPage(pageParam, wrapper);
        return orders.convert(this::convertToResponse);
    }

    @Override
    public OrderResponse getOrderDetail(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) throw new BusinessException(404, "订单不存在");
        return convertToResponse(order);
    }

    @Override
    @Transactional
    public void shipOrder(Long orderId, String logisticsCompany, String logisticsNo) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) throw new BusinessException(404, "订单不存在");
        if (!"paid".equals(order.getStatus())) throw new BusinessException(400, "只能发货已支付订单");
        order.setStatus("shipped");
        order.setLogisticsCompany(logisticsCompany);
        order.setLogisticsNo(logisticsNo);
        order.setShipTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId, String reason) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) throw new BusinessException(404, "订单不存在");
        if ("completed".equals(order.getStatus()) || "cancelled".equals(order.getStatus()))
            throw new BusinessException(400, "订单已完成或已取消");
        order.setStatus("cancelled");
        order.setCancelReason(reason);
        order.setCancelTime(LocalDateTime.now());
        orderMapper.updateById(order);
        restoreStock(orderId);
    }

    @Override
    @Transactional
    public void modifyPrice(Long orderId, BigDecimal newPrice) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) throw new BusinessException(404, "订单不存在");
        if (!"pending".equals(order.getStatus())) throw new BusinessException(400, "只能修改待付款订单价格");
        order.setPayAmount(newPrice);
        orderMapper.updateById(order);
    }

    @Override
    public Page<Aftersale> getAftersales(String status, Integer page, Integer size) {
        Page<Aftersale> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Aftersale> wrapper = new LambdaQueryWrapper<>();
        if (status != null) wrapper.eq(Aftersale::getStatus, status);
        wrapper.orderByDesc(Aftersale::getCreatedAt);
        return aftersaleMapper.selectPage(pageParam, wrapper);
    }

    @Override
    @Transactional
    public void approveAftersale(Long aftersaleId, String remark) {
        Aftersale aftersale = aftersaleMapper.selectById(aftersaleId);
        if (aftersale == null) throw new BusinessException(404, "售后申请不存在");
        aftersale.setStatus("approved");
        aftersale.setAdminRemark(remark);
        aftersale.setProcessedAt(LocalDateTime.now());
        aftersaleMapper.updateById(aftersale);
        Order order = orderMapper.selectById(aftersale.getOrderId());
        if (order != null) {
            order.setStatus("refunded");
            orderMapper.updateById(order);
            restoreStock(aftersale.getOrderId());
        }
    }

    @Override
    @Transactional
    public void rejectAftersale(Long aftersaleId, String reason) {
        Aftersale aftersale = aftersaleMapper.selectById(aftersaleId);
        if (aftersale == null) throw new BusinessException(404, "售后申请不存在");
        aftersale.setStatus("rejected");
        aftersale.setAdminRemark(reason);
        aftersale.setProcessedAt(LocalDateTime.now());
        aftersaleMapper.updateById(aftersale);
    }

    @Override
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalOrders", orderMapper.selectCount(null));
        stats.put("pendingOrders", orderMapper.selectCount(new LambdaQueryWrapper<Order>().eq(Order::getStatus, "pending")));
        stats.put("paidOrders", orderMapper.selectCount(new LambdaQueryWrapper<Order>().eq(Order::getStatus, "paid")));
        stats.put("shippedOrders", orderMapper.selectCount(new LambdaQueryWrapper<Order>().eq(Order::getStatus, "shipped")));
        stats.put("completedOrders", orderMapper.selectCount(new LambdaQueryWrapper<Order>().eq(Order::getStatus, "completed")));
        stats.put("cancelledOrders", orderMapper.selectCount(new LambdaQueryWrapper<Order>().eq(Order::getStatus, "cancelled")));
        return stats;
    }

    private void restoreStock(Long orderId) {
        List<OrderItem> items = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId));
        for (OrderItem item : items) {
            Product product = productMapper.selectById(item.getProductId());
            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
                product.setSales(product.getSales() - item.getQuantity());
                productMapper.updateById(product);
            }
        }
    }

    private OrderResponse convertToResponse(Order order) {
        List<OrderItem> items = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, order.getId()));
        return OrderResponse.builder()
                .orderId(order.getId())
                .orderNo(order.getOrderNo())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .payAmount(order.getPayAmount())
                .items(items.stream().map(i -> OrderItemResponse.builder()
                        .itemId(i.getId())
                        .productId(i.getProductId())
                        .productName(i.getProductName())
                        .productImage(i.getProductImage())
                        .price(i.getPrice())
                        .quantity(i.getQuantity())
                        .totalAmount(i.getTotalAmount())
                        .build()).collect(Collectors.toList()))
                .createdAt(order.getCreatedAt())
                .paymentTime(order.getPaymentTime())
                .shipTime(order.getShipTime())
                .build();
    }
}