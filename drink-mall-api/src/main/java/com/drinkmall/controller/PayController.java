package com.drinkmall.controller;

import com.drinkmall.common.BusinessException;
import com.drinkmall.config.WxPayProperties;
import com.drinkmall.payment.PayCallbackVerifier;
import com.drinkmall.service.OrderService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@RestController
@RequestMapping("/api/v1/pay")
@RequiredArgsConstructor
public class PayController {

    private final OrderService orderService;
    private final PayCallbackVerifier payCallbackVerifier;
    private final ObjectMapper objectMapper;
    private final WxPayProperties wxPayProperties;

    @PostMapping("/callback")
    public String handleWeChatPayCallback(@RequestBody String body, @RequestHeader HttpHeaders headers) {
        log.info("Received WeChat Pay callback");
        try {
            payCallbackVerifier.verify(body, headers);
            JsonNode root = objectMapper.readTree(body);
            root = businessPayload(root);
            if (!isSuccessTrade(root)) {
                log.warn("Ignore non-success WeChat Pay callback: {}", text(root, "trade_state"));
                return "SUCCESS";
            }
            orderService.confirmOnlinePaymentCallback(
                    text(root, "out_trade_no"),
                    paidAmount(root),
                    text(root, "transaction_id")
            );
            return "SUCCESS";
        } catch (BusinessException e) {
            log.warn("Rejected WeChat Pay callback: {}", e.getMessage());
            return "FAIL";
        } catch (Exception e) {
            log.warn("Failed to handle WeChat Pay callback", e);
            return "FAIL";
        }
    }

    private JsonNode businessPayload(JsonNode root) throws Exception {
        JsonNode resource = root.path("resource");
        if (resource.isMissingNode() || resource.isNull()) {
            return root;
        }
        String algorithm = text(resource, "algorithm");
        if (!"AEAD_AES_256_GCM".equals(algorithm)) {
            throw new BusinessException(400, "不支持的微信支付回调加密算法");
        }
        String apiV3Key = wxPayProperties.getApiV3Key();
        if (apiV3Key == null || apiV3Key.isBlank()) {
            throw new BusinessException(401, "微信支付回调解密配置缺失");
        }
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE,
                new SecretKeySpec(apiV3Key.getBytes(StandardCharsets.UTF_8), "AES"),
                new GCMParameterSpec(128, text(resource, "nonce").getBytes(StandardCharsets.UTF_8)));
        String associatedData = text(resource, "associated_data");
        if (associatedData != null) {
            cipher.updateAAD(associatedData.getBytes(StandardCharsets.UTF_8));
        }
        byte[] plain = cipher.doFinal(Base64.getDecoder().decode(text(resource, "ciphertext")));
        return objectMapper.readTree(new String(plain, StandardCharsets.UTF_8));
    }

    private boolean isSuccessTrade(JsonNode root) {
        String state = text(root, "trade_state");
        return state == null || state.isBlank() || "SUCCESS".equals(state);
    }

    private BigDecimal paidAmount(JsonNode root) {
        JsonNode amount = root.path("amount");
        if (amount.has("total")) {
            return BigDecimal.valueOf(amount.path("total").asLong()).movePointLeft(2).setScale(2);
        }
        if (root.has("total_fee")) {
            return BigDecimal.valueOf(root.path("total_fee").asLong()).movePointLeft(2).setScale(2);
        }
        if (root.has("paid_amount")) {
            return new BigDecimal(root.path("paid_amount").asText()).setScale(2);
        }
        throw new BusinessException(400, "微信支付回调金额缺失");
    }

    private String text(JsonNode root, String field) {
        JsonNode value = root.path(field);
        return value.isMissingNode() || value.isNull() ? null : value.asText();
    }
}
