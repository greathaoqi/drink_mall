package com.drinkmall.controller.admin;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drinkmall.common.Result;
import com.drinkmall.entity.PointsLog;
import com.drinkmall.entity.Withdrawal;
import com.drinkmall.service.admin.AdminFinanceService;
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
    public Result<Void> approveWithdrawal(@PathVariable Long withdrawalId) {
        adminFinanceService.approveWithdrawal(withdrawalId);
        return Result.success(null);
    }

    @PutMapping("/withdrawals/{withdrawalId}/reject")
    public Result<Void> rejectWithdrawal(@PathVariable Long withdrawalId, @RequestParam String reason) {
        adminFinanceService.rejectWithdrawal(withdrawalId, reason);
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

    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        return Result.success(adminFinanceService.getStatistics());
    }
}
