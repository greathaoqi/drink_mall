package com.drinkmall.migration;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class DemoFlowSeedMigrationTest {

    @Test
    void migrationSeedsCompleteMiniProgramDemoFlow() throws Exception {
        Path migration = Path.of("src/main/resources/db/migration/V21__Complete_Mini_Demo_Flow.sql");
        assertThat(migration).exists();

        String sql = Files.readString(migration, StandardCharsets.UTF_8);
        assertThat(sql)
                .contains("demo-openid")
                .contains("DEMO1001")
                .contains("DEMO-SEED")
                .contains("DEMO-DIRECT-1")
                .contains("DEMO-INDIRECT-1")
                .contains("演示主产品")
                .contains("演示招商升级礼包")
                .contains("积分兑换")
                .contains("investment_level_code")
                .contains("allowed_payment_methods")
                .contains("real_name_status")
                .contains("main_zone_paid_amount")
                .contains("team_performance");
    }
}
