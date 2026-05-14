package com.drinkmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drinkmall.common.BusinessException;
import com.drinkmall.entity.*;
import com.drinkmall.enums.AssetType;
import com.drinkmall.enums.ProductZoneType;
import com.drinkmall.enums.RewardStatus;
import com.drinkmall.enums.RewardType;
import com.drinkmall.enums.UserLevel;
import com.drinkmall.mapper.*;
import com.drinkmall.service.AssetService;
import com.drinkmall.service.RewardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RewardServiceImpl implements RewardService {

    private static final Set<String> MERCHANT_LEVELS = Set.of(
            UserLevel.COUNTY.getCode(), UserLevel.CITY.getCode(), UserLevel.PROVINCE.getCode()
    );

    private final RewardRecordMapper rewardRecordMapper;
    private final UserMapper userMapper;
    private final SysConfigMapper sysConfigMapper;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ProductMapper productMapper;
    private final AssetService assetService;

    @Override
    @Transactional
    public void settleOrderRewards(Order order) {
        String zoneType = order.getZoneType();
        if (zoneType == null || zoneType.isBlank()) {
            zoneType = orderZone(order.getId());
        }
        if (ProductZoneType.RETAIL.getCode().equals(zoneType)) {
            issueRetail(order);
        } else if (ProductZoneType.MAIN.getCode().equals(zoneType)) {
            issueMain(order);
        } else if (ProductZoneType.INVESTMENT.getCode().equals(zoneType)) {
            issueInvestment(order);
        }
    }

    @Override
    @Transactional
    public void unfreezeDueRewards(LocalDateTime now) {
        List<RewardRecord> records = rewardRecordMapper.selectList(new LambdaQueryWrapper<RewardRecord>()
                .eq(RewardRecord::getStatus, RewardStatus.FROZEN.getCode())
                .le(RewardRecord::getFrozenUntil, now));
        for (RewardRecord record : records) {
            AssetType assetType = rewardAssetType(record.getRewardType());
            assetService.add(record.getBeneficiaryUserId(), assetType, record.getAmount(),
                    "reward_unfreeze", record.getId(), "reward",
                    "reward_unfreeze:" + record.getId(), "unfreeze reward " + record.getOrderNo());
            record.setStatus(RewardStatus.UNFROZEN.getCode());
            record.setUpdatedAt(LocalDateTime.now());
            rewardRecordMapper.updateById(record);
        }
    }

    @Override
    @Transactional
    public void rollbackOrderRewards(Long orderId, String reason) {
        List<RewardRecord> records = rewardRecordMapper.selectList(new LambdaQueryWrapper<RewardRecord>()
                .eq(RewardRecord::getOrderId, orderId)
                .ne(RewardRecord::getStatus, RewardStatus.ROLLED_BACK.getCode()));
        for (RewardRecord record : records) {
            if (RewardStatus.UNFROZEN.getCode().equals(record.getStatus())) {
                assetService.deduct(record.getBeneficiaryUserId(), rewardAssetType(record.getRewardType()), record.getAmount(),
                        "reward_rollback", record.getId(), "reward",
                        "reward_rollback:" + record.getId(), reason);
            }
            record.setStatus(RewardStatus.ROLLED_BACK.getCode());
            record.setUpdatedAt(LocalDateTime.now());
            rewardRecordMapper.updateById(record);
        }
    }

    @Override
    public Page<RewardRecord> getRecords(Long userId, String status, Integer page, Integer size) {
        LambdaQueryWrapper<RewardRecord> wrapper = new LambdaQueryWrapper<RewardRecord>().orderByDesc(RewardRecord::getCreatedAt);
        if (userId != null) {
            wrapper.eq(RewardRecord::getBeneficiaryUserId, userId);
        }
        if (status != null && !status.isBlank()) {
            wrapper.eq(RewardRecord::getStatus, status);
        }
        return rewardRecordMapper.selectPage(new Page<>(page, size), wrapper);
    }

    private void issueRetail(Order order) {
        User buyer = userMapper.selectById(order.getUserId());
        if (buyer == null || buyer.getInviterId() == null) {
            return;
        }
        createReward(order, buyer.getId(), buyer.getInviterId(), RewardType.RETAIL_DIRECT,
                order.getPayAmount(), configDecimal("reward.retail.direct_ratio"), null);
    }

    private void issueMain(Order order) {
        User buyer = userMapper.selectById(order.getUserId());
        if (buyer == null || buyer.getInviterId() == null) {
            return;
        }
        String type = hasPreviousMainOrder(order) ? RewardType.MAIN_REPURCHASE.getCode() : RewardType.MAIN_FIRST_ORDER.getCode();
        String ratioKey = RewardType.MAIN_FIRST_ORDER.getCode().equals(type)
                ? "reward.main.direct_first_ratio"
                : "reward.main.direct_repurchase_ratio";
        createReward(order, buyer.getId(), buyer.getInviterId(), RewardType.valueOf(type.toUpperCase()), order.getPayAmount(), configDecimal(ratioKey), null);
    }

    private void issueInvestment(Order order) {
        createReward(order, order.getUserId(), order.getUserId(), RewardType.ADVERTISING,
                order.getPayAmount(), configDecimal("reward.investment.advertising_ratio"), null);

        User qualified = findFirstMerchant(order.getUserId());
        if (qualified == null) {
            return;
        }
        BigDecimal cap = configDecimal("reward.investment.cap_amount." + qualified.getDistributionLevel());
        RewardRecord investment = createReward(order, order.getUserId(), qualified.getId(), RewardType.INVESTMENT,
                order.getPayAmount(), configDecimal("reward.investment.ratio." + qualified.getDistributionLevel()), cap);

        User parent = qualified.getInviterId() == null ? null : userMapper.selectById(qualified.getInviterId());
        if (parent != null && isMerchant(parent)) {
            createReward(order, qualified.getId(), parent.getId(), RewardType.SUPPORT_MERCHANT,
                    investment.getAmount(), configDecimal("reward.support_merchant.ratio"), null);
        }
    }

    private RewardRecord createReward(Order order, Long sourceUserId, Long beneficiaryUserId, RewardType rewardType,
                                      BigDecimal baseAmount, BigDecimal ratio, BigDecimal cap) {
        String key = order.getOrderNo() + ":" + rewardType.getCode() + ":" + beneficiaryUserId;
        Long exists = rewardRecordMapper.selectCount(new LambdaQueryWrapper<RewardRecord>().eq(RewardRecord::getIdempotencyKey, key));
        if (exists != null && exists > 0) {
            RewardRecord existing = rewardRecordMapper.selectOne(new LambdaQueryWrapper<RewardRecord>().eq(RewardRecord::getIdempotencyKey, key));
            return existing == null ? new RewardRecord() : existing;
        }
        BigDecimal amount = baseAmount.multiply(ratio).setScale(2, RoundingMode.HALF_UP);
        if (cap != null && amount.compareTo(cap) > 0) {
            amount = cap;
        }
        RewardRecord record = new RewardRecord();
        record.setSourceUserId(sourceUserId);
        record.setBeneficiaryUserId(beneficiaryUserId);
        record.setOrderId(order.getId());
        record.setOrderNo(order.getOrderNo());
        record.setRewardType(rewardType.getCode());
        record.setBaseAmount(baseAmount);
        record.setRatio(ratio);
        record.setAmount(amount);
        record.setStatus(RewardStatus.FROZEN.getCode());
        record.setFrozenUntil(LocalDateTime.now().plusDays(configLong("reward.freeze_days")));
        record.setIdempotencyKey(key);
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());
        rewardRecordMapper.insert(record);
        return record;
    }

    private User findFirstMerchant(Long buyerUserId) {
        User current = userMapper.selectById(buyerUserId);
        Long inviterId = current == null ? null : current.getInviterId();
        while (inviterId != null) {
            User candidate = userMapper.selectById(inviterId);
            if (candidate == null) {
                return null;
            }
            if (isMerchant(candidate)) {
                return candidate;
            }
            inviterId = candidate.getInviterId();
        }
        return null;
    }

    private boolean isMerchant(User user) {
        return user != null && MERCHANT_LEVELS.contains(user.getDistributionLevel());
    }

    private boolean hasPreviousMainOrder(Order order) {
        Long count = orderMapper.selectCount(new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, order.getUserId())
                .eq(Order::getZoneType, ProductZoneType.MAIN.getCode())
                .ne(Order::getId, order.getId())
                .in(Order::getStatus, "paid", "shipped", "completed", "aftersale"));
        return count != null && count > 0;
    }

    private String orderZone(Long orderId) {
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

    private AssetType rewardAssetType(String rewardType) {
        if (RewardType.INVESTMENT.getCode().equals(rewardType)
                || RewardType.SUPPORT_MERCHANT.getCode().equals(rewardType)
                || RewardType.ADVERTISING.getCode().equals(rewardType)) {
            return AssetType.DF;
        }
        return AssetType.BALANCE;
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
}
