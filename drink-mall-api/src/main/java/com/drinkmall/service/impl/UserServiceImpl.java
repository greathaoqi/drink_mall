package com.drinkmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drinkmall.common.BusinessException;
import com.drinkmall.dto.UserInfoResponse;
import com.drinkmall.dto.WithdrawalRequest;
import com.drinkmall.entity.BalanceLog;
import com.drinkmall.entity.PointsLog;
import com.drinkmall.entity.User;
import com.drinkmall.entity.Withdrawal;
import com.drinkmall.mapper.BalanceLogMapper;
import com.drinkmall.mapper.PointsLogMapper;
import com.drinkmall.mapper.UserMapper;
import com.drinkmall.mapper.WithdrawalMapper;
import com.drinkmall.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final BalanceLogMapper balanceLogMapper;
    private final PointsLogMapper pointsLogMapper;
    private final WithdrawalMapper withdrawalMapper;

    @Override
    public User getById(Long userId) {
        return userMapper.selectById(userId);
    }

    @Override
    public User findByOpenid(String openid) {
        return userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getOpenid, openid)
        );
    }

    @Override
    @Transactional
    public void verifyAge(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        user.setAgeVerified(true);
        user.setAgeVerifiedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userMapper.updateById(user);
        log.info("User {} verified age at {}", userId, user.getAgeVerifiedAt());
    }

    @Override
    public UserInfoResponse getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return null;
        }
        return UserInfoResponse.fromEntity(user);
    }

    @Override
    @Transactional
    public void updateProfile(Long userId, String nickname) {
        User user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException(404, "用户不存在");
        user.setNickname(nickname);
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
    }

    @Override
    @Transactional
    public void applyWithdrawal(Long userId, WithdrawalRequest request) {
        User user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException(404, "用户不存在");
        BigDecimal balance = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;
        if (balance.compareTo(request.getAmount()) < 0) {
            throw new BusinessException(400, "余额不足");
        }
        long pending = withdrawalMapper.selectCount(
            new LambdaQueryWrapper<Withdrawal>()
                .eq(Withdrawal::getUserId, userId)
                .eq(Withdrawal::getStatus, "pending")
        );
        if (pending > 0) throw new BusinessException(400, "您有待审核的提现申请，请等待处理后再提交");

        user.setBalance(balance.subtract(request.getAmount()));
        BigDecimal frozen = user.getFrozenBalance() != null ? user.getFrozenBalance() : BigDecimal.ZERO;
        user.setFrozenBalance(frozen.add(request.getAmount()));
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);

        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setUserId(userId);
        withdrawal.setAmount(request.getAmount());
        withdrawal.setBankName(request.getBankName());
        withdrawal.setBankAccount(request.getBankAccount());
        withdrawal.setAccountName(request.getAccountName());
        withdrawal.setStatus("pending");
        withdrawal.setCreatedAt(LocalDateTime.now());
        withdrawalMapper.insert(withdrawal);
    }

    @Override
    public Page<BalanceLog> getBalanceLogs(Long userId, Integer page, Integer size) {
        return balanceLogMapper.selectPage(
            new Page<>(page, size),
            new LambdaQueryWrapper<BalanceLog>()
                .eq(BalanceLog::getUserId, userId)
                .orderByDesc(BalanceLog::getCreatedAt)
        );
    }

    @Override
    public Page<PointsLog> getPointsLogs(Long userId, Integer page, Integer size) {
        return pointsLogMapper.selectPage(
            new Page<>(page, size),
            new LambdaQueryWrapper<PointsLog>()
                .eq(PointsLog::getUserId, userId)
                .orderByDesc(PointsLog::getCreatedAt)
        );
    }

    @Override
    public Page<Withdrawal> getUserWithdrawals(Long userId, Integer page, Integer size) {
        return withdrawalMapper.selectPage(
            new Page<>(page, size),
            new LambdaQueryWrapper<Withdrawal>()
                .eq(Withdrawal::getUserId, userId)
                .orderByDesc(Withdrawal::getCreatedAt)
        );
    }
}
