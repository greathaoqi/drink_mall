package com.drinkmall.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drinkmall.common.Result;
import com.drinkmall.dto.MemberCenterResponse;
import com.drinkmall.dto.UpdateProfileRequest;
import com.drinkmall.dto.UserInfoResponse;
import com.drinkmall.dto.WithdrawalRequest;
import com.drinkmall.dto.DfExchangeRequest;
import com.drinkmall.dto.DfTransferRequest;
import com.drinkmall.entity.AssetLog;
import com.drinkmall.entity.BalanceLog;
import com.drinkmall.entity.PointsLog;
import com.drinkmall.entity.Withdrawal;
import com.drinkmall.enums.AssetType;
import com.drinkmall.service.AssetService;
import com.drinkmall.service.UserService;
import com.drinkmall.service.WithdrawalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AssetService assetService;
    private final WithdrawalService withdrawalService;

    @GetMapping("/info")
    @SaCheckLogin
    public Result<UserInfoResponse> getUserInfo() {
        Long userId = StpUtil.getLoginIdAsLong();
        UserInfoResponse userInfo = userService.getUserInfo(userId);
        return Result.success(userInfo);
    }

    @GetMapping("/member-center")
    @SaCheckLogin
    public Result<MemberCenterResponse> getMemberCenter() {
        Long userId = StpUtil.getLoginIdAsLong();
        return Result.success(userService.getMemberCenter(userId));
    }

    @PostMapping("/verify-age")
    @SaCheckLogin
    public Result<Void> verifyAge() {
        Long userId = StpUtil.getLoginIdAsLong();
        userService.verifyAge(userId);
        return Result.success(null);
    }

    @PutMapping("/profile")
    @SaCheckLogin
    public Result<Void> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        Long userId = StpUtil.getLoginIdAsLong();
        userService.updateProfile(userId, request.getNickname());
        return Result.success(null);
    }

    @PostMapping("/withdrawal")
    @SaCheckLogin
    public Result<Void> applyWithdrawal(@Valid @RequestBody WithdrawalRequest request) {
        Long userId = StpUtil.getLoginIdAsLong();
        withdrawalService.submit(userId, request);
        return Result.success(null);
    }

    @GetMapping("/assets/summary")
    @SaCheckLogin
    public Result<Map<String, Object>> getAssetSummary() {
        Long userId = StpUtil.getLoginIdAsLong();
        return Result.success(assetService.getSummary(userId));
    }

    @GetMapping("/assets/logs")
    @SaCheckLogin
    public Result<Page<AssetLog>> getAssetLogs(
            @RequestParam(required = false) String assetType,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        Long userId = StpUtil.getLoginIdAsLong();
        AssetType type = assetType == null || assetType.isBlank() ? null : AssetType.fromCode(assetType);
        return Result.success(assetService.getLogs(userId, type, page, size));
    }

    @PostMapping("/assets/df/exchange-wine-bean")
    @SaCheckLogin
    public Result<Void> exchangeDfToWineBean(@Valid @RequestBody DfExchangeRequest request) {
        Long userId = StpUtil.getLoginIdAsLong();
        assetService.exchangeDfToWineBean(userId, request.getAmount());
        return Result.success(null);
    }

    @PostMapping("/assets/df/transfer")
    @SaCheckLogin
    public Result<Void> transferDf(@Valid @RequestBody DfTransferRequest request) {
        Long userId = StpUtil.getLoginIdAsLong();
        assetService.transferDf(userId, request.getToUserId(), request.getAmount());
        return Result.success(null);
    }

    @GetMapping("/balance-logs")
    @SaCheckLogin
    public Result<Page<BalanceLog>> getBalanceLogs(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        Long userId = StpUtil.getLoginIdAsLong();
        return Result.success(userService.getBalanceLogs(userId, page, size));
    }

    @GetMapping("/points-logs")
    @SaCheckLogin
    public Result<Page<PointsLog>> getPointsLogs(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        Long userId = StpUtil.getLoginIdAsLong();
        return Result.success(userService.getPointsLogs(userId, page, size));
    }

    @GetMapping("/withdrawals")
    @SaCheckLogin
    public Result<Page<Withdrawal>> getUserWithdrawals(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        Long userId = StpUtil.getLoginIdAsLong();
        return Result.success(withdrawalService.listUser(userId, page, size));
    }
}
