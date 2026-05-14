package com.drinkmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drinkmall.common.BusinessException;
import com.drinkmall.entity.AssetAccount;
import com.drinkmall.entity.AssetLog;
import com.drinkmall.entity.OperationLog;
import com.drinkmall.entity.User;
import com.drinkmall.enums.AssetType;
import com.drinkmall.mapper.AssetAccountMapper;
import com.drinkmall.mapper.AssetLogMapper;
import com.drinkmall.mapper.OperationLogMapper;
import com.drinkmall.mapper.UserMapper;
import com.drinkmall.service.AssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AssetServiceImpl implements AssetService {

    private final UserMapper userMapper;
    private final AssetAccountMapper assetAccountMapper;
    private final AssetLogMapper assetLogMapper;
    private final OperationLogMapper operationLogMapper;

    @Override
    public Map<String, Object> getSummary(Long userId) {
        User user = requireUser(userId);
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("balance", money(user.getBalance()));
        summary.put("frozenBalance", money(user.getFrozenBalance()));
        summary.put("df", money(user.getDfBalance()));
        summary.put("wineBean", money(user.getWineBeanBalance()));
        summary.put("points", user.getPoints() == null ? 0 : user.getPoints());
        summary.put("option", money(user.getOptionBalance()));
        return summary;
    }

    @Override
    public Page<AssetLog> getLogs(Long userId, AssetType assetType, Integer page, Integer size) {
        LambdaQueryWrapper<AssetLog> wrapper = new LambdaQueryWrapper<AssetLog>()
                .eq(AssetLog::getUserId, userId)
                .orderByDesc(AssetLog::getCreatedAt);
        if (assetType != null) {
            wrapper.eq(AssetLog::getAssetType, assetType.getCode());
        }
        return assetLogMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    @Transactional
    public void add(Long userId, AssetType assetType, BigDecimal amount, String changeType, Long businessId,
                    String businessType, String idempotencyKey, String remark) {
        change(userId, assetType, amount, changeType, businessId, businessType, idempotencyKey, remark);
    }

    @Override
    @Transactional
    public void deduct(Long userId, AssetType assetType, BigDecimal amount, String changeType, Long businessId,
                       String businessType, String idempotencyKey, String remark) {
        change(userId, assetType, amount.negate(), changeType, businessId, businessType, idempotencyKey, remark);
    }

    @Override
    @Transactional
    public void freezeBalance(Long userId, BigDecimal amount, String changeType, Long businessId,
                              String businessType, String idempotencyKey, String remark) {
        if (idempotencyExists(idempotencyKey)) {
            return;
        }
        User user = requireUser(userId);
        BigDecimal before = money(user.getBalance());
        BigDecimal frozenBefore = money(user.getFrozenBalance());
        if (before.compareTo(amount) < 0) {
            throw new BusinessException(400, "余额不足");
        }
        user.setBalance(before.subtract(amount));
        user.setFrozenBalance(frozenBefore.add(amount));
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
        syncAccount(userId, AssetType.BALANCE, user.getBalance(), user.getFrozenBalance());
        insertLog(userId, AssetType.BALANCE, amount.negate(), before, user.getBalance(), changeType, businessId, businessType, idempotencyKey, remark);
    }

    @Override
    @Transactional
    public void releaseFrozenBalance(Long userId, BigDecimal amount, String changeType, Long businessId,
                                     String businessType, String idempotencyKey, String remark) {
        if (idempotencyExists(idempotencyKey)) {
            return;
        }
        User user = requireUser(userId);
        BigDecimal before = money(user.getBalance());
        BigDecimal frozenBefore = money(user.getFrozenBalance());
        if (frozenBefore.compareTo(amount) < 0) {
            throw new BusinessException(400, "冻结余额不足");
        }
        user.setBalance(before.add(amount));
        user.setFrozenBalance(frozenBefore.subtract(amount));
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
        syncAccount(userId, AssetType.BALANCE, user.getBalance(), user.getFrozenBalance());
        insertLog(userId, AssetType.BALANCE, amount, before, user.getBalance(), changeType, businessId, businessType, idempotencyKey, remark);
    }

    @Override
    @Transactional
    public void consumeFrozenBalance(Long userId, BigDecimal amount, String changeType, Long businessId,
                                     String businessType, String idempotencyKey, String remark) {
        if (idempotencyExists(idempotencyKey)) {
            return;
        }
        User user = requireUser(userId);
        BigDecimal frozenBefore = money(user.getFrozenBalance());
        if (frozenBefore.compareTo(amount) < 0) {
            throw new BusinessException(400, "冻结余额不足");
        }
        user.setFrozenBalance(frozenBefore.subtract(amount));
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
        syncAccount(userId, AssetType.BALANCE, money(user.getBalance()), user.getFrozenBalance());
        insertLog(userId, AssetType.FROZEN_BALANCE, amount.negate(), frozenBefore, user.getFrozenBalance(), changeType, businessId, businessType, idempotencyKey, remark);
    }

    @Override
    @Transactional
    public void exchangeDfToWineBean(Long userId, BigDecimal amount) {
        deduct(userId, AssetType.DF, amount, "exchange_out", null, "df_exchange",
                "df_exchange:" + userId + ":" + System.nanoTime(), "DF exchange to wine bean");
        add(userId, AssetType.WINE_BEAN, amount, "exchange_in", null, "df_exchange",
                "wine_bean_exchange:" + userId + ":" + System.nanoTime(), "DF exchange to wine bean");
    }

    @Override
    @Transactional
    public void transferDf(Long fromUserId, Long toUserId, BigDecimal amount) {
        requireUser(toUserId);
        deduct(fromUserId, AssetType.DF, amount, "transfer_out", toUserId, "df_transfer",
                "df_transfer_out:" + fromUserId + ":" + toUserId + ":" + System.nanoTime(), "DF transfer");
        add(toUserId, AssetType.DF, amount, "transfer_in", fromUserId, "df_transfer",
                "df_transfer_in:" + fromUserId + ":" + toUserId + ":" + System.nanoTime(), "DF transfer");
    }

    @Override
    @Transactional
    public void adminAdjust(Long adminId, Long userId, AssetType assetType, BigDecimal amount, String reason) {
        if (amount.compareTo(BigDecimal.ZERO) >= 0) {
            add(userId, assetType, amount, "admin_adjust", adminId, "admin", "admin_adjust:" + userId + ":" + assetType.getCode() + ":" + System.nanoTime(), reason);
        } else {
            deduct(userId, assetType, amount.abs(), "admin_adjust", adminId, "admin", "admin_adjust:" + userId + ":" + assetType.getCode() + ":" + System.nanoTime(), reason);
        }
        OperationLog log = new OperationLog();
        log.setAdminUserId(adminId);
        log.setModule("asset");
        log.setAction("admin_adjust");
        log.setTargetId(String.valueOf(userId));
        log.setDetail("asset=" + assetType.getCode() + ", amount=" + amount + ", reason=" + reason);
        log.setCreatedAt(LocalDateTime.now());
        operationLogMapper.insert(log);
    }

    private void change(Long userId, AssetType assetType, BigDecimal signedAmount, String changeType, Long businessId,
                        String businessType, String idempotencyKey, String remark) {
        if (signedAmount.compareTo(BigDecimal.ZERO) == 0 || idempotencyExists(idempotencyKey)) {
            return;
        }
        if (assetType == AssetType.FROZEN_BALANCE) {
            throw new BusinessException(400, "冻结余额必须通过冻结接口变更");
        }
        User user = requireUser(userId);
        BigDecimal before = getAmount(user, assetType);
        BigDecimal after = before.add(signedAmount);
        if (after.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(400, assetType.getCode() + "余额不足");
        }
        setAmount(user, assetType, after);
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
        syncAccount(userId, assetType, after, BigDecimal.ZERO);
        insertLog(userId, assetType, signedAmount, before, after, changeType, businessId, businessType, idempotencyKey, remark);
    }

    private BigDecimal getAmount(User user, AssetType assetType) {
        return switch (assetType) {
            case BALANCE -> money(user.getBalance());
            case DF -> money(user.getDfBalance());
            case WINE_BEAN -> money(user.getWineBeanBalance());
            case POINTS -> BigDecimal.valueOf(user.getPoints() == null ? 0 : user.getPoints());
            case OPTION -> money(user.getOptionBalance());
            case FROZEN_BALANCE -> money(user.getFrozenBalance());
        };
    }

    private void setAmount(User user, AssetType assetType, BigDecimal amount) {
        switch (assetType) {
            case BALANCE -> user.setBalance(money(amount));
            case DF -> user.setDfBalance(money(amount));
            case WINE_BEAN -> user.setWineBeanBalance(money(amount));
            case POINTS -> user.setPoints(amount.intValueExact());
            case OPTION -> user.setOptionBalance(money(amount));
            case FROZEN_BALANCE -> user.setFrozenBalance(money(amount));
        }
    }

    private void insertLog(Long userId, AssetType assetType, BigDecimal change, BigDecimal before, BigDecimal after,
                           String changeType, Long businessId, String businessType, String idempotencyKey, String remark) {
        AssetLog log = new AssetLog();
        log.setUserId(userId);
        log.setAssetType(assetType.getCode());
        log.setChangeType(changeType);
        log.setChangeAmount(change);
        log.setBeforeAmount(before);
        log.setAfterAmount(after);
        log.setBusinessId(businessId);
        log.setBusinessType(businessType);
        log.setIdempotencyKey(idempotencyKey);
        log.setRemark(remark);
        log.setCreatedAt(LocalDateTime.now());
        assetLogMapper.insert(log);
    }

    private void syncAccount(Long userId, AssetType assetType, BigDecimal available, BigDecimal frozen) {
        AssetAccount account = assetAccountMapper.selectOne(new LambdaQueryWrapper<AssetAccount>()
                .eq(AssetAccount::getUserId, userId)
                .eq(AssetAccount::getAssetType, assetType.getCode()));
        if (account == null) {
            account = new AssetAccount();
            account.setUserId(userId);
            account.setAssetType(assetType.getCode());
            account.setAvailableAmount(money(available));
            account.setFrozenAmount(money(frozen));
            account.setCreatedAt(LocalDateTime.now());
            account.setUpdatedAt(LocalDateTime.now());
            assetAccountMapper.insert(account);
            return;
        }
        account.setAvailableAmount(money(available));
        account.setFrozenAmount(money(frozen));
        account.setUpdatedAt(LocalDateTime.now());
        assetAccountMapper.updateById(account);
    }

    private boolean idempotencyExists(String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            return false;
        }
        Long count = assetLogMapper.selectCount(new LambdaQueryWrapper<AssetLog>().eq(AssetLog::getIdempotencyKey, idempotencyKey));
        return count != null && count > 0;
    }

    private User requireUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return user;
    }

    private BigDecimal money(BigDecimal amount) {
        return amount == null ? BigDecimal.ZERO : amount.setScale(2, RoundingMode.HALF_UP);
    }
}
