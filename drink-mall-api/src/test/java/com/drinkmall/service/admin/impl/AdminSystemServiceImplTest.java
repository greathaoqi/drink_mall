package com.drinkmall.service.admin.impl;

import com.drinkmall.entity.AdminUser;
import com.drinkmall.entity.OperationLog;
import com.drinkmall.entity.SysConfig;
import com.drinkmall.mapper.AdminUserMapper;
import com.drinkmall.mapper.OperationLogMapper;
import com.drinkmall.mapper.SysConfigMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminSystemServiceImplTest {

    @Mock private AdminUserMapper adminUserMapper;
    @Mock private OperationLogMapper operationLogMapper;
    @Mock private SysConfigMapper sysConfigMapper;

    private AdminSystemServiceImpl adminSystemService;

    @BeforeEach
    void setUp() {
        adminSystemService = new AdminSystemServiceImpl(adminUserMapper, operationLogMapper, sysConfigMapper);
    }

    @Test
    void createAdminUserWritesOperationLog() {
        AdminUser adminUser = new AdminUser();
        adminUser.setId(1L);
        adminUser.setUsername("operator");
        adminUser.setPasswordHash("plain");
        when(adminUserMapper.selectCount(any())).thenReturn(0L);

        adminSystemService.createAdminUser(adminUser);

        assertOperationLogged("admin_user", "create", "1");
    }

    @Test
    void updateConfigWritesOperationLogWithBeforeAndAfter() {
        SysConfig config = new SysConfig();
        config.setId(2L);
        config.setConfigKey("withdraw.min_amount");
        config.setConfigValue("10");
        when(sysConfigMapper.selectOne(any())).thenReturn(config);

        adminSystemService.updateConfig("withdraw.min_amount", "20", "finance rule adjustment");

        ArgumentCaptor<OperationLog> log = ArgumentCaptor.forClass(OperationLog.class);
        verify(operationLogMapper).insert(log.capture());
        assertThat(log.getValue().getModule()).isEqualTo("config");
        assertThat(log.getValue().getAction()).isEqualTo("update");
        assertThat(log.getValue().getTargetId()).isEqualTo("2");
        assertThat(log.getValue().getDetail()).contains("10 -> 20").contains("finance rule adjustment");
    }

    private void assertOperationLogged(String module, String action, String targetId) {
        ArgumentCaptor<OperationLog> log = ArgumentCaptor.forClass(OperationLog.class);
        verify(operationLogMapper).insert(log.capture());
        assertThat(log.getValue().getModule()).isEqualTo(module);
        assertThat(log.getValue().getAction()).isEqualTo(action);
        assertThat(log.getValue().getTargetId()).isEqualTo(targetId);
        assertThat(log.getValue().getCreatedAt()).isNotNull();
    }
}
