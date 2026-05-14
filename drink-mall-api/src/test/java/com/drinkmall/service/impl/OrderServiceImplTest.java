package com.drinkmall.service.impl;

import com.drinkmall.common.BusinessException;
import com.drinkmall.dto.CreateOrderRequest;
import com.drinkmall.dto.OrderResponse;
import com.drinkmall.dto.PayOrderRequest;
import com.drinkmall.entity.Address;
import com.drinkmall.entity.Order;
import com.drinkmall.entity.OrderItem;
import com.drinkmall.entity.Product;
import com.drinkmall.entity.SysConfig;
import com.drinkmall.entity.User;
import com.drinkmall.enums.PaymentMethod;
import com.drinkmall.enums.ProductZoneType;
import com.drinkmall.enums.UserLevel;
import com.drinkmall.mapper.AddressMapper;
import com.drinkmall.mapper.AssetLogMapper;
import com.drinkmall.mapper.BalanceLogMapper;
import com.drinkmall.mapper.CartMapper;
import com.drinkmall.mapper.OrderItemMapper;
import com.drinkmall.mapper.OrderMapper;
import com.drinkmall.mapper.PointsLogMapper;
import com.drinkmall.mapper.ProductMapper;
import com.drinkmall.mapper.SysConfigMapper;
import com.drinkmall.mapper.UserMapper;
import com.drinkmall.service.PhaseOneCoreService;
import com.drinkmall.service.LevelService;
import com.drinkmall.service.RewardService;
import com.drinkmall.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock private OrderMapper orderMapper;
    @Mock private OrderItemMapper orderItemMapper;
    @Mock private CartMapper cartMapper;
    @Mock private ProductMapper productMapper;
    @Mock private AddressMapper addressMapper;
    @Mock private UserService userService;
    @Mock private UserMapper userMapper;
    @Mock private BalanceLogMapper balanceLogMapper;
    @Mock private PhaseOneCoreService phaseOneCoreService;
    @Mock private PointsLogMapper pointsLogMapper;
    @Mock private AssetLogMapper assetLogMapper;
    @Mock private LevelService levelService;
    @Mock private RewardService rewardService;
    @Mock private SysConfigMapper sysConfigMapper;

    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl(
                orderMapper,
                orderItemMapper,
                cartMapper,
                productMapper,
                addressMapper,
                userService,
                userMapper,
                balanceLogMapper,
                phaseOneCoreService,
                pointsLogMapper,
                assetLogMapper,
                levelService,
                rewardService,
                sysConfigMapper
        );
    }

    @Test
    void investmentZoneRequiresRealNameBeforeOrderCreation() {
        mockAddress();
        Product product = product(10L, ProductZoneType.INVESTMENT, "online,offline_corporate");
        when(productMapper.selectById(10L)).thenReturn(product);
        BusinessException realNameFailure = new BusinessException(400, "购买招商专区商品前必须完成实名认证");
        org.mockito.Mockito.doThrow(realNameFailure)
                .when(phaseOneCoreService)
                .validateBeforeCreateOrder(1L, product, PaymentMethod.ONLINE);

        assertThatThrownBy(() -> orderService.createOrder(1L, request(10L, 1, "online")))
                .isSameAs(realNameFailure);

        verify(orderMapper, never()).insert(any());
    }

    @Test
    void giftZoneCannotSubmitWhenPointsInsufficient() {
        mockAddress();
        Product product = product(20L, ProductZoneType.GIFT, "points");
        product.setGiftPointsPrice(500);
        when(productMapper.selectById(20L)).thenReturn(product);
        User user = user();
        user.setPoints(499);
        when(userMapper.selectById(1L)).thenReturn(user);

        assertThatThrownBy(() -> orderService.createOrder(1L, request(20L, 1, "points")))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("积分不足");
    }

    @Test
    void giftZoneRejectsCashBalanceOrWineBeanPayment() {
        mockAddress();
        Product product = product(21L, ProductZoneType.GIFT, "points");
        when(productMapper.selectById(21L)).thenReturn(product);

        assertThatThrownBy(() -> orderService.createOrder(1L, request(21L, 1, "balance")))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("纯积分");
    }

    @Test
    void retailZonePaymentDoesNotIncreaseMainUpgradePerformance() {
        Order order = pendingOrder("retail", "balance");
        when(orderMapper.selectOne(any())).thenReturn(order);
        User user = user();
        user.setBalance(new BigDecimal("200.00"));
        when(userMapper.selectById(1L)).thenReturn(user);
        when(orderItemMapper.selectList(any())).thenReturn(List.of(orderItem(30L)));

        orderService.payOrder(1L, 100L, payRequest("balance"));

        verify(levelService, never()).recordMainProductPaid(any(), any(), any());
        verify(rewardService).settleOrderRewards(order);
    }

    @Test
    void disabledProductCannotBePurchased() {
        mockAddress();
        Product product = product(40L, ProductZoneType.MAIN, "online");
        product.setStatus(0);
        when(productMapper.selectById(40L)).thenReturn(product);

        assertThatThrownBy(() -> orderService.createOrder(1L, request(40L, 1, "online")))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("已下架");
    }

    @Test
    void repeatedPaymentDoesNotDeductAssetsAgain() {
        Order paid = pendingOrder("main", "balance");
        paid.setStatus("paid");
        when(orderMapper.selectOne(any())).thenReturn(paid);

        orderService.payOrder(1L, 100L, payRequest("balance"));

        verify(userMapper, never()).updateById(any());
        verify(balanceLogMapper, never()).insert(any());
    }

    @Test
    void promoterMainProductRepurchaseUsesConfiguredDiscount() {
        mockAddress();
        Product product = product(45L, ProductZoneType.MAIN, "online");
        when(productMapper.selectById(45L)).thenReturn(product);
        when(productMapper.update(any(), any())).thenReturn(1);
        when(orderMapper.selectCount(any())).thenReturn(1L);
        User user = user();
        user.setDistributionLevel(UserLevel.PROMOTER.getCode());
        when(userMapper.selectById(1L)).thenReturn(user);
        SysConfig discount = new SysConfig();
        discount.setConfigValue("0.80");
        when(sysConfigMapper.selectOne(any())).thenReturn(discount);

        Order order = orderService.createOrder(1L, request(45L, 1, "online"));

        ArgumentCaptor<OrderItem> item = ArgumentCaptor.forClass(OrderItem.class);
        verify(orderItemMapper).insert(item.capture());
        assertThat(item.getValue().getPrice()).isEqualByComparingTo("80.00");
        assertThat(order.getPayAmount()).isEqualByComparingTo("80.00");
    }

    @Test
    void orderDetailContainsLogisticsAfterAdminShipment() {
        Order shipped = pendingOrder("main", "online");
        shipped.setStatus("shipped");
        shipped.setLogisticsCompany("顺丰速运");
        shipped.setLogisticsNo("SF123456");
        when(orderMapper.selectOne(any())).thenReturn(shipped);
        when(orderItemMapper.selectList(any())).thenReturn(List.of(orderItem(50L)));

        OrderResponse response = orderService.getOrderDetail(1L, 100L);

        assertThat(response.getLogisticsCompany()).isEqualTo("顺丰速运");
        assertThat(response.getLogisticsNo()).isEqualTo("SF123456");
    }

    @Test
    void pendingOrderCanBeCancelledAndRollsBackRewards() {
        Order pending = pendingOrder("main", "online");
        when(orderMapper.selectOne(any())).thenReturn(pending);
        when(orderItemMapper.selectList(any())).thenReturn(List.of(orderItem(50L)));

        orderService.cancelOrder(1L, 100L, "changed mind");

        assertThat(pending.getStatus()).isEqualTo("cancelled");
        assertThat(pending.getCancelReason()).isEqualTo("changed mind");
        assertThat(pending.getCancelTime()).isNotNull();
        verify(orderMapper).updateById(pending);
        verify(productMapper).update(any(), any());
        verify(rewardService).rollbackOrderRewards(100L, "user_cancel_order");
    }

    @Test
    void shippedOrderCanBeConfirmedAndTriggersRewardUnfreezeCheck() {
        Order shipped = pendingOrder("main", "online");
        shipped.setStatus("shipped");
        when(orderMapper.selectOne(any())).thenReturn(shipped);

        orderService.confirmReceipt(1L, 100L);

        assertThat(shipped.getStatus()).isEqualTo("completed");
        assertThat(shipped.getCompleteTime()).isNotNull();
        verify(orderMapper).updateById(shipped);
        verify(rewardService).unfreezeDueRewards(any(LocalDateTime.class));
    }

    private void mockAddress() {
        Address address = new Address();
        address.setId(2L);
        address.setUserId(1L);
        when(addressMapper.selectById(2L)).thenReturn(address);
    }

    private CreateOrderRequest request(Long productId, int quantity, String paymentMethod) {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setAddressId(2L);
        request.setPaymentMethod(paymentMethod);
        CreateOrderRequest.CartItemInfo item = new CreateOrderRequest.CartItemInfo();
        item.setProductId(productId);
        item.setQuantity(quantity);
        request.setItems(List.of(item));
        return request;
    }

    private PayOrderRequest payRequest(String paymentMethod) {
        PayOrderRequest request = new PayOrderRequest();
        request.setPaymentMethod(paymentMethod);
        return request;
    }

    private Product product(Long id, ProductZoneType zoneType, String paymentMethods) {
        Product product = new Product();
        product.setId(id);
        product.setName("测试商品");
        product.setMainImage("https://example.com/p.png");
        product.setPrice(new BigDecimal("100.00"));
        product.setStock(10);
        product.setStatus(1);
        product.setZoneType(zoneType.getCode());
        product.setAllowedPaymentMethods(paymentMethods);
        product.setGiftPoints(0);
        return product;
    }

    private User user() {
        User user = new User();
        user.setId(1L);
        user.setBalance(new BigDecimal("0.00"));
        user.setWineBeanBalance(new BigDecimal("0.00"));
        user.setPoints(0);
        return user;
    }

    private Order pendingOrder(String zoneType, String paymentMethod) {
        Order order = new Order();
        order.setId(100L);
        order.setOrderNo("DM100");
        order.setUserId(1L);
        order.setAddressId(2L);
        order.setTotalAmount(new BigDecimal("100.00"));
        order.setPayAmount(new BigDecimal("100.00"));
        order.setStatus("pending");
        order.setPaymentMethod(paymentMethod);
        order.setZoneType(zoneType);
        order.setCreatedAt(LocalDateTime.now());
        return order;
    }

    private OrderItem orderItem(Long productId) {
        OrderItem item = new OrderItem();
        item.setId(1L);
        item.setOrderId(100L);
        item.setProductId(productId);
        item.setProductName("测试商品");
        item.setProductImage("https://example.com/p.png");
        item.setPrice(new BigDecimal("100.00"));
        item.setQuantity(1);
        item.setTotalAmount(new BigDecimal("100.00"));
        return item;
    }
}
