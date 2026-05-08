package com.drinkmall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drinkmall.dto.*;
import com.drinkmall.entity.Order;

public interface OrderService {
    Order createOrder(Long userId, CreateOrderRequest request);
    Page<OrderResponse> getOrders(Long userId, String status, Integer page, Integer size);
    OrderResponse getOrderDetail(Long userId, Long orderId);
    void cancelOrder(Long userId, Long orderId, String reason);
    void confirmReceipt(Long userId, Long orderId);
    void payOrder(Long userId, Long orderId);
    PayResponse getPayParams(Long userId, Long orderId);
}
