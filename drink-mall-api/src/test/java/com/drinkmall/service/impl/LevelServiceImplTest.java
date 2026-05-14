package com.drinkmall.service.impl;

import com.drinkmall.entity.OperationLog;
import com.drinkmall.entity.SysConfig;
import com.drinkmall.entity.User;
import com.drinkmall.enums.UserLevel;
import com.drinkmall.mapper.OperationLogMapper;
import com.drinkmall.mapper.OrderItemMapper;
import com.drinkmall.mapper.ProductMapper;
import com.drinkmall.mapper.SysConfigMapper;
import com.drinkmall.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class LevelServiceImplTest {

    @Mock private UserMapper userMapper;
    @Mock private SysConfigMapper sysConfigMapper;
    @Mock private OrderItemMapper orderItemMapper;
    @Mock private ProductMapper productMapper;
    @Mock private OperationLogMapper operationLogMapper;

    private LevelServiceImpl levelService;

    @BeforeEach
    void setUp() {
        levelService = new LevelServiceImpl(userMapper, sysConfigMapper, orderItemMapper, productMapper, operationLogMapper);
    }

    @Test
    void recordMainProductPaidKeepsNormalWhenConfiguredThresholdIsNotReached() {
        User user = user(UserLevel.NORMAL.getCode(), "0.00");
        when(userMapper.selectById(1L)).thenReturn(user);
        when(sysConfigMapper.selectOne(any())).thenReturn(config("500.00"));

        levelService.recordMainProductPaid(1L, new BigDecimal("499.99"), "ORD-1");

        assertThat(user.getDistributionLevel()).isEqualTo(UserLevel.NORMAL.getCode());
        verify(operationLogMapper, never()).insert(any(OperationLog.class));
    }

    @Test
    void recordMainProductPaidUsesAdminConfiguredPromoterThreshold() {
        User user = user(UserLevel.NORMAL.getCode(), "499.99");
        when(userMapper.selectById(1L)).thenReturn(user);
        when(sysConfigMapper.selectOne(any()))
                .thenReturn(config("500.00"))
                .thenReturn(config("0"))
                .thenReturn(config("1"));

        levelService.recordMainProductPaid(1L, new BigDecimal("0.01"), "ORD-2");

        assertThat(user.getDistributionLevel()).isEqualTo(UserLevel.PROMOTER.getCode());
        verify(operationLogMapper).insert(any(OperationLog.class));
    }

    @Test
    void recordMainProductPaidRejectsPendingThresholdInsteadOfParsingAsAmount() {
        User user = user(UserLevel.NORMAL.getCode(), "0.00");
        when(userMapper.selectById(1L)).thenReturn(user);
        when(sysConfigMapper.selectOne(any())).thenReturn(config("待业务确认"));

        assertThatThrownBy(() -> levelService.recordMainProductPaid(1L, new BigDecimal("100.00"), "ORD-PENDING"))
                .isInstanceOf(com.drinkmall.common.BusinessException.class)
                .hasMessageContaining("level.promoter.main_paid_threshold")
                .hasMessageContaining("待业务确认");
    }

    private User user(String level, String mainPaidAmount) {
        User user = new User();
        user.setId(1L);
        user.setDistributionLevel(level);
        user.setMainZonePaidAmount(new BigDecimal(mainPaidAmount));
        return user;
    }

    private SysConfig config(String value) {
        SysConfig config = new SysConfig();
        config.setConfigValue(value);
        return config;
    }
}
