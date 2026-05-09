package com.drinkmall.service.admin.impl;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drinkmall.common.BusinessException;
import com.drinkmall.entity.AdminUser;
import com.drinkmall.entity.OperationLog;
import com.drinkmall.entity.SysConfig;
import com.drinkmall.mapper.AdminUserMapper;
import com.drinkmall.mapper.OperationLogMapper;
import com.drinkmall.mapper.SysConfigMapper;
import com.drinkmall.service.admin.AdminSystemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AdminSystemServiceImpl implements AdminSystemService {

    private final AdminUserMapper adminUserMapper;
    private final OperationLogMapper operationLogMapper;
    private final SysConfigMapper sysConfigMapper;

    @Override
    public Page<AdminUser> getAdminUsers(String keyword, Integer status, Integer page, Integer size) {
        Page<AdminUser> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<AdminUser> wrapper = new LambdaQueryWrapper<AdminUser>().orderByDesc(AdminUser::getCreatedAt);
        if (keyword != null) wrapper.like(AdminUser::getUsername, keyword);
        if (status != null) wrapper.eq(AdminUser::getStatus, status);
        return adminUserMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public AdminUser createAdminUser(AdminUser adminUser) {
        if (adminUserMapper.selectCount(new LambdaQueryWrapper<AdminUser>().eq(AdminUser::getUsername, adminUser.getUsername())) > 0) {
            throw new BusinessException(400, "用户名已存在");
        }
        adminUser.setPassword(SecureUtil.md5(adminUser.getPassword()));
        adminUser.setCreatedAt(LocalDateTime.now());
        adminUserMapper.insert(adminUser);
        return adminUser;
    }

    @Override
    public AdminUser updateAdminUser(AdminUser adminUser) {
        AdminUser existing = adminUserMapper.selectById(adminUser.getId());
        if (existing == null) throw new BusinessException(404, "用户不存在");
        if (adminUser.getPassword() != null && !adminUser.getPassword().isEmpty()) {
            adminUser.setPassword(SecureUtil.md5(adminUser.getPassword()));
        } else {
            adminUser.setPassword(existing.getPassword());
        }
        adminUser.setUpdatedAt(LocalDateTime.now());
        adminUserMapper.updateById(adminUser);
        return adminUser;
    }

    @Override
    public void deleteAdminUser(Long userId) {
        adminUserMapper.deleteById(userId);
    }

    @Override
    public void updateAdminUserStatus(Long userId, Integer status) {
        AdminUser adminUser = adminUserMapper.selectById(userId);
        if (adminUser == null) throw new BusinessException(404, "用户不存在");
        adminUser.setStatus(status);
        adminUser.setUpdatedAt(LocalDateTime.now());
        adminUserMapper.updateById(adminUser);
    }

    @Override
    public List<Map<String, Object>> getRoles() {
        List<Map<String, Object>> roles = new ArrayList<>();
        Map<String, Object> admin = new HashMap<>();
        admin.put("id", 1);
        admin.put("name", "管理员");
        roles.add(admin);
        Map<String, Object> operator = new HashMap<>();
        operator.put("id", 2);
        operator.put("name", "运营");
        roles.add(operator);
        return roles;
    }

    @Override
    public List<SysConfig> getConfigs() {
        return sysConfigMapper.selectList(null);
    }

    @Override
    public void updateConfig(String configKey, String configValue) {
        SysConfig config = sysConfigMapper.selectOne(new LambdaQueryWrapper<SysConfig>().eq(SysConfig::getConfigKey, configKey));
        if (config == null) {
            config = new SysConfig();
            config.setConfigKey(configKey);
            config.setConfigValue(configValue);
            config.setUpdatedAt(LocalDateTime.now());
            sysConfigMapper.insert(config);
        } else {
            config.setConfigValue(configValue);
            config.setUpdatedAt(LocalDateTime.now());
            sysConfigMapper.updateById(config);
        }
    }

    @Override
    public Page<OperationLog> getOperationLogs(Long userId, String module, String startDate, String endDate, Integer page, Integer size) {
        Page<OperationLog> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<OperationLog>().orderByDesc(OperationLog::getCreatedAt);
        if (userId != null) wrapper.eq(OperationLog::getAdminUserId, userId);
        if (module != null) wrapper.eq(OperationLog::getModule, module);
        if (startDate != null) wrapper.ge(OperationLog::getCreatedAt, LocalDateTime.parse(startDate + "T00:00:00"));
        if (endDate != null) wrapper.le(OperationLog::getCreatedAt, LocalDateTime.parse(endDate + "T23:59:59"));
        return operationLogMapper.selectPage(pageParam, wrapper);
    }
}