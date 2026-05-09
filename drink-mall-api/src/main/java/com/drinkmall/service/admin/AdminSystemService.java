package com.drinkmall.service.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drinkmall.entity.AdminUser;
import com.drinkmall.entity.OperationLog;
import com.drinkmall.entity.SysConfig;
import java.util.List;
import java.util.Map;

public interface AdminSystemService {
    Page<AdminUser> getAdminUsers(String keyword, Integer status, Integer page, Integer size);
    AdminUser createAdminUser(AdminUser adminUser);
    AdminUser updateAdminUser(AdminUser adminUser);
    void deleteAdminUser(Long userId);
    void updateAdminUserStatus(Long userId, Integer status);
    List<Map<String, Object>> getRoles();
    List<SysConfig> getConfigs();
    void updateConfig(String configKey, String configValue);
    Page<OperationLog> getOperationLogs(Long userId, String module, String startDate, String endDate, Integer page, Integer size);
}
