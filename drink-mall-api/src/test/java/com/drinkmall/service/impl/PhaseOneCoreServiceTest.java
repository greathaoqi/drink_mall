package com.drinkmall.service.impl;

import com.drinkmall.common.BusinessException;
import com.drinkmall.dto.TeamOverviewResponse;
import com.drinkmall.entity.Product;
import com.drinkmall.entity.RewardRecord;
import com.drinkmall.entity.SysConfig;
import com.drinkmall.entity.User;
import com.drinkmall.enums.AssetType;
import com.drinkmall.enums.PaymentMethod;
import com.drinkmall.enums.ProductZoneType;
import com.drinkmall.enums.UserLevel;
import com.drinkmall.mapper.ProductMapper;
import com.drinkmall.mapper.RewardRecordMapper;
import com.drinkmall.mapper.SysConfigMapper;
import com.drinkmall.mapper.UserMapper;
import com.drinkmall.mapper.InvitationCodeMapper;
import com.drinkmall.service.PhaseOneCoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PhaseOneCoreServiceTest {

    @Mock
    private UserMapper userMapper;
    @Mock
    private ProductMapper productMapper;
    @Mock
    private RewardRecordMapper rewardRecordMapper;
    @Mock
    private SysConfigMapper sysConfigMapper;
    @Mock
    private InvitationCodeMapper invitationCodeMapper;

    private PhaseOneCoreService phaseOneCoreService;

    @BeforeEach
    void setUp() {
        phaseOneCoreService = new PhaseOneCoreServiceImpl(userMapper, productMapper, rewardRecordMapper, sysConfigMapper, invitationCodeMapper);
    }

    @Test
    void registerRequiresInviterForNormalUser() {
        assertThatThrownBy(() -> phaseOneCoreService.registerUser("openid-new", null, "manual", false))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("邀请码");
    }

    @Test
    void seedAccountCanRegisterWithoutInviter() {
        phaseOneCoreService.registerUser("seed-openid", null, "admin_seed", true);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).insert(captor.capture());
        assertThat(captor.getValue().getSeedAccount()).isTrue();
        assertThat(captor.getValue().getInviterId()).isNull();
    }

    @Test
    void registerWithInviteCodeBindsInviterAndRecordsSource() {
        User inviter = user(11L);
        inviter.setInviteCode("ABC12345");
        when(userMapper.selectOne(any()))
                .thenReturn(null)
                .thenReturn(inviter);

        phaseOneCoreService.registerUser("openid-new", "ABC12345", "share_product", false);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).insert(captor.capture());
        assertThat(captor.getValue().getInviterId()).isEqualTo(11L);
        assertThat(captor.getValue().getRegisterSource()).isEqualTo("share_product");
        assertThat(captor.getValue().getRegisterInviteCode()).isEqualTo("ABC12345");
    }

    @Test
    void mainProductCumulativePaidAmountUpgradesPromoter() {
        User user = user(10L);
        user.setDistributionLevel(UserLevel.NORMAL.getCode());
        user.setMainZonePaidAmount(new BigDecimal("300.00"));
        when(userMapper.selectById(10L)).thenReturn(user);
        when(sysConfigMapper.selectOne(any())).thenReturn(config("396.00"));

        phaseOneCoreService.recordPaidOrder(10L, ProductZoneType.MAIN.getCode(), new BigDecimal("96.00"), "ORD-1");

        assertThat(user.getMainZonePaidAmount()).isEqualByComparingTo("396.00");
        assertThat(user.getDistributionLevel()).isEqualTo(UserLevel.PROMOTER.getCode());
        verify(userMapper).updateById(user);
    }

    @Test
    void retailZoneRewardsOnlyDirectInviter() {
        User buyer = user(20L);
        buyer.setInviterId(21L);
        when(userMapper.selectById(20L)).thenReturn(buyer);
        when(rewardRecordMapper.selectCount(any())).thenReturn(0L);
        when(sysConfigMapper.selectOne(any()))
                .thenReturn(config("0.10"))
                .thenReturn(config("7"));

        phaseOneCoreService.issueRetailReward(20L, new BigDecimal("100.00"), 200L, "ORD-2");

        ArgumentCaptor<RewardRecord> captor = ArgumentCaptor.forClass(RewardRecord.class);
        verify(rewardRecordMapper).insert(captor.capture());
        assertThat(captor.getValue().getBeneficiaryUserId()).isEqualTo(21L);
        assertThat(captor.getValue().getAmount()).isEqualByComparingTo("10.00");
    }

    @Test
    void investmentZoneRequiresRealNameBeforePurchase() {
        User user = user(30L);
        user.setRealNameStatus("not_submitted");
        when(userMapper.selectById(30L)).thenReturn(user);
        when(sysConfigMapper.selectOne(any())).thenReturn(config("true"));
        Product product = product(ProductZoneType.INVESTMENT, PaymentMethod.ONLINE);

        assertThatThrownBy(() -> phaseOneCoreService.validateBeforeCreateOrder(30L, product, PaymentMethod.ONLINE))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("实名");
    }

    @Test
    void realNameRequirementForInvestmentPurchaseCanBeDisabledByConfig() {
        User user = user(31L);
        user.setRealNameStatus("not_submitted");
        when(userMapper.selectById(31L)).thenReturn(user);
        when(sysConfigMapper.selectOne(any())).thenReturn(config("false"));
        Product product = product(ProductZoneType.INVESTMENT, PaymentMethod.ONLINE);

        phaseOneCoreService.validateBeforeCreateOrder(31L, product, PaymentMethod.ONLINE);
    }

    @Test
    void giftZoneAllowsOnlyPurePointsPayment() {
        User user = user(40L);
        user.setRealNameStatus("approved");
        when(userMapper.selectById(40L)).thenReturn(user);
        Product product = product(ProductZoneType.GIFT, PaymentMethod.POINTS);

        assertThatThrownBy(() -> phaseOneCoreService.validateBeforeCreateOrder(40L, product, PaymentMethod.BALANCE))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("积分");
    }

    @Test
    void dfAssetCannotBeWithdrawn() {
        assertThatThrownBy(() -> phaseOneCoreService.validateWithdrawalAsset(AssetType.DF))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("DF");
    }

    @Test
    void teamOverviewDoesNotExposeThirdLevelDetails() {
        when(userMapper.selectDirectInvitees(50L)).thenReturn(List.of(user(51L)));
        when(userMapper.selectIndirectInvitees(50L)).thenReturn(List.of(user(52L)));
        when(userMapper.countTeamMembers(50L)).thenReturn(3L);

        TeamOverviewResponse response = phaseOneCoreService.getMiniTeamOverview(50L);

        assertThat(response.getDirectMembers()).hasSize(1);
        assertThat(response.getIndirectMembers()).hasSize(1);
        assertThat(response.getTeamTotal()).isEqualTo(3L);
        assertThat(response.getThirdLevelMembers()).isNull();
    }

    @Test
    void rewardIssueIsIdempotentByOrderAndType() {
        when(rewardRecordMapper.selectCount(any())).thenReturn(1L);

        phaseOneCoreService.issueRetailReward(60L, new BigDecimal("100.00"), 600L, "ORD-6");

        verify(userMapper, never()).selectById(60L);
        verify(rewardRecordMapper, never()).insert(any());
    }

    private User user(Long id) {
        User user = new User();
        user.setId(id);
        user.setDistributionLevel(UserLevel.NORMAL.getCode());
        user.setMainZonePaidAmount(BigDecimal.ZERO);
        return user;
    }

    private Product product(ProductZoneType zoneType, PaymentMethod paymentMethod) {
        Product product = new Product();
        product.setZoneType(zoneType.getCode());
        product.setAllowedPaymentMethods(paymentMethod.getCode());
        return product;
    }

    private SysConfig config(String value) {
        SysConfig config = new SysConfig();
        config.setConfigValue(value);
        return config;
    }
}
