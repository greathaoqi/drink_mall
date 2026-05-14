package com.drinkmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drinkmall.common.BusinessException;
import com.drinkmall.dto.WithdrawalRequest;
import com.drinkmall.entity.OperationLog;
import com.drinkmall.entity.SysConfig;
import com.drinkmall.entity.User;
import com.drinkmall.entity.Withdrawal;
import com.drinkmall.enums.AssetType;
import com.drinkmall.mapper.OperationLogMapper;
import com.drinkmall.mapper.SysConfigMapper;
import com.drinkmall.mapper.UserMapper;
import com.drinkmall.mapper.WithdrawalMapper;
import com.drinkmall.service.AssetService;
import com.drinkmall.service.WithdrawalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WithdrawalServiceImpl implements WithdrawalService {

    private final WithdrawalMapper withdrawalMapper;
    private final UserMapper userMapper;
    private final SysConfigMapper sysConfigMapper;
    private final AssetService assetService;
    private final OperationLogMapper operationLogMapper;

    @Override
    @Transactional
    public void submit(Long userId, WithdrawalRequest request) {
        User user = requireUser(userId);
        if (realNameEnabled() && !"approved".equals(user.getRealNameStatus())) {
            throw new BusinessException(400, "提现前必须完成实名认证");
        }
        BigDecimal minAmount = configDecimal("withdraw.min_amount");
        if (request.getAmount().compareTo(minAmount) < 0) {
            throw new BusinessException(400, "低于最小提现金额");
        }
        long pending = withdrawalMapper.selectCount(new LambdaQueryWrapper<Withdrawal>()
                .eq(Withdrawal::getUserId, userId)
                .eq(Withdrawal::getStatus, "pending"));
        if (pending > 0) {
            throw new BusinessException(400, "存在待审核提现申请");
        }

        BigDecimal fee = request.getAmount().multiply(configDecimal("withdraw.fee_ratio")).setScale(2, RoundingMode.HALF_UP);
        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setUserId(userId);
        withdrawal.setAmount(request.getAmount());
        withdrawal.setFeeAmount(fee);
        withdrawal.setActualAmount(request.getAmount().subtract(fee));
        withdrawal.setBankName(request.getBankName());
        withdrawal.setBankAccount(request.getBankAccount());
        withdrawal.setAccountName(request.getAccountName());
        withdrawal.setStatus("pending");
        withdrawal.setCreatedAt(LocalDateTime.now());
        withdrawalMapper.insert(withdrawal);

        assetService.freezeBalance(userId, request.getAmount(), "withdraw_submit", withdrawal.getId(),
                "withdrawal", "withdraw_submit:" + userId + ":" + withdrawal.getId(), "submit withdrawal");
    }

    @Override
    public Page<Withdrawal> listUser(Long userId, Integer page, Integer size) {
        return withdrawalMapper.selectPage(new Page<>(page, size), new LambdaQueryWrapper<Withdrawal>()
                .eq(Withdrawal::getUserId, userId)
                .orderByDesc(Withdrawal::getCreatedAt));
    }

    @Override
    public Page<Withdrawal> listAdmin(String status, Integer page, Integer size) {
        LambdaQueryWrapper<Withdrawal> wrapper = new LambdaQueryWrapper<Withdrawal>().orderByDesc(Withdrawal::getCreatedAt);
        if (status != null && !status.isBlank()) {
            wrapper.eq(Withdrawal::getStatus, status);
        }
        return withdrawalMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    @Transactional
    public void approve(Long withdrawalId, Long adminId, String offlineTransferNo) {
        Withdrawal withdrawal = requirePending(withdrawalId);
        assetService.consumeFrozenBalance(withdrawal.getUserId(), withdrawal.getAmount(), "withdraw_approve",
                withdrawalId, "withdrawal", "withdraw_approve:" + withdrawalId, "approve withdrawal");
        withdrawal.setStatus("approved");
        withdrawal.setOfflineTransferNo(offlineTransferNo);
        withdrawal.setReviewerAdminId(adminId);
        withdrawal.setProcessedAt(LocalDateTime.now());
        withdrawalMapper.updateById(withdrawal);
        log(adminId, "approve", withdrawalId, "offlineTransferNo=" + offlineTransferNo);
    }

    @Override
    @Transactional
    public void reject(Long withdrawalId, Long adminId, String reason) {
        Withdrawal withdrawal = requirePending(withdrawalId);
        assetService.releaseFrozenBalance(withdrawal.getUserId(), withdrawal.getAmount(), "withdraw_reject",
                withdrawalId, "withdrawal", "withdraw_reject:" + withdrawalId, "reject withdrawal");
        withdrawal.setStatus("rejected");
        withdrawal.setRejectReason(reason);
        withdrawal.setReviewerAdminId(adminId);
        withdrawal.setProcessedAt(LocalDateTime.now());
        withdrawalMapper.updateById(withdrawal);
        log(adminId, "reject", withdrawalId, reason);
    }

    private Withdrawal requirePending(Long withdrawalId) {
        Withdrawal withdrawal = withdrawalMapper.selectById(withdrawalId);
        if (withdrawal == null) {
            throw new BusinessException(404, "提现申请不存在");
        }
        if (!"pending".equals(withdrawal.getStatus())) {
            throw new BusinessException(400, "该申请已处理");
        }
        return withdrawal;
    }

    private User requireUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return user;
    }

    private BigDecimal configDecimal(String key) {
        SysConfig config = sysConfigMapper.selectOne(new LambdaQueryWrapper<SysConfig>().eq(SysConfig::getConfigKey, key));
        if (config == null || config.getConfigValue() == null || config.getConfigValue().isBlank()) {
            throw new BusinessException(500, "系统配置缺失: " + key);
        }
        return new BigDecimal(config.getConfigValue());
    }

    private boolean realNameEnabled() {
        SysConfig config = sysConfigMapper.selectOne(new LambdaQueryWrapper<SysConfig>().eq(SysConfig::getConfigKey, "real_name.enabled"));
        if (config == null || config.getConfigValue() == null || config.getConfigValue().isBlank()) {
            throw new BusinessException(500, "系统配置缺失: real_name.enabled");
        }
        return Boolean.parseBoolean(config.getConfigValue());
    }

    private void log(Long adminId, String action, Long targetId, String detail) {
        OperationLog log = new OperationLog();
        log.setAdminUserId(adminId);
        log.setModule("withdrawal");
        log.setAction(action);
        log.setTargetId(String.valueOf(targetId));
        log.setDetail(detail);
        log.setCreatedAt(LocalDateTime.now());
        operationLogMapper.insert(log);
    }
}
