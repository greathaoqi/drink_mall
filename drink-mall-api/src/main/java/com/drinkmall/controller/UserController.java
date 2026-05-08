package com.drinkmall.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.drinkmall.common.Result;
import com.drinkmall.dto.UserInfoResponse;
import com.drinkmall.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/info")
    @SaCheckLogin
    public Result<UserInfoResponse> getUserInfo() {
        Long userId = StpUtil.getLoginIdAsLong();
        UserInfoResponse userInfo = userService.getUserInfo(userId);
        return Result.success(userInfo);
    }

    @PostMapping("/verify-age")
    @SaCheckLogin
    public Result<Void> verifyAge() {
        Long userId = StpUtil.getLoginIdAsLong();
        userService.verifyAge(userId);
        return Result.success(null);
    }
}
