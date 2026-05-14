package com.drinkmall.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.drinkmall.common.Result;
import com.drinkmall.dto.SystemConfigResponse;
import com.drinkmall.entity.SysConfig;
import com.drinkmall.mapper.SysConfigMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/config")
@RequiredArgsConstructor
public class ConfigController {

    private final SysConfigMapper sysConfigMapper;

    @GetMapping("/system")
    public Result<SystemConfigResponse> getSystemConfig() {
        return Result.success(SystemConfigResponse.fromConfigs(sysConfigMapper.selectList(new LambdaQueryWrapper<SysConfig>())));
    }

    @GetMapping("/customer-service")
    public Result<Map<String, String>> getCustomerService() {
        Map<String, String> config = new LinkedHashMap<>();
        put(config, "phone", "customer_service.phone");
        put(config, "wechat", "customer_service.wechat");
        put(config, "workTime", "customer_service.work_time");
        return Result.success(config);
    }

    private void put(Map<String, String> target, String field, String key) {
        SysConfig config = sysConfigMapper.selectOne(new LambdaQueryWrapper<SysConfig>().eq(SysConfig::getConfigKey, key));
        target.put(field, config == null ? "" : config.getConfigValue());
    }
}
