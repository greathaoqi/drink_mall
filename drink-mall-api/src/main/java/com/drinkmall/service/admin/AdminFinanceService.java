package com.drinkmall.service.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drinkmall.entity.PointsLog;
import com.drinkmall.entity.Withdrawal;
import java.util.Map;

public interface AdminFinanceService {
    Page<Withdrawal> getWithdrawals(String status, Integer page, Integer size);
    void approveWithdrawal(Long withdrawalId);
    void rejectWithdrawal(Long withdrawalId, String reason);
    Page<Map<String, Object>> getBalanceLogs(Long userId, Integer page, Integer size);
    Page<PointsLog> getPointsLogs(Long userId, Integer page, Integer size);
    Map<String, Object> getStatistics();
}
