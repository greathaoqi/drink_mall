package com.drinkmall.controller.admin;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drinkmall.common.Result;
import com.drinkmall.dto.OrderResponse;
import com.drinkmall.entity.Aftersale;
import com.drinkmall.entity.Order;
import com.drinkmall.service.admin.AdminOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/order")
@RequiredArgsConstructor
@SaCheckRole("admin")
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    @GetMapping("/list")
    public Result<IPage<OrderResponse>> getOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        return Result.success(adminOrderService.getOrders(status, orderNo, userId, startDate, endDate, page, size));
    }

    @GetMapping("/{orderId}")
    public Result<OrderResponse> getOrderDetail(@PathVariable Long orderId) {
        return Result.success(adminOrderService.getOrderDetail(orderId));
    }

    @PutMapping("/{orderId}/ship")
    public Result<Void> shipOrder(@PathVariable Long orderId, @RequestParam String logisticsCompany, @RequestParam String logisticsNo) {
        adminOrderService.shipOrder(orderId, logisticsCompany, logisticsNo);
        return Result.success(null);
    }

    @PutMapping("/{orderId}/offline-confirm")
    public Result<Void> confirmOfflineTransfer(@PathVariable Long orderId, @RequestParam String paymentNo) {
        adminOrderService.confirmOfflineTransfer(orderId, StpUtil.getLoginIdAsLong(), paymentNo);
        return Result.success(null);
    }

    @PutMapping("/{orderId}/cancel")
    public Result<Void> cancelOrder(@PathVariable Long orderId, @RequestParam String reason) {
        adminOrderService.cancelOrder(orderId, reason);
        return Result.success(null);
    }

    @PutMapping("/{orderId}/complete")
    public Result<Void> completeOrder(@PathVariable Long orderId) {
        adminOrderService.completeOrder(orderId);
        return Result.success(null);
    }

    @PutMapping("/{orderId}/price")
    public Result<Void> modifyPrice(@PathVariable Long orderId, @RequestParam BigDecimal newPrice) {
        adminOrderService.modifyPrice(orderId, newPrice);
        return Result.success(null);
    }

    @GetMapping("/aftersale/list")
    public Result<Page<Aftersale>> getAftersales(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        return Result.success(adminOrderService.getAftersales(status, page, size));
    }

    @PutMapping("/aftersale/{aftersaleId}/approve")
    public Result<Void> approveAftersale(@PathVariable Long aftersaleId, @RequestParam(required = false) String remark) {
        adminOrderService.approveAftersale(aftersaleId, remark);
        return Result.success(null);
    }

    @PutMapping("/aftersale/{aftersaleId}/reject")
    public Result<Void> rejectAftersale(@PathVariable Long aftersaleId, @RequestParam String reason) {
        adminOrderService.rejectAftersale(aftersaleId, reason);
        return Result.success(null);
    }

    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        return Result.success(adminOrderService.getStatistics());
    }
}
