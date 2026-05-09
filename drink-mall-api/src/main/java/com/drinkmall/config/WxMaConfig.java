package com.drinkmall.config;

import me.chanjar.weixin.ma.api.WxMaService;
import me.chanjar.weixin.ma.api.impl.WxMaServiceImpl;
import me.chanjar.weixin.ma.config.impl.WxMaDefaultConfigImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WxMaConfig {

    @Bean
    public WxMaService wxMaService(WxMaProperties properties) {
        WxMaDefaultConfigImpl config = new WxMaDefaultConfigImpl();
        config.setAppid(properties.getAppid());
        config.setSecret(properties.getSecret());
        WxMaService service = new WxMaServiceImpl();
        service.setWxMaConfig(config);
        return service;
    }
}
