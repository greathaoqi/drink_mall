package com.drinkmall.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.drinkmall.common.Result;
import com.drinkmall.dto.LoginRequest;
import com.drinkmall.dto.LoginResponse;
import com.drinkmall.dto.UserInfoResponse;
import com.drinkmall.service.AuthService;
import com.drinkmall.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return Result.success(response);
    }

    @PostMapping("/demo-login")
    public Result<LoginResponse> demoLogin() {
        return Result.success(authService.demoLogin());
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.success(null);
    }

    @GetMapping("/check")
    public Result<UserInfoResponse> checkAuth() {
        if (!authService.isLoggedIn()) {
            return Result.success(null);
        }
        Long userId = StpUtil.getLoginIdAsLong();
        UserInfoResponse userInfo = userService.getUserInfo(userId);
        return Result.success(userInfo);
    }
}
