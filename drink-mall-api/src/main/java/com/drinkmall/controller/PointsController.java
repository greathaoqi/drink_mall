package com.drinkmall.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.drinkmall.common.Result;
import com.drinkmall.service.PointsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/points")
@RequiredArgsConstructor
public class PointsController {

    private final PointsService pointsService;

    @GetMapping("/balance")
    @SaCheckLogin
    public Result<Integer> getPointsBalance() {
        Long userId = StpUtil.getLoginIdAsLong();
        return Result.success(pointsService.getPointsBalance(userId));
    }

    @PostMapping("/redeem/{productId}")
    @SaCheckLogin
    public Result<Long> redeemProduct(@PathVariable Long productId) {
        Long userId = StpUtil.getLoginIdAsLong();
        return Result.success(pointsService.redeemProduct(userId, productId));
    }
}
