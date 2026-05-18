package com.drinkmall;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExternalDemoImageContractTest {

    @Test
    void currentDemoDataDoesNotUseUnstableVantImageHost() throws IOException {
        String demoFlow = read("src/main/resources/db/migration/V21__Complete_Mini_Demo_Flow.sql");
        String authService = read("src/main/java/com/drinkmall/service/impl/AuthServiceImpl.java");
        String repairMigration = read("src/main/resources/db/migration/V23__Replace_External_Demo_Images.sql");

        assertFalse(demoFlow.contains("img.yzcdn.cn/vant"), "V21 demo data should use bundled miniapp image paths");
        assertFalse(authService.contains("img.yzcdn.cn/vant"), "Demo login fallback avatar should use bundled miniapp image paths");
        assertTrue(repairMigration.contains("img.yzcdn.cn/vant"), "Repair migration should target already-seeded external URLs");
        assertTrue(repairMigration.contains("/static/images/zone-gift.png"));
    }

    private static String read(String path) throws IOException {
        return Files.readString(Path.of(path), StandardCharsets.UTF_8);
    }
}
