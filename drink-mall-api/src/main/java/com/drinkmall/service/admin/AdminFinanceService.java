package com.drinkmall.service.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drinkmall.entity.AssetLog;
import com.drinkmall.entity.PointsLog;
import com.drinkmall.entity.RewardRecord;
import com.drinkmall.entity.Withdrawal;
import com.drinkmall.enums.AssetType;
import java.util.Map;

public interface AdminFinanceService {
    Page<Withdrawal> getWithdrawals(String status, Integer page, Integer size);
    void approveWithdrawal(Long withdrawalId, Long adminId, String offlineTransferNo);
    void rejectWithdrawal(Long withdrawalId, Long adminId, String reason);
    Page<Map<String, Object>> getBalanceLogs(Long userId, Integer page, Integer size);
    Page<PointsLog> getPointsLogs(Long userId, Integer page, Integer size);
    Page<AssetLog> getAssetLogs(Long userId, AssetType assetType, Integer page, Integer size);
    Page<RewardRecord> getRewardRecords(Long userId, String status, Integer page, Integer size);
    void adjustAsset(Long adminId, Long userId, AssetType assetType, java.math.BigDecimal amount, String reason);
    Map<String, Object> getStatistics();
}
