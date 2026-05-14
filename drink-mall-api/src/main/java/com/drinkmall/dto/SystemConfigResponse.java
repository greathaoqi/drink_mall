package com.drinkmall.dto;

import com.drinkmall.entity.SysConfig;
import lombok.Builder;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class SystemConfigResponse {
    private Boolean showOption;
    private Map<String, String> configs;

    public static SystemConfigResponse fromConfigs(List<SysConfig> configList) {
        Map<String, String> configs = new LinkedHashMap<>();
        for (SysConfig config : configList) {
            configs.put(config.getConfigKey(), config.getConfigValue());
        }
        return SystemConfigResponse.builder()
                .showOption(Boolean.parseBoolean(configs.getOrDefault("asset.option.visible_in_mini", "false")))
                .configs(configs)
                .build();
    }
}
