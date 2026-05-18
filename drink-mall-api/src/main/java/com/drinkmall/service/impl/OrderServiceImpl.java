package com.drinkmall.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drinkmall.common.BusinessException;
import com.drinkmall.dto.*;
import com.drinkmall.entity.*;
import com.drinkmall.enums.AssetType;
import com.drinkmall.enums.OrderStatus;
import com.drinkmall.enums.PaymentMethod;
import com.drinkmall.enums.ProductZoneType;
import com.drinkmall.enums.UserLevel;
import com.drinkmall.mapper.*;
import com.drinkmall.service.OrderService;
import com.drinkmall.service.PhaseOneCoreService;
import com.drinkmall.service.LevelService;
import com.drinkmall.service.RewardService;
import com.drinkmall.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
    private final UserMapper userMapper;
    private final BalanceLogMapper balanceLogMapper;
    private final PhaseOneCoreService phaseOneCoreService;
    private final PointsLogMapper pointsLogMapper;
    private final AssetLogMapper assetLogMapper;
    private final LevelService levelService;
    private final RewardService rewardService;
    private final SysConfigMapper sysConfigMapper;

    @Override
    public CheckoutPreviewResponse previewOrder(Long userId, CreateOrderRequest request) {
        CheckoutDraft draft = buildDraft(userId, request, resolvePaymentMethod(request.getPaymentMethod()));
        return CheckoutPreviewResponse.builder()
                .zoneType(draft.zoneType)
                .paymentMethod(draft.paymentMethod.getCode())
                .totalAmount(draft.totalAmount)
                .payAmount(draft.payAmount)
                .pointsAmount(draft.pointsAmount)
                .giftPoints(draft.giftPoints)
                .items(draft.items.stream().map(this::toItemResponse).collect(Collectors.toList()))
                .build();
    }

    @Override
    @Transactional
    public Order createOrder(Long userId, CreateOrderRequest request) {
        User currentUser = userService.getById(userId);
        if (currentUser != null && !Boolean.TRUE.equals(currentUser.getAgeVerified())) {
            throw new BusinessException(400, "请先完成年龄验证");
        }
        PaymentMethod paymentMethod = resolvePaymentMethod(request.getPaymentMethod());
        Address address = addressMapper.selectById(request.getAddressId());
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException(400, "请选择收货地址");
        }

        CheckoutDraft draft = buildDraft(userId, request, paymentMethod);
        if (paymentMethod == PaymentMethod.POINTS || paymentMethod == PaymentMethod.WINE_BEAN) {
            validateAssetEnough(userId, paymentMethod, draft.payAmount, draft.pointsAmount);
        }

        Order order = new Order();
        order.setOrderNo("DM" + IdUtil.getSnowflakeNextIdStr());
        order.setUserId(userId);
        order.setAddressId(address.getId());
        order.setTotalAmount(draft.totalAmount);
        order.setPayAmount(draft.payAmount);
        order.setPointsAmount(draft.pointsAmount);
        order.setZoneType(draft.zoneType);
        order.setStatus(OrderStatus.PENDING.getCode());
        order.setPaymentMethod(paymentMethod.getCode());
        order.setCreatedAt(LocalDateTime.now());
        orderMapper.insert(order);

        for (OrderItem item : draft.items) {
            item.setOrderId(order.getId());
            orderItemMapper.insert(item);
            decreaseStock(item);
        }
        deletePurchasedCartItems(userId, request);
        return order;
    }

    @Override
    public IPage<OrderResponse> getOrders(Long userId, String status, Integer page, Integer size) {
        Page<Order> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>().eq(Order::getUserId, userId);
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Order::getStatus, status);
        }
        wrapper.orderByDesc(Order::getCreatedAt);
        return orderMapper.selectPage(pageParam, wrapper).convert(this::convertToResponse);
    }

    @Override
    public OrderResponse getOrderDetail(Long userId, Long orderId) {
        Order order = orderMapper.selectOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getId, orderId)
                .eq(Order::getUserId, userId));
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        return convertToResponse(order);
    }

    @Override
    @Transactional
    public void cancelOrder(Long userId, Long orderId, String reason) {
        Order order = orderMapper.selectOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getId, orderId)
                .eq(Order::getUserId, userId));
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        if (!OrderStatus.PENDING.getCode().equals(order.getStatus())) {
            throw new BusinessException(400, "只能取消待付款订单");
        }
        order.setStatus(OrderStatus.CANCELLED.getCode());
        order.setCancelReason(reason);
        order.setCancelTime(LocalDateTime.now());
        orderMapper.updateById(order);
        restoreStock(orderId);
        rewardService.rollbackOrderRewards(orderId, "user_cancel_order");
    }

    @Override
    @Transactional
    public void confirmReceipt(Long userId, Long orderId) {
        Order order = orderMapper.selectOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getId, orderId)
                .eq(Order::getUserId, userId));
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        if (!OrderStatus.SHIPPED.getCode().equals(order.getStatus())) {
            throw new BusinessException(400, "只能确认已发货订单");
        }
        order.setStatus(OrderStatus.COMPLETED.getCode());
        order.setCompleteTime(LocalDateTime.now());
        orderMapper.updateById(order);
        rewardService.unfreezeDueRewards(LocalDateTime.now());
    }

    @Override
    @Transactional
    public void payOrder(Long userId, Long orderId) {
        PayOrderRequest request = new PayOrderRequest();
        request.setPaymentMethod(PaymentMethod.ONLINE.getCode());
        payOrder(userId, orderId, request);
    }

    @Override
    @Transactional
    public PayResponse payOrder(Long userId, Long orderId, PayOrderRequest request) {
        Order order = orderMapper.selectOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getId, orderId)
                .eq(Order::getUserId, userId));
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        if (!OrderStatus.PENDING.getCode().equals(order.getStatus())) {
            return PayResponse.builder()
                    .orderNo(order.getOrderNo())
                    .prepayId(order.getPaymentNo())
                    .build();
        }

        PaymentMethod paymentMethod = resolvePaymentMethod(request.getPaymentMethod());
        ensureOrderPaymentMethod(order, paymentMethod);
        switch (paymentMethod) {
            case ONLINE -> markOrderPaid(order, "ONL" + IdUtil.getSnowflakeNextIdStr(), paymentMethod);
            case BALANCE -> deductBalanceAndPay(userId, order);
            case WINE_BEAN -> deductWineBeanAndPay(userId, order);
            case POINTS -> deductPointsAndPay(userId, order);
            case OFFLINE_CORPORATE -> {
                order.setOfflineTransferNo(request.getPaymentNo());
                orderMapper.updateById(order);
            }
        }

        return PayResponse.builder()
                .orderNo(order.getOrderNo())
                .prepayId(order.getPaymentNo())
                .build();
    }

    @Override
    @Transactional
    public PayResponse payOrderByBalance(Long userId, Long orderId) {
        PayOrderRequest request = new PayOrderRequest();
        request.setPaymentMethod(PaymentMethod.BALANCE.getCode());
        return payOrder(userId, orderId, request);
    }

    @Override
    public PayResponse getPayParams(Long userId, Long orderId) {
        Order order = orderMapper.selectOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getId, orderId)
                .eq(Order::getUserId, userId));
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        return PayResponse.builder().orderNo(order.getOrderNo()).prepayId(order.getPaymentNo()).build();
    }

    @Override
    @Transactional
    public void confirmOnlinePaymentCallback(String orderNo, BigDecimal paidAmount, String paymentNo) {
        if (orderNo == null || orderNo.isBlank()) {
            throw new BusinessException(400, "订单号不能为空");
        }
        if (paidAmount == null) {
            throw new BusinessException(400, "支付金额不能为空");
        }
        if (paymentNo == null || paymentNo.isBlank()) {
            throw new BusinessException(400, "微信支付流水号不能为空");
        }

        Order order = orderMapper.selectOne(new LambdaQueryWrapper<Order>().eq(Order::getOrderNo, orderNo));
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        if (order.getPayAmount() == null || order.getPayAmount().compareTo(paidAmount) != 0) {
            throw new BusinessException(400, "支付金额不一致");
        }
        if (order.getPaymentMethod() != null && !PaymentMethod.ONLINE.getCode().equals(order.getPaymentMethod())) {
            throw new BusinessException(400, "订单支付方式不是微信在线支付");
        }
        if (!OrderStatus.PENDING.getCode().equals(order.getStatus())) {
            return;
        }

        LocalDateTime paidAt = LocalDateTime.now();
        int updated = orderMapper.update(null, new UpdateWrapper<Order>()
                .eq("id", order.getId())
                .eq("status", OrderStatus.PENDING.getCode())
                .set("status", OrderStatus.PAID.getCode())
                .set("payment_method", PaymentMethod.ONLINE.getCode())
                .set("payment_no", paymentNo)
                .set("payment_time", paidAt));
        if (updated == 0) {
            return;
        }

        order.setStatus(OrderStatus.PAID.getCode());
        order.setPaymentMethod(PaymentMethod.ONLINE.getCode());
        order.setPaymentNo(paymentNo);
        order.setPaymentTime(paidAt);
        afterOrderPaid(order);
    }

    private CheckoutDraft buildDraft(Long userId, CreateOrderRequest request, PaymentMethod paymentMethod) {
        List<CreateOrderRequest.CartItemInfo> requestItems = resolveRequestItems(userId, request);
        if (requestItems.isEmpty()) {
            throw new BusinessException(400, "请选择结算商品");
        }

        CheckoutDraft draft = new CheckoutDraft(paymentMethod);
        for (CreateOrderRequest.CartItemInfo itemInfo : requestItems) {
            if (itemInfo.getQuantity() == null || itemInfo.getQuantity() < 1) {
                throw new BusinessException(400, "商品数量必须大于0");
            }
            Product product = productMapper.selectById(itemInfo.getProductId());
            validateProductForOrder(userId, product, itemInfo.getQuantity(), paymentMethod);
            if (draft.zoneType == null) {
                draft.zoneType = product.getZoneType();
            } else if (!draft.zoneType.equals(product.getZoneType())) {
                throw new BusinessException(400, "一次订单只能结算同一专区商品");
            }

            BigDecimal unitPrice = applyMainRepurchaseDiscount(userId, product);
            BigDecimal itemTotal = unitPrice.multiply(BigDecimal.valueOf(itemInfo.getQuantity()));
            int itemPoints = ProductZoneType.GIFT.getCode().equals(product.getZoneType())
                    ? pointsPrice(product) * itemInfo.getQuantity()
                    : 0;
            OrderItem item = new OrderItem();
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setProductImage(product.getMainImage());
            item.setPrice(unitPrice);
            item.setQuantity(itemInfo.getQuantity());
            item.setTotalAmount(itemTotal);
            item.setZoneType(product.getZoneType());
            item.setGiftPoints(value(product.getGiftPoints()) * itemInfo.getQuantity());
            item.setPointsAmount(itemPoints);
            draft.items.add(item);
            draft.totalAmount = draft.totalAmount.add(itemTotal);
            draft.pointsAmount += itemPoints;
            draft.giftPoints += value(product.getGiftPoints()) * itemInfo.getQuantity();
        }
        draft.payAmount = paymentMethod == PaymentMethod.POINTS ? BigDecimal.ZERO : draft.totalAmount;
        return draft;
    }

    private List<CreateOrderRequest.CartItemInfo> resolveRequestItems(Long userId, CreateOrderRequest request) {
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            return request.getItems();
        }
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId)
                .eq(Cart::getSelected, true);
        if (request.getCartIds() != null && !request.getCartIds().isEmpty()) {
            wrapper.in(Cart::getId, request.getCartIds());
        }
        return cartMapper.selectList(wrapper).stream().map(cart -> {
            CreateOrderRequest.CartItemInfo item = new CreateOrderRequest.CartItemInfo();
            item.setProductId(cart.getProductId());
            item.setQuantity(cart.getQuantity());
            return item;
        }).collect(Collectors.toList());
    }

    private void validateProductForOrder(Long userId, Product product, Integer quantity, PaymentMethod paymentMethod) {
        if (product == null) {
            throw new BusinessException(404, "商品不存在");
        }
        if (!Objects.equals(product.getStatus(), 1)) {
            throw new BusinessException(400, "商品已下架，不能购买");
        }
        if (product.getStock() == null || product.getStock() < quantity) {
            throw new BusinessException(400, "库存不足: " + product.getName());
        }
        if (ProductZoneType.GIFT.getCode().equals(product.getZoneType()) && paymentMethod != PaymentMethod.POINTS) {
            throw new BusinessException(400, "礼包专区只允许纯积分支付");
        }
        String allowed = product.getAllowedPaymentMethods();
        if (allowed != null && !allowed.isBlank() && !("," + allowed + ",").contains("," + paymentMethod.getCode() + ",")) {
            throw new BusinessException(400, "商品不支持当前支付方式");
        }
        phaseOneCoreService.validateBeforeCreateOrder(userId, product, paymentMethod);
        if (paymentMethod == PaymentMethod.WINE_BEAN && !Boolean.TRUE.equals(product.getWineBeanPayable())) {
            throw new BusinessException(400, "商品未开启酒豆支付");
        }
    }

    private void validateAssetEnough(Long userId, PaymentMethod paymentMethod, BigDecimal payAmount, Integer pointsAmount) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        if (paymentMethod == PaymentMethod.POINTS && value(user.getPoints()) < value(pointsAmount)) {
            throw new BusinessException(400, "积分不足");
        }
        if (paymentMethod == PaymentMethod.WINE_BEAN
                && value(user.getWineBeanBalance()).compareTo(payAmount) < 0) {
            throw new BusinessException(400, "酒豆不足");
        }
    }

    private void decreaseStock(OrderItem item) {
        int updated = productMapper.update(null, new LambdaUpdateWrapper<Product>()
                .eq(Product::getId, item.getProductId())
                .ge(Product::getStock, item.getQuantity())
                .setSql("stock = stock - " + item.getQuantity() + ", sales = sales + " + item.getQuantity()));
        if (updated == 0) {
            throw new BusinessException(400, "库存不足: " + item.getProductName());
        }
    }

    private void deletePurchasedCartItems(Long userId, CreateOrderRequest request) {
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            return;
        }
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId)
                .eq(Cart::getSelected, true);
        if (request.getCartIds() != null && !request.getCartIds().isEmpty()) {
            wrapper.in(Cart::getId, request.getCartIds());
        }
        cartMapper.delete(wrapper);
    }

    private void deductBalanceAndPay(Long userId, Order order) {
        User user = userMapper.selectById(userId);
        BigDecimal before = value(user.getBalance());
        if (before.compareTo(order.getPayAmount()) < 0) {
            throw new BusinessException(400, "余额不足");
        }
        BigDecimal after = before.subtract(order.getPayAmount());
        user.setBalance(after);
        userMapper.updateById(user);
        BalanceLog log = new BalanceLog();
        log.setUserId(userId);
        log.setChangeType("payment");
        log.setAmount(order.getPayAmount().negate());
        log.setBeforeBalance(before);
        log.setAfterBalance(after);
        log.setOrderId(order.getId());
        log.setRemark("余额支付订单 " + order.getOrderNo());
        log.setCreatedAt(LocalDateTime.now());
        balanceLogMapper.insert(log);
        markOrderPaid(order, "BAL" + IdUtil.getSnowflakeNextIdStr(), PaymentMethod.BALANCE);
    }

    private void deductWineBeanAndPay(Long userId, Order order) {
        User user = userMapper.selectById(userId);
        BigDecimal before = value(user.getWineBeanBalance());
        if (before.compareTo(order.getPayAmount()) < 0) {
            throw new BusinessException(400, "酒豆不足");
        }
        BigDecimal after = before.subtract(order.getPayAmount());
        user.setWineBeanBalance(after);
        userMapper.updateById(user);
        insertAssetLog(userId, AssetType.WINE_BEAN.getCode(), order.getPayAmount().negate(), before, after, order, "酒豆支付订单");
        markOrderPaid(order, "WB" + IdUtil.getSnowflakeNextIdStr(), PaymentMethod.WINE_BEAN);
    }

    private void deductPointsAndPay(Long userId, Order order) {
        User user = userMapper.selectById(userId);
        int before = value(user.getPoints());
        int amount = value(order.getPointsAmount());
        if (before < amount) {
            throw new BusinessException(400, "积分不足");
        }
        user.setPoints(before - amount);
        userMapper.updateById(user);
        PointsLog log = new PointsLog();
        log.setUserId(userId);
        log.setChangeType("redeem");
        log.setPoints(-amount);
        log.setBeforePoints(before);
        log.setAfterPoints(user.getPoints());
        log.setOrderId(order.getId());
        log.setRemark("积分兑换订单 " + order.getOrderNo());
        pointsLogMapper.insert(log);
        markOrderPaid(order, "PTS" + IdUtil.getSnowflakeNextIdStr(), PaymentMethod.POINTS);
    }

    private void markOrderPaid(Order order, String paymentNo, PaymentMethod paymentMethod) {
        order.setStatus(OrderStatus.PAID.getCode());
        order.setPaymentMethod(paymentMethod.getCode());
        order.setPaymentNo(paymentNo);
        order.setPaymentTime(LocalDateTime.now());
        orderMapper.updateById(order);
        afterOrderPaid(order);
    }

    private void afterOrderPaid(Order order) {
        String zoneType = order.getZoneType();
        if (zoneType == null || zoneType.isBlank()) {
            zoneType = orderPrimaryZone(order.getId());
        }
        if (ProductZoneType.MAIN.getCode().equals(zoneType)) {
            levelService.recordMainProductPaid(order.getUserId(), order.getPayAmount(), order.getOrderNo());
        }
        if (ProductZoneType.INVESTMENT.getCode().equals(zoneType)) {
            levelService.upgradeByInvestmentOrder(order);
        }
        rewardService.settleOrderRewards(order);
        grantGiftPoints(order);
    }

    private void grantGiftPoints(Order order) {
        List<OrderItem> items = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, order.getId()));
        int points = items.stream().mapToInt(i -> value(i.getGiftPoints())).sum();
        if (points <= 0) {
            return;
        }
        User user = userMapper.selectById(order.getUserId());
        int before = value(user.getPoints());
        user.setPoints(before + points);
        userMapper.updateById(user);
        PointsLog log = new PointsLog();
        log.setUserId(user.getId());
        log.setChangeType("purchase");
        log.setPoints(points);
        log.setBeforePoints(before);
        log.setAfterPoints(user.getPoints());
        log.setOrderId(order.getId());
        log.setRemark("订单 " + order.getOrderNo() + " 赠送积分");
        pointsLogMapper.insert(log);
    }

    private void restoreStock(Long orderId) {
        List<OrderItem> items = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId));
        for (OrderItem item : items) {
            productMapper.update(null, new LambdaUpdateWrapper<Product>()
                    .eq(Product::getId, item.getProductId())
                    .setSql("stock = stock + " + item.getQuantity() + ", sales = GREATEST(0, sales - " + item.getQuantity() + ")"));
        }
    }

    private OrderResponse convertToResponse(Order order) {
        Address address = addressMapper.selectById(order.getAddressId());
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
                .address(address != null ? AddressResponse.fromEntity(address) : null)
                .items(items.stream().map(this::toItemResponse).collect(Collectors.toList()))
                .createdAt(order.getCreatedAt())
                .paymentTime(order.getPaymentTime())
                .shipTime(order.getShipTime())
                .logisticsCompany(order.getLogisticsCompany())
                .logisticsNo(order.getLogisticsNo())
                .build();
    }

    private OrderItemResponse toItemResponse(OrderItem i) {
        return OrderItemResponse.builder()
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
                .build();
    }

    private String orderPrimaryZone(Long orderId) {
        List<OrderItem> items = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId));
        if (items.isEmpty()) {
            return "";
        }
        if (items.get(0).getZoneType() != null) {
            return items.get(0).getZoneType();
        }
        Product product = productMapper.selectById(items.get(0).getProductId());
        return product == null ? "" : product.getZoneType();
    }

    private void ensureOrderPaymentMethod(Order order, PaymentMethod paymentMethod) {
        if (order.getPaymentMethod() != null && !order.getPaymentMethod().equals(paymentMethod.getCode())) {
            throw new BusinessException(400, "订单支付方式不匹配，不支持组合支付");
        }
    }

    private BigDecimal applyMainRepurchaseDiscount(Long userId, Product product) {
        if (!ProductZoneType.MAIN.getCode().equals(product.getZoneType()) || !hasPreviousMainOrder(userId)) {
            return product.getPrice();
        }
        User user = userMapper.selectById(userId);
        if (user == null || user.getDistributionLevel() == null || UserLevel.NORMAL.getCode().equals(user.getDistributionLevel())) {
            return product.getPrice();
        }
        SysConfig config = sysConfigMapper.selectOne(new LambdaQueryWrapper<SysConfig>()
                .eq(SysConfig::getConfigKey, "discount.main.repurchase." + user.getDistributionLevel()));
        if (config == null || config.getConfigValue() == null || config.getConfigValue().isBlank()
                || config.getConfigValue().contains("待")) {
            throw new BusinessException(500, "系统配置缺失: discount.main.repurchase." + user.getDistributionLevel());
        }
        return product.getPrice().multiply(new BigDecimal(config.getConfigValue())).setScale(2, java.math.RoundingMode.HALF_UP);
    }

    private boolean hasPreviousMainOrder(Long userId) {
        Long count = orderMapper.selectCount(new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, userId)
                .eq(Order::getZoneType, ProductZoneType.MAIN.getCode())
                .in(Order::getStatus, OrderStatus.PAID.getCode(), OrderStatus.SHIPPED.getCode(), OrderStatus.COMPLETED.getCode()));
        return count != null && count > 0;
    }

    private PaymentMethod resolvePaymentMethod(String paymentMethod) {
        return paymentMethod == null || paymentMethod.isBlank()
                ? PaymentMethod.ONLINE
                : PaymentMethod.fromCodeOrDefault(paymentMethod);
    }

    private int pointsPrice(Product product) {
        if (product.getGiftPointsPrice() != null) {
            return product.getGiftPointsPrice();
        }
        return product.getPrice() == null ? 0 : product.getPrice().intValue();
    }

    private int value(Integer value) {
        return value == null ? 0 : value;
    }

    private BigDecimal value(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private void insertAssetLog(Long userId, String assetType, BigDecimal change, BigDecimal before, BigDecimal after, Order order, String remark) {
        AssetLog log = new AssetLog();
        log.setUserId(userId);
        log.setAssetType(assetType);
        log.setChangeType("payment");
        log.setChangeAmount(change);
        log.setBeforeAmount(before);
        log.setAfterAmount(after);
        log.setBusinessId(order.getId());
        log.setBusinessType("order");
        log.setIdempotencyKey(order.getOrderNo() + ":" + assetType + ":payment");
        log.setRemark(remark + " " + order.getOrderNo());
        log.setCreatedAt(LocalDateTime.now());
        assetLogMapper.insert(log);
    }

    private static class CheckoutDraft {
        private final PaymentMethod paymentMethod;
        private final List<OrderItem> items = new ArrayList<>();
        private String zoneType;
        private BigDecimal totalAmount = BigDecimal.ZERO;
        private BigDecimal payAmount = BigDecimal.ZERO;
        private Integer pointsAmount = 0;
        private Integer giftPoints = 0;

        private CheckoutDraft(PaymentMethod paymentMethod) {
            this.paymentMethod = paymentMethod;
        }
    }
}
