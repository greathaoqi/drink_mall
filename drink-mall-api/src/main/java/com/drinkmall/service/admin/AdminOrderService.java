package com.drinkmall.service.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drinkmall.dto.OrderResponse;
import com.drinkmall.entity.Aftersale;
import java.math.BigDecimal;
import java.util.Map;

public interface AdminOrderService {
    Page<OrderResponse> getOrders(String status, String orderNo, Long userId, String startDate, String endDate, Integer page, Integer size);
    OrderResponse getOrderDetail(Long orderId);
    void shipOrder(Long orderId, String logisticsCompany, String logisticsNo);
    void cancelOrder(Long orderId, String reason);
    void modifyPrice(Long orderId, BigDecimal newPrice);
    Page<Aftersale> getAftersales(String status, Integer page, Integer size);
    void approveAftersale(Long aftersaleId, String remark);
    void rejectAftersale(Long aftersaleId, String reason);
    Map<String, Object> getStatistics();
}
