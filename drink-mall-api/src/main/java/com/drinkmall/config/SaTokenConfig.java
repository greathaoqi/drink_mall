package com.drinkmall.config;

import cn.dev33.satoken.stp.StpInterface;
import com.drinkmall.entity.AdminUser;
import com.drinkmall.mapper.AdminUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SaTokenConfig {

    private final AdminUserMapper adminUserMapper;

    @Bean
    public StpInterface stpInterface() {
        return new StpInterface() {
            @Override
            public List<String> getPermissionList(Object loginId, String loginType) {
                return Collections.emptyList();
            }

            @Override
            public List<String> getRoleList(Object loginId, String loginType) {
                String loginIdText = String.valueOf(loginId);
                if (!loginIdText.startsWith("admin:")) {
                    return Collections.emptyList();
                }
                AdminUser adminUser = adminUserMapper.selectById(Long.valueOf(loginIdText.substring("admin:".length())));
                if (adminUser == null || adminUser.getRole() == null) {
                    return Collections.emptyList();
                }
                return List.of(adminUser.getRole());
            }
        };
    }
}
