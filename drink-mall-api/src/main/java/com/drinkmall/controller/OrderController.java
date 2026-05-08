package com.drinkmall.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drinkmall.common.Result;
import com.drinkmall.dto.*;
import com.drinkmall.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @SaCheckLogin
    public Result<Order> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        Long userId = StpUtil.getLoginIdAsLong();
        return Result.success(orderService.createOrder(userId, request));
    }

    @GetMapping
    @SaCheckLogin
    public Result<Page<OrderResponse>> getOrders(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = StpUtil.getLoginIdAsLong();
        return Result.success(orderService.getOrders(userId, status, page, size));
    }

    @GetMapping("/{orderId}")
    @SaCheckLogin
    public Result<OrderResponse> getOrderDetail(@PathVariable Long orderId) {
        Long userId = StpUtil.getLoginIdAsLong();
        return Result.success(orderService.getOrderDetail(userId, orderId));
    }

    @PostMapping("/{orderId}/cancel")
    @SaCheckLogin
    public Result<Void> cancelOrder(@PathVariable Long orderId, @RequestParam(required = false) String reason) {
        Long userId = StpUtil.getLoginIdAsLong();
        orderService.cancelOrder(userId, orderId, reason);
        return Result.success(null);
    }

    @PostMapping("/{orderId}/confirm")
    @SaCheckLogin
    public Result<Void> confirmReceipt(@PathVariable Long orderId) {
        Long userId = StpUtil.getLoginIdAsLong();
        orderService.confirmReceipt(userId, orderId);
        return Result.success(null);
    }

    @PostMapping("/{orderId}/pay")
    @SaCheckLogin
    public Result<PayResponse> payOrder(@PathVariable Long orderId) {
        Long userId = StpUtil.getLoginIdAsLong();
        orderService.payOrder(userId, orderId);
        return Result.success(orderService.getPayParams(userId, orderId));
    }
}
