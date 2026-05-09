package com.drinkmall.service.admin.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drinkmall.common.BusinessException;
import com.drinkmall.entity.PointsLog;
import com.drinkmall.entity.User;
import com.drinkmall.entity.Withdrawal;
import com.drinkmall.mapper.PointsLogMapper;
import com.drinkmall.mapper.UserMapper;
import com.drinkmall.mapper.WithdrawalMapper;
import com.drinkmall.service.admin.AdminFinanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminFinanceServiceImpl implements AdminFinanceService {

    private final WithdrawalMapper withdrawalMapper;
    private final PointsLogMapper pointsLogMapper;
    private final UserMapper userMapper;

    @Override
    public Page<Withdrawal> getWithdrawals(String status, Integer page, Integer size) {
        Page<Withdrawal> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Withdrawal> wrapper = new LambdaQueryWrapper<Withdrawal>().orderByDesc(Withdrawal::getCreatedAt);
        if (status != null) wrapper.eq(Withdrawal::getStatus, status);
        return withdrawalMapper.selectPage(pageParam, wrapper);
    }

    @Override
    @Transactional
    public void approveWithdrawal(Long withdrawalId) {
        Withdrawal withdrawal = withdrawalMapper.selectById(withdrawalId);
        if (withdrawal == null) throw new BusinessException(404, "提现申请不存在");
        if (!"pending".equals(withdrawal.getStatus())) throw new BusinessException(400, "该申请已处理");
        withdrawal.setStatus("approved");
        withdrawal.setProcessedAt(LocalDateTime.now());
        withdrawalMapper.updateById(withdrawal);
        User user = userMapper.selectById(withdrawal.getUserId());
        if (user != null) {
            BigDecimal newBalance = user.getBalance().subtract(withdrawal.getAmount());
            user.setBalance(newBalance);
            userMapper.updateById(user);
        }
    }

    @Override
    @Transactional
    public void rejectWithdrawal(Long withdrawalId, String reason) {
        Withdrawal withdrawal = withdrawalMapper.selectById(withdrawalId);
        if (withdrawal == null) throw new BusinessException(404, "提现申请不存在");
        if (!"pending".equals(withdrawal.getStatus())) throw new BusinessException(400, "该申请已处理");
        withdrawal.setStatus("rejected");
        withdrawal.setRejectReason(reason);
        withdrawal.setProcessedAt(LocalDateTime.now());
        withdrawalMapper.updateById(withdrawal);
        User user = userMapper.selectById(withdrawal.getUserId());
        if (user != null && user.getFrozenBalance() != null) {
            user.setBalance(user.getBalance().add(withdrawal.getAmount()));
            user.setFrozenBalance(user.getFrozenBalance().subtract(withdrawal.getAmount()));
            userMapper.updateById(user);
        }
    }

    @Override
    public Page<Map<String, Object>> getBalanceLogs(Long userId, Integer page, Integer size) {
        return new Page<>(page, size);
    }

    @Override
    public Page<PointsLog> getPointsLogs(Long userId, Integer page, Integer size) {
        Page<PointsLog> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<PointsLog> wrapper = new LambdaQueryWrapper<PointsLog>().orderByDesc(PointsLog::getCreatedAt);
        if (userId != null) wrapper.eq(PointsLog::getUserId, userId);
        return pointsLogMapper.selectPage(pageParam, wrapper);
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