package com.drinkmall.payment;

import com.drinkmall.common.BusinessException;
import com.drinkmall.config.WxPayProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class DefaultPayCallbackVerifier implements PayCallbackVerifier {

    private static final String MODE_MOCK = "mock";
    private final WxPayProperties properties;

    @Override
    public void verify(String body, HttpHeaders headers) {
        if (MODE_MOCK.equalsIgnoreCase(properties.getCallbackVerifier())) {
            verifyMockSignatureIfConfigured(body, headers);
            return;
        }
        verifyWechatPaySignature(body, headers);
    }

    private void verifyMockSignatureIfConfigured(String body, HttpHeaders headers) {
        String secret = properties.getMockSecret();
        if (secret == null || secret.isBlank()) {
            return;
        }
        String actual = headers.getFirst("X-Mock-Pay-Signature");
        String expected = hmacSha256(body, secret);
        if (!expected.equals(actual)) {
            throw new BusinessException(401, "微信支付回调 mock 签名无效");
        }
    }

    private void verifyWechatPaySignature(String body, HttpHeaders headers) {
        String timestamp = headers.getFirst("Wechatpay-Timestamp");
        String nonce = headers.getFirst("Wechatpay-Nonce");
        String signature = headers.getFirst("Wechatpay-Signature");
        String publicKey = properties.getPlatformPublicKey();
        if (isBlank(timestamp) || isBlank(nonce) || isBlank(signature) || isBlank(publicKey)
                || isBlank(properties.getApiV3Key())) {
            throw new BusinessException(401, "微信支付回调验签配置缺失，已拒绝真实回调");
        }
        try {
            String message = timestamp + "\n" + nonce + "\n" + body + "\n";
            Signature verifier = Signature.getInstance("SHA256withRSA");
            verifier.initVerify(KeyFactory.getInstance("RSA").generatePublic(toKeySpec(publicKey)));
            verifier.update(message.getBytes(StandardCharsets.UTF_8));
            if (!verifier.verify(Base64.getDecoder().decode(signature))) {
                throw new BusinessException(401, "微信支付回调签名无效");
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(401, "微信支付回调验签失败");
        }
    }

    private X509EncodedKeySpec toKeySpec(String pem) {
        String normalized = pem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        return new X509EncodedKeySpec(Base64.getDecoder().decode(normalized));
    }

    private String hmacSha256(String body, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return Base64.getEncoder().encodeToString(mac.doFinal(body.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new BusinessException(500, "微信支付 mock 签名计算失败");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
