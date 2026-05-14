package com.drinkmall.service.impl;

import com.drinkmall.dto.DistributionLevelOverviewResponse;
import com.drinkmall.dto.MemberCenterResponse;
import com.drinkmall.entity.User;
import com.drinkmall.mapper.BalanceLogMapper;
import com.drinkmall.mapper.OrderMapper;
import com.drinkmall.mapper.PointsLogMapper;
import com.drinkmall.mapper.SysConfigMapper;
import com.drinkmall.mapper.UserMapper;
import com.drinkmall.mapper.WithdrawalMapper;
import com.drinkmall.service.WithdrawalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;
    @Mock
    private BalanceLogMapper balanceLogMapper;
    @Mock
    private PointsLogMapper pointsLogMapper;
    @Mock
    private WithdrawalMapper withdrawalMapper;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private SysConfigMapper sysConfigMapper;
    @Mock
    private WithdrawalService withdrawalService;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(
                userMapper,
                balanceLogMapper,
                pointsLogMapper,
                withdrawalMapper,
                orderMapper,
                sysConfigMapper,
                withdrawalService
        );
    }

    @Test
    void getMemberCenterBuildsDesignSummaryFromUserAndOrderCounts() {
        when(userMapper.selectById(10001L)).thenReturn(demoUser());
        when(orderMapper.selectCount(any()))
                .thenReturn(1L)
                .thenReturn(2L)
                .thenReturn(3L)
                .thenReturn(4L)
                .thenReturn(5L);

        MemberCenterResponse response = userService.getMemberCenter(10001L);

        assertThat(response.getProfile().getNickname()).isEqualTo("李明远");
        assertThat(response.getProfile().getMaskedPhone()).isEqualTo("138****8888");
        assertThat(response.getProfile().getMemberLevelName()).isEqualTo("县级联营商");
        assertThat(response.getSummary().getWithdrawableBalance()).isEqualByComparingTo("24860.00");
        assertThat(response.getSummary().getTeamPerformance()).isEqualByComparingTo("58400.00");
        assertThat(response.getSummary().getPoints()).isEqualTo(1280);
        assertThat(response.getAssets().getFrozenBalance()).isEqualByComparingTo("3200.00");
        assertThat(response.getAssets().getDfBalance()).isEqualByComparingTo("1500.00");
        assertThat(response.getOrderSummary().getPendingPayment()).isEqualTo(1L);
        assertThat(response.getOrderSummary().getPendingShipment()).isEqualTo(2L);
        assertThat(response.getOrderSummary().getPendingReceipt()).isEqualTo(3L);
        assertThat(response.getOrderSummary().getCompleted()).isEqualTo(4L);
        assertThat(response.getOrderSummary().getAfterSale()).isEqualTo(5L);
    }

    @Test
    void getDistributionLevelOverviewReturnsCurrentProgressAndUpgradeDifference() {
        when(userMapper.selectById(10001L)).thenReturn(demoUser());

        DistributionLevelOverviewResponse response = userService.getDistributionLevelOverview(10001L);

        assertThat(response.getCurrentLevel().getCode()).isEqualTo("county");
        assertThat(response.getPerformanceAmount()).isEqualByComparingTo("58400.00");
        assertThat(response.getNextTargetAmount()).isEqualByComparingTo("150000.00");
        assertThat(response.getUpgradeDifference()).isEqualByComparingTo("91600.00");
        assertThat(response.getProgressPercent()).isEqualByComparingTo("38.93");
        assertThat(response.getLevels()).hasSize(2);
        assertThat(response.getLevels().get(0).getAchieved()).isTrue();
        assertThat(response.getLevels().get(0).getBenefits()).contains("直推首购 20% 佣金");
        assertThat(response.getLevels().get(1).getAchieved()).isFalse();
    }

    private User demoUser() {
        User user = new User();
        user.setId(10001L);
        user.setNickname("李明远");
        user.setPhone("13800138888");
        user.setAvatarUrl("https://example.com/avatar.png");
        user.setBalance(new BigDecimal("24860.00"));
        user.setFrozenBalance(new BigDecimal("3200.00"));
        user.setPoints(1280);
        user.setDistributionLevel("county");
        user.setTeamPerformance(new BigDecimal("58400.00"));
        user.setDfBalance(new BigDecimal("1500.00"));
        return user;
    }
}
