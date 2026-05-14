package com.drinkmall.service.admin.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drinkmall.entity.AssetLog;
import com.drinkmall.entity.BalanceLog;
import com.drinkmall.entity.PointsLog;
import com.drinkmall.entity.RewardRecord;
import com.drinkmall.entity.Withdrawal;
import com.drinkmall.enums.AssetType;
import com.drinkmall.mapper.BalanceLogMapper;
import com.drinkmall.mapper.PointsLogMapper;
import com.drinkmall.mapper.WithdrawalMapper;
import com.drinkmall.service.AssetService;
import com.drinkmall.service.RewardService;
import com.drinkmall.service.WithdrawalService;
import com.drinkmall.service.admin.AdminFinanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminFinanceServiceImpl implements AdminFinanceService {

    private final WithdrawalMapper withdrawalMapper;
    private final PointsLogMapper pointsLogMapper;
    private final BalanceLogMapper balanceLogMapper;
    private final AssetService assetService;
    private final RewardService rewardService;
    private final WithdrawalService withdrawalService;

    @Override
    public Page<Withdrawal> getWithdrawals(String status, Integer page, Integer size) {
        return withdrawalService.listAdmin(status, page, size);
    }

    @Override
    public void approveWithdrawal(Long withdrawalId, Long adminId, String offlineTransferNo) {
        withdrawalService.approve(withdrawalId, adminId, offlineTransferNo);
    }

    @Override
    public void rejectWithdrawal(Long withdrawalId, Long adminId, String reason) {
        withdrawalService.reject(withdrawalId, adminId, reason);
    }

    @Override
    public Page<Map<String, Object>> getBalanceLogs(Long userId, Integer page, Integer size) {
        Page<BalanceLog> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<BalanceLog> wrapper = new LambdaQueryWrapper<BalanceLog>()
                .orderByDesc(BalanceLog::getCreatedAt);
        if (userId != null) {
            wrapper.eq(BalanceLog::getUserId, userId);
        }
        Page<BalanceLog> logs = balanceLogMapper.selectPage(pageParam, wrapper);

        Page<Map<String, Object>> result = new Page<>(page, size);
        result.setTotal(logs.getTotal());
        result.setRecords(logs.getRecords().stream().map(log -> {
            Map<String, Object> item = new HashMap<>();
            item.put("id", log.getId());
            item.put("userId", log.getUserId());
            item.put("changeType", log.getChangeType());
            item.put("amount", log.getAmount());
            item.put("beforeBalance", log.getBeforeBalance());
            item.put("afterBalance", log.getAfterBalance());
            item.put("orderId", log.getOrderId());
            item.put("remark", log.getRemark());
            item.put("createdAt", log.getCreatedAt());
            return item;
        }).toList());
        return result;
    }

    @Override
    public Page<PointsLog> getPointsLogs(Long userId, Integer page, Integer size) {
        Page<PointsLog> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<PointsLog> wrapper = new LambdaQueryWrapper<PointsLog>().orderByDesc(PointsLog::getCreatedAt);
        if (userId != null) {
            wrapper.eq(PointsLog::getUserId, userId);
        }
        return pointsLogMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public Page<AssetLog> getAssetLogs(Long userId, AssetType assetType, Integer page, Integer size) {
        return assetService.getLogs(userId, assetType, page, size);
    }

    @Override
    public Page<RewardRecord> getRewardRecords(Long userId, String status, Integer page, Integer size) {
        return rewardService.getRecords(userId, status, page, size);
    }

    @Override
    public void adjustAsset(Long adminId, Long userId, AssetType assetType, BigDecimal amount, String reason) {
        assetService.adminAdjust(adminId, userId, assetType, amount, reason);
    }

    @Override
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalWithdrawals", withdrawalMapper.selectCount(null));
        stats.put("pendingWithdrawals", withdrawalMapper.selectCount(new LambdaQueryWrapper<Withdrawal>().eq(Withdrawal::getStatus, "pending")));
        stats.put("approvedWithdrawals", withdrawalMapper.selectCount(new LambdaQueryWrapper<Withdrawal>().eq(Withdrawal::getStatus, "approved")));
        return stats;
    }
}
