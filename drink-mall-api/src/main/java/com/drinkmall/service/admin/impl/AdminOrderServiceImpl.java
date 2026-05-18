package com.drinkmall.service.admin.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drinkmall.common.BusinessException;
import com.drinkmall.dto.OrderItemResponse;
import com.drinkmall.dto.OrderResponse;
import com.drinkmall.entity.Aftersale;
import com.drinkmall.entity.OperationLog;
import com.drinkmall.entity.Order;
import com.drinkmall.entity.OrderItem;
import com.drinkmall.entity.Product;
import com.drinkmall.enums.AftersaleStatus;
import com.drinkmall.enums.OrderStatus;
import com.drinkmall.enums.PaymentMethod;
import com.drinkmall.enums.ProductZoneType;
import com.drinkmall.mapper.AddressMapper;
import com.drinkmall.mapper.AftersaleMapper;
import com.drinkmall.mapper.OrderItemMapper;
import com.drinkmall.mapper.OrderMapper;
import com.drinkmall.mapper.OperationLogMapper;
import com.drinkmall.mapper.ProductMapper;
import com.drinkmall.service.PhaseOneCoreService;
import com.drinkmall.service.LevelService;
import com.drinkmall.service.RewardService;
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
    private final PhaseOneCoreService phaseOneCoreService;
    private final OperationLogMapper operationLogMapper;
    private final LevelService levelService;
    private final RewardService rewardService;

    @Override
    public IPage<OrderResponse> getOrders(String status, String orderNo, Long userId, String startDate, String endDate, Integer page, Integer size) {
        Page<Order> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        if (status != null) wrapper.eq(Order::getStatus, status);
        if (orderNo != null) wrapper.like(Order::getOrderNo, orderNo);
        if (userId != null) wrapper.eq(Order::getUserId, userId);
        if (startDate != null) wrapper.ge(Order::getCreatedAt, LocalDateTime.parse(startDate + "T00:00:00"));
        if (endDate != null) wrapper.le(Order::getCreatedAt, LocalDateTime.parse(endDate + "T23:59:59"));
        wrapper.orderByDesc(Order::getCreatedAt);
        return orderMapper.selectPage(pageParam, wrapper).convert(this::convertToResponse);
    }

    @Override
    public OrderResponse getOrderDetail(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) throw new BusinessException(404, "order not found");
        return convertToResponse(order);
    }

    @Override
    @Transactional
    public void confirmOfflineTransfer(Long orderId, Long adminUserId, String paymentNo, String reason) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) throw new BusinessException(404, "order not found");
        if (!OrderStatus.PENDING.getCode().equals(order.getStatus())) return;
        if (!PaymentMethod.OFFLINE_CORPORATE.getCode().equals(order.getPaymentMethod())) {
            throw new BusinessException(400, "only offline corporate orders can be confirmed by admin");
        }
        order.setStatus(OrderStatus.PAID.getCode());
        order.setPaymentNo(paymentNo);
        order.setPaymentTime(LocalDateTime.now());
        order.setOfflineConfirmedBy(adminUserId);
        order.setOfflineConfirmedAt(LocalDateTime.now());
        orderMapper.updateById(order);
        afterOrderPaid(order);
        logOperation("offline_confirm", orderId, "paymentNo=" + paymentNo + ", reason=" + reason);
    }

    @Override
    @Transactional
    public void shipOrder(Long orderId, String logisticsCompany, String logisticsNo, String reason) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) throw new BusinessException(404, "order not found");
        if (!OrderStatus.PAID.getCode().equals(order.getStatus())) throw new BusinessException(400, "only paid orders can be shipped");
        order.setStatus(OrderStatus.SHIPPED.getCode());
        order.setLogisticsCompany(logisticsCompany);
        order.setLogisticsNo(logisticsNo);
        order.setShipTime(LocalDateTime.now());
        orderMapper.updateById(order);
        logOperation("ship", orderId, logisticsCompany + "/" + logisticsNo + ", reason=" + reason);
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId, String reason) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) throw new BusinessException(404, "order not found");
        if (OrderStatus.COMPLETED.getCode().equals(order.getStatus()) || OrderStatus.CANCELLED.getCode().equals(order.getStatus())) {
            throw new BusinessException(400, "completed or cancelled orders cannot be cancelled");
        }
        order.setStatus(OrderStatus.CANCELLED.getCode());
        order.setCancelReason(reason);
        order.setCancelTime(LocalDateTime.now());
        orderMapper.updateById(order);
        restoreStock(orderId);
        rewardService.rollbackOrderRewards(orderId, "admin_cancel_order");
        logOperation("cancel", orderId, reason);
    }

    @Override
    @Transactional
    public void completeOrder(Long orderId, String reason) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) throw new BusinessException(404, "order not found");
        if (OrderStatus.CANCELLED.getCode().equals(order.getStatus())) throw new BusinessException(400, "cancelled orders cannot be completed");
        order.setStatus(OrderStatus.COMPLETED.getCode());
        order.setCompleteTime(LocalDateTime.now());
        orderMapper.updateById(order);
        rewardService.unfreezeDueRewards(LocalDateTime.now());
        logOperation("complete", orderId, "reason=" + reason);
    }

    @Override
    @Transactional
    public void modifyPrice(Long orderId, BigDecimal newPrice) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) throw new BusinessException(404, "order not found");
        if (!OrderStatus.PENDING.getCode().equals(order.getStatus())) throw new BusinessException(400, "only pending orders can be repriced");
        order.setPayAmount(newPrice);
        orderMapper.updateById(order);
        logOperation("modify_price", orderId, "newPrice=" + newPrice);
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
        if (aftersale == null) throw new BusinessException(404, "aftersale not found");
        aftersale.setStatus(AftersaleStatus.APPROVED.getCode());
        aftersale.setAdminRemark(remark);
        aftersale.setProcessedAt(LocalDateTime.now());
        aftersaleMapper.updateById(aftersale);
        Order order = orderMapper.selectById(aftersale.getOrderId());
        if (order != null) {
            order.setStatus(OrderStatus.AFTERSALE.getCode());
            orderMapper.updateById(order);
            restoreStock(aftersale.getOrderId());
            rewardService.rollbackOrderRewards(aftersale.getOrderId(), "aftersale_approved");
        }
        logAftersaleOperation("approve", aftersaleId, remark);
    }

    @Override
    @Transactional
    public void rejectAftersale(Long aftersaleId, String reason) {
        Aftersale aftersale = aftersaleMapper.selectById(aftersaleId);
        if (aftersale == null) throw new BusinessException(404, "aftersale not found");
        aftersale.setStatus(AftersaleStatus.REJECTED.getCode());
        aftersale.setAdminRemark(reason);
        aftersale.setProcessedAt(LocalDateTime.now());
        aftersaleMapper.updateById(aftersale);
        logAftersaleOperation("reject", aftersaleId, reason);
    }

    @Override
    @Transactional
    public void closeAftersale(Long aftersaleId, String reason) {
        Aftersale aftersale = requireAftersale(aftersaleId);
        aftersale.setStatus(AftersaleStatus.CLOSED.getCode());
        aftersale.setAdminRemark(reason);
        aftersale.setProcessedAt(LocalDateTime.now());
        aftersaleMapper.updateById(aftersale);
        logAftersaleOperation("close", aftersaleId, reason);
    }

    @Override
    @Transactional
    public void completeAftersale(Long aftersaleId, String remark) {
        Aftersale aftersale = requireAftersale(aftersaleId);
        aftersale.setStatus(AftersaleStatus.COMPLETED.getCode());
        aftersale.setAdminRemark(remark);
        aftersale.setProcessedAt(LocalDateTime.now());
        aftersaleMapper.updateById(aftersale);
        logAftersaleOperation("complete", aftersaleId, remark);
    }

    @Override
    @Transactional
    public void recordOfflineAftersaleResult(Long aftersaleId, String result) {
        Aftersale aftersale = requireAftersale(aftersaleId);
        aftersale.setStatus(AftersaleStatus.OFFLINE_PROCESSED.getCode());
        aftersale.setOfflineProcessResult(result);
        aftersale.setOfflineProcessedAt(LocalDateTime.now());
        aftersale.setOfflineProcessorAdminId(currentAdminId());
        aftersale.setProcessedAt(LocalDateTime.now());
        aftersaleMapper.updateById(aftersale);
        logAftersaleOperation("offline_process", aftersaleId, result);
    }

    @Override
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalOrders", orderMapper.selectCount(null));
        stats.put("pendingOrders", orderMapper.selectCount(new LambdaQueryWrapper<Order>().eq(Order::getStatus, OrderStatus.PENDING.getCode())));
        stats.put("paidOrders", orderMapper.selectCount(new LambdaQueryWrapper<Order>().eq(Order::getStatus, OrderStatus.PAID.getCode())));
        stats.put("shippedOrders", orderMapper.selectCount(new LambdaQueryWrapper<Order>().eq(Order::getStatus, OrderStatus.SHIPPED.getCode())));
        stats.put("completedOrders", orderMapper.selectCount(new LambdaQueryWrapper<Order>().eq(Order::getStatus, OrderStatus.COMPLETED.getCode())));
        stats.put("cancelledOrders", orderMapper.selectCount(new LambdaQueryWrapper<Order>().eq(Order::getStatus, OrderStatus.CANCELLED.getCode())));
        return stats;
    }

    private void afterOrderPaid(Order order) {
        String zoneType = order.getZoneType() == null ? orderPrimaryZone(order.getId()) : order.getZoneType();
        if (ProductZoneType.MAIN.getCode().equals(zoneType)) {
            levelService.recordMainProductPaid(order.getUserId(), order.getPayAmount(), order.getOrderNo());
        }
        if (ProductZoneType.INVESTMENT.getCode().equals(zoneType)) {
            levelService.upgradeByInvestmentOrder(order);
        }
        rewardService.settleOrderRewards(order);
    }

    private void restoreStock(Long orderId) {
        List<OrderItem> items = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId));
        for (OrderItem item : items) {
            Product product = productMapper.selectById(item.getProductId());
            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
                product.setSales(Math.max(0, product.getSales() - item.getQuantity()));
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
                .zoneType(order.getZoneType())
                .paymentMethod(order.getPaymentMethod())
                .totalAmount(order.getTotalAmount())
                .payAmount(order.getPayAmount())
                .pointsAmount(order.getPointsAmount())
                .items(items.stream().map(i -> OrderItemResponse.builder()
                        .itemId(i.getId())
                        .productId(i.getProductId())
                        .productName(i.getProductName())
                        .productImage(i.getProductImage())
                        .price(i.getPrice())
                        .quantity(i.getQuantity())
                        .totalAmount(i.getTotalAmount())
                        .zoneType(i.getZoneType())
                        .giftPoints(i.getGiftPoints())
                        .pointsAmount(i.getPointsAmount())
                        .build()).collect(Collectors.toList()))
                .createdAt(order.getCreatedAt())
                .paymentTime(order.getPaymentTime())
                .shipTime(order.getShipTime())
                .logisticsCompany(order.getLogisticsCompany())
                .logisticsNo(order.getLogisticsNo())
                .build();
    }

    private String orderPrimaryZone(Long orderId) {
        List<OrderItem> items = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId));
        if (items.isEmpty()) return "";
        if (items.get(0).getZoneType() != null) return items.get(0).getZoneType();
        Product product = productMapper.selectById(items.get(0).getProductId());
        return product == null ? "" : product.getZoneType();
    }

    private Aftersale requireAftersale(Long aftersaleId) {
        Aftersale aftersale = aftersaleMapper.selectById(aftersaleId);
        if (aftersale == null) throw new BusinessException(404, "aftersale not found");
        return aftersale;
    }

    private void logOperation(String action, Long orderId, String detail) {
        OperationLog log = new OperationLog();
        log.setAdminUserId(currentAdminId());
        log.setModule("order");
        log.setAction(action);
        log.setTargetId(String.valueOf(orderId));
        log.setDetail(detail);
        log.setCreatedAt(LocalDateTime.now());
        operationLogMapper.insert(log);
    }

    private void logAftersaleOperation(String action, Long aftersaleId, String detail) {
        OperationLog log = new OperationLog();
        log.setAdminUserId(currentAdminId());
        log.setModule("aftersale");
        log.setAction(action);
        log.setTargetId(String.valueOf(aftersaleId));
        log.setDetail(detail);
        log.setCreatedAt(LocalDateTime.now());
        operationLogMapper.insert(log);
    }

    private Long currentAdminId() {
        try {
            return StpUtil.getLoginIdAsLong();
        } catch (Exception ignored) {
            return null;
        }
    }
}
