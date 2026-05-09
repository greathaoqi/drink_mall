package com.drinkmall.controller.admin;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drinkmall.common.Result;
import com.drinkmall.entity.User;
import com.drinkmall.service.admin.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/user")
@RequiredArgsConstructor
@SaCheckRole("admin")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping("/list")
    public Result<Page<User>> getUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer ageVerified,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        return Result.success(adminUserService.getUsers(keyword, ageVerified, page, size));
    }

    @GetMapping("/{userId}")
    public Result<Map<String, Object>> getUserDetail(@PathVariable Long userId) {
        return Result.success(adminUserService.getUserDetail(userId));
    }

    @GetMapping("/export")
    public void exportUsers(HttpServletResponse response, @RequestParam(required = false) String keyword) {
        adminUserService.exportUsers(response, keyword);
    }
}
