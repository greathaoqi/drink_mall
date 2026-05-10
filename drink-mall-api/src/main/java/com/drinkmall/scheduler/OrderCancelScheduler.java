package com.drinkmall.scheduler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.drinkmall.entity.Order;
import com.drinkmall.entity.OrderItem;
import com.drinkmall.entity.Product;
import com.drinkmall.mapper.OrderItemMapper;
import com.drinkmall.mapper.OrderMapper;
import com.drinkmall.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCancelScheduler {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ProductMapper productMapper;

    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void cancelExpiredOrders() {
        LocalDateTime expiry = LocalDateTime.now().minusMinutes(30);
        List<Order> expiredOrders = orderMapper.selectList(
            new LambdaQueryWrapper<Order>()
                .eq(Order::getStatus, "pending")
                .lt(Order::getCreatedAt, expiry)
        );
        if (expiredOrders.isEmpty()) return;

        for (Order order : expiredOrders) {
            order.setStatus("cancelled");
            order.setCancelReason("超时未支付自动取消");
            order.setCancelTime(LocalDateTime.now());
            orderMapper.updateById(order);

            List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, order.getId())
            );
            for (OrderItem item : items) {
                int qty = item.getQuantity();
                productMapper.update(null, new LambdaUpdateWrapper<Product>()
                    .eq(Product::getId, item.getProductId())
                    .setSql("stock = stock + " + qty + ", sales = GREATEST(0, sales - " + qty + ")"));
            }
            log.info("Auto-cancelled expired order: {}", order.getOrderNo());
        }
    }
}
