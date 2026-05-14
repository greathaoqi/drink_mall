package com.drinkmall.service.impl;

import com.drinkmall.common.BusinessException;
import com.drinkmall.dto.WithdrawalRequest;
import com.drinkmall.entity.*;
import com.drinkmall.enums.ProductZoneType;
import com.drinkmall.enums.RewardStatus;
import com.drinkmall.enums.UserLevel;
import com.drinkmall.mapper.*;
import com.drinkmall.service.AssetService;
import com.drinkmall.service.RewardService;
import com.drinkmall.service.WithdrawalService;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssetRewardWithdrawalServiceTest {

    @Mock private UserMapper userMapper;
    @Mock private AssetAccountMapper assetAccountMapper;
    @Mock private AssetLogMapper assetLogMapper;
    @Mock private RewardRecordMapper rewardRecordMapper;
    @Mock private SysConfigMapper sysConfigMapper;
    @Mock private WithdrawalMapper withdrawalMapper;
    @Mock private OperationLogMapper operationLogMapper;
    @Mock private OrderMapper orderMapper;
    @Mock private OrderItemMapper orderItemMapper;
    @Mock private ProductMapper productMapper;

    private AssetService assetService;
    private RewardService rewardService;
    private WithdrawalService withdrawalService;

    @BeforeEach
    void setUp() {
        assetService = new AssetServiceImpl(userMapper, assetAccountMapper, assetLogMapper, operationLogMapper);
        rewardService = new RewardServiceImpl(
                rewardRecordMapper, userMapper, sysConfigMapper, orderMapper, orderItemMapper, productMapper, assetService
        );
        withdrawalService = new WithdrawalServiceImpl(
                withdrawalMapper, userMapper, sysConfigMapper, assetService, operationLogMapper
        );
    }

    @Test
    void submitWithdrawalFreezesOnlyWithdrawableBalance() {
        User user = user(10L, UserLevel.NORMAL);
        user.setRealNameStatus("approved");
        user.setBalance(new BigDecimal("100.00"));
        user.setFrozenBalance(BigDecimal.ZERO);
        when(userMapper.selectById(10L)).thenReturn(user);
        when(sysConfigMapper.selectOne(any()))
                .thenReturn(config("true"))
                .thenReturn(config("10.00"))
                .thenReturn(config("0.02"));
        when(withdrawalMapper.selectCount(any())).thenReturn(0L);

        withdrawalService.submit(10L, withdrawalRequest("50.00"));

        assertThat(user.getBalance()).isEqualByComparingTo("50.00");
        assertThat(user.getFrozenBalance()).isEqualByComparingTo("50.00");
        ArgumentCaptor<Withdrawal> withdrawal = ArgumentCaptor.forClass(Withdrawal.class);
        verify(withdrawalMapper).insert(withdrawal.capture());
        assertThat(withdrawal.getValue().getFeeAmount()).isEqualByComparingTo("1.00");
        verify(assetLogMapper).insert(any(AssetLog.class));
    }

    @Test
    void approvingWithdrawalDeductsFrozenBalanceWithoutDoubleChargingAvailableBalance() {
        User user = user(11L, UserLevel.NORMAL);
        user.setBalance(new BigDecimal("50.00"));
        user.setFrozenBalance(new BigDecimal("50.00"));
        Withdrawal withdrawal = withdrawal(100L, 11L, "50.00");
        when(withdrawalMapper.selectById(100L)).thenReturn(withdrawal);
        when(userMapper.selectById(11L)).thenReturn(user);

        withdrawalService.approve(100L, 1L, "offline-paid");

        assertThat(user.getBalance()).isEqualByComparingTo("50.00");
        assertThat(user.getFrozenBalance()).isEqualByComparingTo("0.00");
        assertThat(withdrawal.getStatus()).isEqualTo("approved");
        verify(operationLogMapper).insert(any(OperationLog.class));
    }

    @Test
    void unapprovedRealNameCannotWithdraw() {
        User user = user(12L, UserLevel.NORMAL);
        user.setRealNameStatus("pending");
        user.setBalance(new BigDecimal("100.00"));
        when(userMapper.selectById(12L)).thenReturn(user);
        when(sysConfigMapper.selectOne(any())).thenReturn(config("true"));

        assertThatThrownBy(() -> withdrawalService.submit(12L, withdrawalRequest("20.00")))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("实名");
    }

    @Test
    void retailRewardCreatesOnlyDirectFrozenRecordAndIsIdempotent() {
        User buyer = user(20L, UserLevel.NORMAL);
        buyer.setInviterId(21L);
        Order order = order(200L, 20L, ProductZoneType.RETAIL, "100.00");
        when(rewardRecordMapper.selectCount(any())).thenReturn(0L);
        when(userMapper.selectById(20L)).thenReturn(buyer);
        when(sysConfigMapper.selectOne(any()))
                .thenReturn(config("0.10"))
                .thenReturn(config("7"));

        rewardService.settleOrderRewards(order);

        ArgumentCaptor<RewardRecord> reward = ArgumentCaptor.forClass(RewardRecord.class);
        verify(rewardRecordMapper).insert(reward.capture());
        assertThat(reward.getValue().getBeneficiaryUserId()).isEqualTo(21L);
        assertThat(reward.getValue().getRewardType()).isEqualTo("retail_direct");
        assertThat(reward.getValue().getStatus()).isEqualTo(RewardStatus.FROZEN.getCode());
        assertThat(reward.getValue().getAmount()).isEqualByComparingTo("10.00");

        when(rewardRecordMapper.selectCount(any())).thenReturn(1L);
        rewardService.settleOrderRewards(order);
        verify(rewardRecordMapper, times(1)).insert(any(RewardRecord.class));
    }

    @Test
    void investmentRewardTracesToQualifiedMerchantAndSupportRewardRequiresMerchantParent() {
        User buyer = user(30L, UserLevel.NORMAL);
        buyer.setInviterId(31L);
        User middle = user(31L, UserLevel.PROMOTER);
        middle.setInviterId(32L);
        User merchant = user(32L, UserLevel.COUNTY);
        merchant.setInviterId(33L);
        User merchantParent = user(33L, UserLevel.CITY);
        Order order = order(300L, 30L, ProductZoneType.INVESTMENT, "1000.00");
        when(userMapper.selectById(30L)).thenReturn(buyer);
        when(userMapper.selectById(31L)).thenReturn(middle);
        when(userMapper.selectById(32L)).thenReturn(merchant);
        when(userMapper.selectById(33L)).thenReturn(merchantParent);
        when(rewardRecordMapper.selectCount(any())).thenReturn(0L);
        when(sysConfigMapper.selectOne(any()))
                .thenReturn(config("0.05"))
                .thenReturn(config("7"))
                .thenReturn(config("0.20"))
                .thenReturn(config("500.00"))
                .thenReturn(config("7"))
                .thenReturn(config("0.10"))
                .thenReturn(config("7"));

        rewardService.settleOrderRewards(order);

        ArgumentCaptor<RewardRecord> rewards = ArgumentCaptor.forClass(RewardRecord.class);
        verify(rewardRecordMapper, times(3)).insert(rewards.capture());
        List<RewardRecord> records = rewards.getAllValues();
        assertThat(records).extracting(RewardRecord::getRewardType)
                .containsExactly("advertising", "investment", "support_merchant");
        assertThat(records.get(1).getBeneficiaryUserId()).isEqualTo(32L);
        assertThat(records.get(2).getBeneficiaryUserId()).isEqualTo(33L);
    }

    @Test
    void giftZoneDoesNotCreateRewards() {
        rewardService.settleOrderRewards(order(400L, 40L, ProductZoneType.GIFT, "0.00"));

        verify(rewardRecordMapper, never()).insert(any());
    }

    @Test
    void unfreezeAndRollbackRewardsMoveAssetsThroughLedger() {
        RewardRecord frozen = reward(500L, 51L, "retail_direct", "25.00", RewardStatus.FROZEN.getCode());
        frozen.setFrozenUntil(LocalDateTime.now().minusDays(1));
        when(rewardRecordMapper.selectList(any())).thenReturn(List.of(frozen));
        when(userMapper.selectById(51L)).thenReturn(user(51L, UserLevel.PROMOTER));

        rewardService.unfreezeDueRewards(LocalDateTime.now());

        assertThat(frozen.getStatus()).isEqualTo(RewardStatus.UNFROZEN.getCode());
        verify(assetLogMapper).insert(any(AssetLog.class));

        RewardRecord unfrozen = reward(501L, 52L, "advertising", "30.00", RewardStatus.UNFROZEN.getCode());
        User user = user(52L, UserLevel.NORMAL);
        user.setDfBalance(new BigDecimal("30.00"));
        when(rewardRecordMapper.selectList(any())).thenReturn(List.of(unfrozen));
        when(userMapper.selectById(52L)).thenReturn(user);

        rewardService.rollbackOrderRewards(900L, "refund");

        assertThat(unfrozen.getStatus()).isEqualTo(RewardStatus.ROLLED_BACK.getCode());
        assertThat(user.getDfBalance()).isEqualByComparingTo("0.00");
    }

    private User user(Long id, UserLevel level) {
        User user = new User();
        user.setId(id);
        user.setDistributionLevel(level.getCode());
        user.setBalance(BigDecimal.ZERO);
        user.setFrozenBalance(BigDecimal.ZERO);
        user.setDfBalance(BigDecimal.ZERO);
        user.setWineBeanBalance(BigDecimal.ZERO);
        user.setOptionBalance(BigDecimal.ZERO);
        user.setPoints(0);
        return user;
    }

    private Order order(Long id, Long userId, ProductZoneType zone, String amount) {
        Order order = new Order();
        order.setId(id);
        order.setUserId(userId);
        order.setOrderNo("ORD-" + id);
        order.setZoneType(zone.getCode());
        order.setPayAmount(new BigDecimal(amount));
        return order;
    }

    private RewardRecord reward(Long id, Long beneficiary, String type, String amount, String status) {
        RewardRecord reward = new RewardRecord();
        reward.setId(id);
        reward.setBeneficiaryUserId(beneficiary);
        reward.setRewardType(type);
        reward.setAmount(new BigDecimal(amount));
        reward.setStatus(status);
        reward.setOrderId(900L);
        reward.setOrderNo("ORD-900");
        return reward;
    }

    private Withdrawal withdrawal(Long id, Long userId, String amount) {
        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setId(id);
        withdrawal.setUserId(userId);
        withdrawal.setAmount(new BigDecimal(amount));
        withdrawal.setFeeAmount(BigDecimal.ZERO);
        withdrawal.setStatus("pending");
        return withdrawal;
    }

    private WithdrawalRequest withdrawalRequest(String amount) {
        WithdrawalRequest request = new WithdrawalRequest();
        request.setAmount(new BigDecimal(amount));
        request.setBankName("bank");
        request.setBankAccount("6222");
        request.setAccountName("user");
        return request;
    }

    private SysConfig config(String value) {
        SysConfig config = new SysConfig();
        config.setConfigValue(value);
        return config;
    }
}
