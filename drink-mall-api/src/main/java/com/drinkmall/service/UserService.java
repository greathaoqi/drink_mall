package com.drinkmall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drinkmall.dto.UserInfoResponse;
import com.drinkmall.dto.WithdrawalRequest;
import com.drinkmall.entity.BalanceLog;
import com.drinkmall.entity.PointsLog;
import com.drinkmall.entity.User;
import com.drinkmall.entity.Withdrawal;

public interface UserService {
    User getById(Long userId);
    User findByOpenid(String openid);
    void verifyAge(Long userId);
    UserInfoResponse getUserInfo(Long userId);
    void updateProfile(Long userId, String nickname);
    void applyWithdrawal(Long userId, WithdrawalRequest request);
    Page<BalanceLog> getBalanceLogs(Long userId, Integer page, Integer size);
    Page<PointsLog> getPointsLogs(Long userId, Integer page, Integer size);
    Page<Withdrawal> getUserWithdrawals(Long userId, Integer page, Integer size);
}
