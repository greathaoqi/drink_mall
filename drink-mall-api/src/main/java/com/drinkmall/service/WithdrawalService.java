package com.drinkmall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drinkmall.dto.WithdrawalRequest;
import com.drinkmall.entity.Withdrawal;

public interface WithdrawalService {
    void submit(Long userId, WithdrawalRequest request);

    Page<Withdrawal> listUser(Long userId, Integer page, Integer size);

    Page<Withdrawal> listAdmin(String status, Integer page, Integer size);

    void approve(Long withdrawalId, Long adminId, String offlineTransferNo);

    void reject(Long withdrawalId, Long adminId, String reason);
}
