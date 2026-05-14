package com.drinkmall.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.drinkmall.common.Result;
import com.drinkmall.dto.RealNameStatusResponse;
import com.drinkmall.dto.RealNameSubmitRequest;
import com.drinkmall.service.RealNameService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/real-name")
@RequiredArgsConstructor
public class RealNameController {

    private final RealNameService realNameService;

    @PostMapping
    @SaCheckLogin
    public Result<RealNameStatusResponse> submit(@Valid @RequestBody RealNameSubmitRequest request) {
        Long userId = StpUtil.getLoginIdAsLong();
        return Result.success(RealNameStatusResponse.fromEntity(realNameService.submit(userId, request)));
    }

    @GetMapping("/status")
    @SaCheckLogin
    public Result<RealNameStatusResponse> status() {
        Long userId = StpUtil.getLoginIdAsLong();
        return Result.success(RealNameStatusResponse.fromEntity(realNameService.getStatus(userId)));
    }
}
