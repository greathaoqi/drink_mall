package com.drinkmall.service.impl;

import com.drinkmall.common.BusinessException;
import com.drinkmall.dto.DistributionLevelOverviewResponse;
import com.drinkmall.dto.MemberCenterResponse;
import com.drinkmall.entity.SysConfig;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserMapper userMapper;
    @Mock private BalanceLogMapper balanceLogMapper;
    @Mock private PointsLogMapper pointsLogMapper;
    @Mock private WithdrawalMapper withdrawalMapper;
    @Mock private OrderMapper orderMapper;
    @Mock private SysConfigMapper sysConfigMapper;
    @Mock private WithdrawalService withdrawalService;

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
    void getMemberCenterBuildsDesignSummaryFromConfiguredLevelNameAndOrderCounts() {
        when(userMapper.selectById(10001L)).thenReturn(demoUser());
        when(sysConfigMapper.selectOne(any()))
                .thenReturn(config("县级联营商"))
                .thenReturn(config("false"));
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
    void getDistributionLevelOverviewUsesAdminConfiguredLevelThresholdsNamesOrderAndBenefits() {
        User user = demoUser();
        user.setTeamPerformance(new BigDecimal("90000.00"));
        when(userMapper.selectById(10001L)).thenReturn(user);
        lenient().when(sysConfigMapper.selectOne(any())).thenReturn(null);
        when(sysConfigMapper.selectOne(any()))
                .thenReturn(config("县代"))
                .thenReturn(config("30000.00"))
                .thenReturn(config("2"))
                .thenReturn(config("县代后台配置权益"))
                .thenReturn(config("市代"))
                .thenReturn(config("100000.00"))
                .thenReturn(config("3"))
                .thenReturn(config("市代后台配置权益"))
                .thenReturn(null);

        DistributionLevelOverviewResponse response = userService.getDistributionLevelOverview(10001L);

        assertThat(response.getCurrentLevel().getName()).isEqualTo("县代");
        assertThat(response.getNextTargetAmount()).isEqualByComparingTo("100000.00");
        assertThat(response.getUpgradeDifference()).isEqualByComparingTo("10000.00");
        assertThat(response.getProgressPercent()).isEqualByComparingTo("90.00");
        assertThat(response.getLevels()).extracting("name").containsExactly("县代", "市代");
        assertThat(response.getLevels().get(0).getEntryAmount()).isEqualByComparingTo("30000.00");
        assertThat(response.getLevels().get(1).getEntryAmount()).isEqualByComparingTo("100000.00");
        assertThat(response.getLevels().get(0).getBenefits()).containsExactly("县代后台配置权益");
    }

    @Test
    void getDistributionLevelOverviewFailsClearlyWhenNextTargetIsPendingConfirmation() {
        when(userMapper.selectById(10001L)).thenReturn(demoUser());
        when(sysConfigMapper.selectOne(any()))
                .thenReturn(config("县级联营商"))
                .thenReturn(config("50000.00"))
                .thenReturn(config("2"))
                .thenReturn(config("县级权益"))
                .thenReturn(config("市级联营商"))
                .thenReturn(config("待业务确认"));

        assertThatThrownBy(() -> userService.getDistributionLevelOverview(10001L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("level.city.main_performance_threshold")
                .hasMessageContaining("待业务确认");
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

    private SysConfig config(String value) {
        SysConfig config = new SysConfig();
        config.setConfigValue(value);
        return config;
    }
}
