package com.drinkmall.service.impl;

import com.drinkmall.common.BusinessException;
import com.drinkmall.dto.CreateOrderRequest;
import com.drinkmall.dto.PayOrderRequest;
import com.drinkmall.entity.Address;
import com.drinkmall.entity.Order;
import com.drinkmall.entity.Product;
import com.drinkmall.entity.User;
import com.drinkmall.mapper.AddressMapper;
import com.drinkmall.mapper.PointsLogMapper;
import com.drinkmall.mapper.ProductMapper;
import com.drinkmall.mapper.UserMapper;
import com.drinkmall.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PointsServiceImplTest {

    @Mock private UserMapper userMapper;
    @Mock private ProductMapper productMapper;
    @Mock private PointsLogMapper pointsLogMapper;
    @Mock private OrderService orderService;
    @Mock private AddressMapper addressMapper;

    private PointsServiceImpl pointsService;

    @BeforeEach
    void setUp() {
        pointsService = new PointsServiceImpl(userMapper, productMapper, pointsLogMapper, orderService, addressMapper);
    }

    @Test
    void redeemProductCreatesAndPaysPointsOrderWithDefaultAddress() {
        User user = new User();
        user.setId(1L);
        user.setPoints(1000);
        when(userMapper.selectById(1L)).thenReturn(user);

        Product product = new Product();
        product.setId(104L);
        product.setStatus(1);
        product.setZoneType("gift");
        product.setPrice(new BigDecimal("688.00"));
        product.setGiftPointsPrice(688);
        when(productMapper.selectById(104L)).thenReturn(product);

        Address address = new Address();
        address.setId(10L);
        address.setUserId(1L);
        address.setIsDefault(true);
        when(addressMapper.selectOne(any())).thenReturn(address);

        Order order = new Order();
        order.setId(99L);
        when(orderService.createOrder(any(), any())).thenReturn(order);

        Long orderId = pointsService.redeemProduct(1L, 104L);

        assertThat(orderId).isEqualTo(99L);
        ArgumentCaptor<CreateOrderRequest> orderRequest = ArgumentCaptor.forClass(CreateOrderRequest.class);
        verify(orderService).createOrder(org.mockito.Mockito.eq(1L), orderRequest.capture());
        assertThat(orderRequest.getValue().getAddressId()).isEqualTo(10L);
        assertThat(orderRequest.getValue().getPaymentMethod()).isEqualTo("points");
        assertThat(orderRequest.getValue().getItems()).hasSize(1);
        assertThat(orderRequest.getValue().getItems().get(0).getProductId()).isEqualTo(104L);

        ArgumentCaptor<PayOrderRequest> payRequest = ArgumentCaptor.forClass(PayOrderRequest.class);
        verify(orderService).payOrder(org.mockito.Mockito.eq(1L), org.mockito.Mockito.eq(99L), payRequest.capture());
        assertThat(payRequest.getValue().getPaymentMethod()).isEqualTo("points");
        verify(userMapper, never()).updateById(any());
        verify(pointsLogMapper, never()).insert(any());
    }

    @Test
    void redeemProductRequiresAddressBeforeCreatingOrder() {
        User user = new User();
        user.setId(1L);
        user.setPoints(1000);
        when(userMapper.selectById(1L)).thenReturn(user);

        Product product = new Product();
        product.setId(104L);
        product.setStatus(1);
        product.setZoneType("gift");
        product.setPrice(new BigDecimal("688.00"));
        when(productMapper.selectById(104L)).thenReturn(product);
        when(addressMapper.selectOne(any())).thenReturn(null);

        assertThatThrownBy(() -> pointsService.redeemProduct(1L, 104L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("收货地址");
        verify(orderService, never()).createOrder(any(), any());
    }
}
