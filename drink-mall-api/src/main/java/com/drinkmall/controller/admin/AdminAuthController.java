package com.drinkmall.controller.admin;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.drinkmall.common.Result;
import com.drinkmall.entity.AdminUser;
import com.drinkmall.mapper.AdminUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminUserMapper adminUserMapper;

    @PostMapping("/login")
    public Result<AdminUser> login(@RequestBody AdminUser loginRequest) {
        AdminUser adminUser = adminUserMapper.selectOne(
            new LambdaQueryWrapper<AdminUser>().eq(AdminUser::getUsername, loginRequest.getUsername())
        );
        if (adminUser == null || !adminUser.getPassword().equals(SecureUtil.md5(loginRequest.getPassword()))) {
            return Result.error(401, "用户名或密码错误");
        }
        if (adminUser.getStatus() != 1) {
            return Result.error(403, "账号已被禁用");
        }
        StpUtil.login(adminUser.getId());
        StpUtil.getSession().set("adminUser", adminUser);
        return Result.success(adminUser);
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        StpUtil.logout();
        return Result.success(null);
    }

    @GetMapping("/info")
    @SaCheckRole("admin")
    public Result<AdminUser> getInfo() {
        Long userId = StpUtil.getLoginIdAsLong();
        AdminUser adminUser = adminUserMapper.selectById(userId);
        return Result.success(adminUser);
    }
}