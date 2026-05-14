package com.drinkmall.service.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drinkmall.dto.OrderResponse;
import com.drinkmall.entity.Aftersale;
import java.math.BigDecimal;
import java.util.Map;

public interface AdminOrderService {
    IPage<OrderResponse> getOrders(String status, String orderNo, Long userId, String startDate, String endDate, Integer page, Integer size);
    OrderResponse getOrderDetail(Long orderId);
    void confirmOfflineTransfer(Long orderId, Long adminUserId, String paymentNo);
    void shipOrder(Long orderId, String logisticsCompany, String logisticsNo);
    void cancelOrder(Long orderId, String reason);
    void completeOrder(Long orderId);
    void modifyPrice(Long orderId, BigDecimal newPrice);
    Page<Aftersale> getAftersales(String status, Integer page, Integer size);
    void approveAftersale(Long aftersaleId, String remark);
    void rejectAftersale(Long aftersaleId, String reason);
    void closeAftersale(Long aftersaleId, String reason);
    void completeAftersale(Long aftersaleId, String remark);
    void recordOfflineAftersaleResult(Long aftersaleId, String result);
    Map<String, Object> getStatistics();
}
