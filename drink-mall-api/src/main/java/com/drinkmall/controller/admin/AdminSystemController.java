package com.drinkmall.controller.admin;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drinkmall.common.Result;
import com.drinkmall.entity.AdminUser;
import com.drinkmall.entity.OperationLog;
import com.drinkmall.entity.SysConfig;
import com.drinkmall.service.admin.AdminSystemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/system")
@RequiredArgsConstructor
@SaCheckRole("admin")
public class AdminSystemController {

    private final AdminSystemService adminSystemService;

    @GetMapping("/users")
    public Result<Page<AdminUser>> getAdminUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        return Result.success(adminSystemService.getAdminUsers(keyword, status, page, size));
    }

    @PostMapping("/users")
    public Result<AdminUser> createAdminUser(@RequestBody AdminUser adminUser) {
        return Result.success(adminSystemService.createAdminUser(adminUser));
    }

    @PutMapping("/users/{userId}")
    public Result<AdminUser> updateAdminUser(@PathVariable Long userId, @RequestBody AdminUser adminUser) {
        adminUser.setId(userId);
        return Result.success(adminSystemService.updateAdminUser(adminUser));
    }

    @DeleteMapping("/users/{userId}")
    public Result<Void> deleteAdminUser(@PathVariable Long userId) {
        adminSystemService.deleteAdminUser(userId);
        return Result.success(null);
    }

    @PutMapping("/users/{userId}/status")
    public Result<Void> updateAdminUserStatus(@PathVariable Long userId, @RequestParam Integer status) {
        adminSystemService.updateAdminUserStatus(userId, status);
        return Result.success(null);
    }

    @GetMapping("/roles")
    public Result<List<Map<String, Object>>> getRoles() {
        return Result.success(adminSystemService.getRoles());
    }

    @GetMapping("/configs")
    public Result<List<SysConfig>> getConfigs() {
        return Result.success(adminSystemService.getConfigs());
    }

    @PutMapping("/configs/{configKey}")
    public Result<Void> updateConfig(@PathVariable String configKey, @RequestParam String configValue) {
        adminSystemService.updateConfig(configKey, configValue);
        return Result.success(null);
    }

    @GetMapping("/logs")
    public Result<Page<OperationLog>> getOperationLogs(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        return Result.success(adminSystemService.getOperationLogs(userId, module, startDate, endDate, page, size));
    }
}
