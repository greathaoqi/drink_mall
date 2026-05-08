package com.drinkmall.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drinkmall.common.BusinessException;
import com.drinkmall.dto.*;
import com.drinkmall.entity.*;
import com.drinkmall.mapper.*;
import com.drinkmall.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final CartMapper cartMapper;
    private final ProductMapper productMapper;
    private final AddressMapper addressMapper;
    private final UserService userService;

    @Override
    @Transactional
    public Order createOrder(Long userId, CreateOrderRequest request) {
        User user = userService.getById(userId);
        if (!Boolean.TRUE.equals(user.getAgeVerified())) {
            throw new BusinessException(400, "请先完成年龄验证");
        }

        Address address = addressMapper.selectById(request.getAddressId());
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException(400, "请选择收货地址");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> items = new ArrayList<>();

        for (CreateOrderRequest.CartItemInfo itemInfo : request.getItems()) {
            Product product = productMapper.selectById(itemInfo.getProductId());
            if (product == null || product.getStatus() != 1) {
                throw new BusinessException(404, "商品不存在: " + itemInfo.getProductId());
            }
            if (product.getStock() < itemInfo.getQuantity()) {
                throw new BusinessException(400, "库存不足: " + product.getName());
            }

            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(itemInfo.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);

            OrderItem item = new OrderItem();
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setProductImage(product.getMainImage());
            item.setPrice(product.getPrice());
            item.setQuantity(itemInfo.getQuantity());
            item.setTotalAmount(itemTotal);
            items.add(item);

            product.setStock(product.getStock() - itemInfo.getQuantity());
            product.setSales(product.getSales() + itemInfo.getQuantity());
            productMapper.updateById(product);
        }

        Order order = new Order();
        order.setOrderNo("DM" + IdUtil.getSnowflakeNextIdStr());
        order.setUserId(userId);
        order.setAddressId(address.getId());
        order.setTotalAmount(totalAmount);
        order.setPayAmount(totalAmount);
        order.setStatus("pending");
        order.setCreatedAt(LocalDateTime.now());
        orderMapper.insert(order);

        for (OrderItem item : items) {
            item.setOrderId(order.getId());
            orderItemMapper.insert(item);
        }

        cartMapper.delete(new LambdaQueryWrapper<Cart>().eq(Cart::getUserId, userId));

        return order;
    }

    @Override
    public Page<OrderResponse> getOrders(Long userId, String status, Integer page, Integer size) {
        Page<Order> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, userId);

        if (status != null && !status.isEmpty()) {
            wrapper.eq(Order::getStatus, status);
        }
        wrapper.orderByDesc(Order::getCreatedAt);

        Page<Order> orders = orderMapper.selectPage(pageParam, wrapper);

        return orders.convert(this::convertToResponse);
    }

    @Override
    public OrderResponse getOrderDetail(Long userId, Long orderId) {
        Order order = orderMapper.selectOne(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getId, orderId)
                        .eq(Order::getUserId, userId)
        );
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        return convertToResponse(order);
    }

    @Override
    @Transactional
    public void cancelOrder(Long userId, Long orderId, String reason) {
        Order order = orderMapper.selectOne(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getId, orderId)
                        .eq(Order::getUserId, userId)
        );
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        if (!"pending".equals(order.getStatus())) {
            throw new BusinessException(400, "只能取消待付款订单");
        }

        order.setStatus("cancelled");
        order.setCancelReason(reason);
        order.setCancelTime(LocalDateTime.now());
        orderMapper.updateById(order);

        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId)
        );
        for (OrderItem item : items) {
            Product product = productMapper.selectById(item.getProductId());
            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
                product.setSales(product.getSales() - item.getQuantity());
                productMapper.updateById(product);
            }
        }
    }

    @Override
    @Transactional
    public void confirmReceipt(Long userId, Long orderId) {
        Order order = orderMapper.selectOne(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getId, orderId)
                        .eq(Order::getUserId, userId)
        );
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        if (!"shipped".equals(order.getStatus())) {
            throw new BusinessException(400, "只能确认已发货订单");
        }

        order.setStatus("completed");
        order.setCompleteTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Override
    @Transactional
    public void payOrder(Long userId, Long orderId) {
        Order order = orderMapper.selectOne(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getId, orderId)
                        .eq(Order::getUserId, userId)
        );
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        if (!"pending".equals(order.getStatus())) {
            throw new BusinessException(400, "订单状态不正确");
        }

        order.setStatus("paid");
        order.setPaymentMethod("wechat");
        order.setPaymentTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Override
    public PayResponse getPayParams(Long userId, Long orderId) {
        return PayResponse.builder()
                .orderNo(orderMapper.selectById(orderId).getOrderNo())
                .build();
    }

    private OrderResponse convertToResponse(Order order) {
        Address address = addressMapper.selectById(order.getAddressId());
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, order.getId())
        );

        return OrderResponse.builder()
                .orderId(order.getId())
                .orderNo(order.getOrderNo())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .payAmount(order.getPayAmount())
                .address(address != null ? AddressResponse.fromEntity(address) : null)
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
