package com.drinkmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.drinkmall.common.BusinessException;
import com.drinkmall.dto.TeamMemberResponse;
import com.drinkmall.dto.TeamOverviewResponse;
import com.drinkmall.entity.InvitationCode;
import com.drinkmall.entity.Product;
import com.drinkmall.entity.RewardRecord;
import com.drinkmall.entity.SysConfig;
import com.drinkmall.entity.User;
import com.drinkmall.enums.AssetType;
import com.drinkmall.enums.PaymentMethod;
import com.drinkmall.enums.ProductZoneType;
import com.drinkmall.enums.RealNameStatus;
import com.drinkmall.enums.RewardStatus;
import com.drinkmall.enums.RewardType;
import com.drinkmall.enums.UserLevel;
import com.drinkmall.mapper.InvitationCodeMapper;
import com.drinkmall.mapper.ProductMapper;
import com.drinkmall.mapper.RewardRecordMapper;
import com.drinkmall.mapper.SysConfigMapper;
import com.drinkmall.mapper.UserMapper;
import com.drinkmall.service.PhaseOneCoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PhaseOneCoreServiceImpl implements PhaseOneCoreService {

    private final UserMapper userMapper;
    private final ProductMapper productMapper;
    private final RewardRecordMapper rewardRecordMapper;
    private final SysConfigMapper sysConfigMapper;
    private final InvitationCodeMapper invitationCodeMapper;

    @Override
    @Transactional
    public User registerUser(String openid, String inviteCode, String registerSource, boolean seedAccount) {
        return registerUser(openid, null, null, inviteCode, registerSource, seedAccount);
    }

    @Override
    @Transactional
    public User registerUser(String openid, String unionid, Long inviterId, String inviteCode, String registerSource, boolean seedAccount) {
        User exists = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getOpenid, openid));
        if (exists != null) {
            return exists;
        }

        User inviter = null;
        InvitationCode generatedInvitationCode = null;
        if (!seedAccount) {
            if (inviterId != null) {
                inviter = userMapper.selectById(inviterId);
                if (inviter == null) {
                    throw new BusinessException(400, "推荐人不存在");
                }
            } else if (inviteCode == null || inviteCode.isBlank()) {
                throw new BusinessException(400, "普通新用户注册必须填写邀请码或通过推荐链接进入");
            } else {
                inviter = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getInviteCode, inviteCode));
                if (inviter == null) {
                    generatedInvitationCode = invitationCodeMapper.selectOne(
                            new LambdaQueryWrapper<InvitationCode>()
                                    .eq(InvitationCode::getCode, inviteCode)
                                    .eq(InvitationCode::getStatus, "active")
                    );
                    if (generatedInvitationCode != null) {
                        inviter = userMapper.selectById(generatedInvitationCode.getOwnerUserId());
                    }
                }
                if (inviter == null) {
                    throw new BusinessException(400, "邀请码无效");
                }
            }
        }

        User user = new User();
        user.setOpenid(openid);
        user.setUnionid(unionid);
        user.setInviterId(inviter == null ? null : inviter.getId());
        user.setInviteCode(generateInviteCode());
        user.setRegisterInviteCode(inviteCode);
        user.setRegisterSource(registerSource == null || registerSource.isBlank() ? "unknown" : registerSource);
        user.setSeedAccount(seedAccount);
        user.setBalance(BigDecimal.ZERO);
        user.setFrozenBalance(BigDecimal.ZERO);
        user.setDfBalance(BigDecimal.ZERO);
        user.setWineBeanBalance(BigDecimal.ZERO);
        user.setOptionBalance(BigDecimal.ZERO);
        user.setPoints(0);
        user.setDistributionLevel(UserLevel.NORMAL.getCode());
        user.setTeamPerformance(BigDecimal.ZERO);
        user.setMainZonePaidAmount(BigDecimal.ZERO);
        user.setRealNameStatus(RealNameStatus.NOT_SUBMITTED.getCode());
        user.setAgeVerified(false);
        user.setStatus(1);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.insert(user);

        if (generatedInvitationCode != null) {
            generatedInvitationCode.setStatus("used");
            generatedInvitationCode.setUsedByUserId(user.getId());
            generatedInvitationCode.setUsedAt(LocalDateTime.now());
            generatedInvitationCode.setUpdatedAt(LocalDateTime.now());
            invitationCodeMapper.updateById(generatedInvitationCode);
        }
        return user;
    }

    @Override
    @Transactional
    public InvitationCode createInvitationCode(Long ownerUserId, Long adminId, String source) {
        User owner = userMapper.selectById(ownerUserId);
        if (owner == null) {
            throw new BusinessException(404, "归属用户不存在");
        }
        InvitationCode code = new InvitationCode();
        code.setCode(generateInviteCode());
        code.setOwnerUserId(ownerUserId);
        code.setSource(source == null || source.isBlank() ? "admin" : source);
        code.setStatus("active");
        code.setCreatedByAdminId(adminId);
        code.setCreatedAt(LocalDateTime.now());
        code.setUpdatedAt(LocalDateTime.now());
        invitationCodeMapper.insert(code);
        return code;
    }

    @Override
    public void validateBeforeCreateOrder(Long userId, Product product, PaymentMethod paymentMethod) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        if (ProductZoneType.INVESTMENT.getCode().equals(product.getZoneType()) && realNameEnabled()
                && !RealNameStatus.APPROVED.getCode().equals(user.getRealNameStatus())) {
            throw new BusinessException(400, "购买招商专区商品前必须完成实名认证");
        }
        if (ProductZoneType.GIFT.getCode().equals(product.getZoneType())
                && paymentMethod != PaymentMethod.POINTS) {
            throw new BusinessException(400, "礼包专区只允许纯积分支付");
        }
        String allowed = product.getAllowedPaymentMethods();
        if (allowed != null && !allowed.isBlank() && !("," + allowed + ",").contains("," + paymentMethod.getCode() + ",")) {
            throw new BusinessException(400, "商品不支持当前支付方式");
        }
    }

    @Override
    @Transactional
    public void recordPaidOrder(Long userId, String zoneType, BigDecimal amount, String orderNo) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        if (ProductZoneType.MAIN.getCode().equals(zoneType)) {
            BigDecimal before = value(user.getMainZonePaidAmount());
            BigDecimal after = before.add(amount);
            user.setMainZonePaidAmount(after);
            if (after.compareTo(configDecimal("level.promoter.main_paid_threshold")) >= 0
                    && UserLevel.NORMAL.getCode().equals(user.getDistributionLevel())) {
                user.setDistributionLevel(UserLevel.PROMOTER.getCode());
            }
            user.setUpdatedAt(LocalDateTime.now());
            userMapper.updateById(user);
        }
    }

    @Override
    @Transactional
    public void issueRetailReward(Long buyerUserId, BigDecimal payAmount, Long orderId, String orderNo) {
        String idempotencyKey = orderNo + ":" + RewardType.RETAIL_DIRECT.getCode();
        Long exists = rewardRecordMapper.selectCount(
                new LambdaQueryWrapper<RewardRecord>().eq(RewardRecord::getIdempotencyKey, idempotencyKey)
        );
        if (exists != null && exists > 0) {
            return;
        }

        User buyer = userMapper.selectById(buyerUserId);
        if (buyer == null || buyer.getInviterId() == null) {
            return;
        }

        RewardRecord record = new RewardRecord();
        record.setSourceUserId(buyerUserId);
        record.setBeneficiaryUserId(buyer.getInviterId());
        record.setOrderId(orderId);
        record.setOrderNo(orderNo);
        record.setRewardType(RewardType.RETAIL_DIRECT.getCode());
        record.setBaseAmount(payAmount);
        BigDecimal ratio = configDecimal("reward.retail.direct_ratio");
        record.setRatio(ratio);
        record.setAmount(payAmount.multiply(ratio).setScale(2, RoundingMode.HALF_UP));
        record.setStatus(RewardStatus.FROZEN.getCode());
        record.setFrozenUntil(LocalDateTime.now().plusDays(configLong("reward.freeze_days")));
        record.setIdempotencyKey(idempotencyKey);
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());
        rewardRecordMapper.insert(record);
    }

    @Override
    public void validateWithdrawalAsset(AssetType assetType) {
        if (!assetType.isWithdrawable()) {
            throw new BusinessException(400, assetType.getCode().toUpperCase() + " 不可提现");
        }
    }

    @Override
    public TeamOverviewResponse getMiniTeamOverview(Long userId) {
        return TeamOverviewResponse.builder()
                .teamTotal(userMapper.countTeamMembers(userId))
                .directMembers(toMembers(userMapper.selectDirectInvitees(userId)))
                .indirectMembers(toMembers(userMapper.selectIndirectInvitees(userId)))
                .thirdLevelMembers(null)
                .build();
    }

    private List<TeamMemberResponse> toMembers(List<User> users) {
        return users.stream().map(TeamMemberResponse::fromUser).toList();
    }

    private BigDecimal value(BigDecimal amount) {
        return amount == null ? BigDecimal.ZERO : amount;
    }

    private String generateInviteCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }

    private BigDecimal configDecimal(String key) {
        SysConfig config = sysConfigMapper.selectOne(new LambdaQueryWrapper<SysConfig>().eq(SysConfig::getConfigKey, key));
        if (config == null || config.getConfigValue() == null || config.getConfigValue().isBlank()) {
            throw new BusinessException(500, "系统配置缺失: " + key);
        }
        return new BigDecimal(config.getConfigValue());
    }

    private long configLong(String key) {
        SysConfig config = sysConfigMapper.selectOne(new LambdaQueryWrapper<SysConfig>().eq(SysConfig::getConfigKey, key));
        if (config == null || config.getConfigValue() == null || config.getConfigValue().isBlank()) {
            throw new BusinessException(500, "系统配置缺失: " + key);
        }
        return Long.parseLong(config.getConfigValue());
    }

    private boolean realNameEnabled() {
        SysConfig config = sysConfigMapper.selectOne(new LambdaQueryWrapper<SysConfig>().eq(SysConfig::getConfigKey, "real_name.enabled"));
        if (config == null || config.getConfigValue() == null || config.getConfigValue().isBlank()) {
            throw new BusinessException(500, "系统配置缺失: real_name.enabled");
        }
        return Boolean.parseBoolean(config.getConfigValue());
    }
}
