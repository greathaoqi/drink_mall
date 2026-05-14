package com.drinkmall.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drinkmall.common.Result;
import com.drinkmall.dto.DfTransferRequest;
import com.drinkmall.dto.WithdrawalRequest;
import com.drinkmall.entity.AssetLog;
import com.drinkmall.enums.AssetType;
import com.drinkmall.service.AssetService;
import com.drinkmall.service.WithdrawalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/asset")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;
    private final WithdrawalService withdrawalService;

    @GetMapping("/overview")
    @SaCheckLogin
    public Result<Map<String, Object>> overview() {
        return Result.success(assetService.getSummary(StpUtil.getLoginIdAsLong()));
    }

    @GetMapping("/records")
    @SaCheckLogin
    public Result<Page<AssetLog>> records(
            @RequestParam(required = false) String assetType,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        AssetType type = assetType == null || assetType.isBlank() ? null : AssetType.fromCode(assetType);
        return Result.success(assetService.getLogs(StpUtil.getLoginIdAsLong(), type, page, size));
    }

    @PostMapping("/withdraw")
    @SaCheckLogin
    public Result<Void> withdraw(@Valid @RequestBody WithdrawalRequest request) {
        withdrawalService.submit(StpUtil.getLoginIdAsLong(), request);
        return Result.success(null);
    }

    @PostMapping("/df/transfer")
    @SaCheckLogin
    public Result<Void> transferDf(@Valid @RequestBody DfTransferRequest request) {
        assetService.transferDf(StpUtil.getLoginIdAsLong(), request.getToUserId(), request.getAmount());
        return Result.success(null);
    }
}
