package com.drinkmall.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "wx.pay")
public class WxPayProperties {
    private String callbackVerifier = "wechat";
    private String mockSecret;
    private String platformPublicKey;
    private String apiV3Key;
}
