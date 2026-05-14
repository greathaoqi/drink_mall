package com.drinkmall.controller.admin;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drinkmall.common.Result;
import com.drinkmall.dto.AssetAdjustRequest;
import com.drinkmall.entity.AssetLog;
import com.drinkmall.entity.PointsLog;
import com.drinkmall.entity.RewardRecord;
import com.drinkmall.entity.Withdrawal;
import com.drinkmall.enums.AssetType;
import com.drinkmall.service.admin.AdminFinanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/finance")
@RequiredArgsConstructor
@SaCheckRole("admin")
public class AdminFinanceController {

    private final AdminFinanceService adminFinanceService;

    @GetMapping("/withdrawals")
    public Result<Page<Withdrawal>> getWithdrawals(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        return Result.success(adminFinanceService.getWithdrawals(status, page, size));
    }

    @PutMapping("/withdrawals/{withdrawalId}/approve")
    public Result<Void> approveWithdrawal(
            @PathVariable Long withdrawalId,
            @RequestParam(required = false) String offlineTransferNo) {
        adminFinanceService.approveWithdrawal(withdrawalId, StpUtil.getLoginIdAsLong(), offlineTransferNo);
        return Result.success(null);
    }

    @PutMapping("/withdrawals/{withdrawalId}/reject")
    public Result<Void> rejectWithdrawal(@PathVariable Long withdrawalId, @RequestParam String reason) {
        adminFinanceService.rejectWithdrawal(withdrawalId, StpUtil.getLoginIdAsLong(), reason);
        return Result.success(null);
    }

    @GetMapping("/balance-logs")
    public Result<Page<Map<String, Object>>> getBalanceLogs(
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        return Result.success(adminFinanceService.getBalanceLogs(userId, page, size));
    }

    @GetMapping("/points-logs")
    public Result<Page<PointsLog>> getPointsLogs(
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        return Result.success(adminFinanceService.getPointsLogs(userId, page, size));
    }

    @GetMapping("/asset-logs")
    public Result<Page<AssetLog>> getAssetLogs(
            @RequestParam Long userId,
            @RequestParam(required = false) String assetType,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        AssetType type = assetType == null || assetType.isBlank() ? null : AssetType.fromCode(assetType);
        return Result.success(adminFinanceService.getAssetLogs(userId, type, page, size));
    }

    @PostMapping("/assets/adjust")
    public Result<Void> adjustAsset(@Valid @RequestBody AssetAdjustRequest request) {
        adminFinanceService.adjustAsset(
                StpUtil.getLoginIdAsLong(),
                request.getUserId(),
                AssetType.fromCode(request.getAssetType()),
                request.getAmount(),
                request.getReason()
        );
        return Result.success(null);
    }

    @GetMapping("/reward-records")
    public Result<Page<RewardRecord>> getRewardRecords(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        return Result.success(adminFinanceService.getRewardRecords(userId, status, page, size));
    }

    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        return Result.success(adminFinanceService.getStatistics());
    }
}
