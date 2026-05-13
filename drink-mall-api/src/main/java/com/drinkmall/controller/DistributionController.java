package com.drinkmall.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.drinkmall.common.Result;
import com.drinkmall.dto.DistributionLevelOverviewResponse;
import com.drinkmall.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/distribution")
@RequiredArgsConstructor
public class DistributionController {

    private final UserService userService;

    @GetMapping("/level")
    @SaCheckLogin
    public Result<DistributionLevelOverviewResponse> getLevelOverview() {
        Long userId = StpUtil.getLoginIdAsLong();
        return Result.success(userService.getDistributionLevelOverview(userId));
    }
}
