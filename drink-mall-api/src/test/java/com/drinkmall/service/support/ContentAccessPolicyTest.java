package com.drinkmall.service.support;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ContentAccessPolicyTest {

    private final Map<String, Integer> orders = Map.of(
            "normal", 0,
            "promoter", 1,
            "county", 2
    );
    private final Map<String, String> names = Map.of(
            "normal", "普通会员",
            "promoter", "推客",
            "county", "县级联营商"
    );

    @Test
    void lowerLevelCannotViewPaidContentEvenIfPurchasedWhenBypassDisabled() {
        ContentAccessDecision decision = ContentAccessPolicy.evaluate(
                "normal",
                "promoter",
                true,
                new BigDecimal("19.90"),
                true,
                false,
                orders::get,
                names::get
        );

        assertThat(decision.isLevelAllowed()).isFalse();
        assertThat(decision.isCanView()).isFalse();
        assertThat(decision.isCanBuy()).isFalse();
        assertThat(decision.getLevelText()).isEqualTo("推客及以上");
        assertThat(decision.getLockReason()).contains("等级");
    }

    @Test
    void lowerLevelCanBuyAndViewPaidContentOnlyWhenConfigAllowsBypass() {
        ContentAccessDecision beforePurchase = ContentAccessPolicy.evaluate(
                "normal",
                "promoter",
                true,
                new BigDecimal("19.90"),
                false,
                true,
                orders::get,
                names::get
        );
        ContentAccessDecision afterPurchase = ContentAccessPolicy.evaluate(
                "normal",
                "promoter",
                true,
                new BigDecimal("19.90"),
                true,
                true,
                orders::get,
                names::get
        );

        assertThat(beforePurchase.isCanView()).isFalse();
        assertThat(beforePurchase.isCanBuy()).isTrue();
        assertThat(afterPurchase.isCanView()).isTrue();
        assertThat(afterPurchase.isCanBuy()).isFalse();
    }

    @Test
    void reachedLevelCanViewFreeContentAndBuyPaidContent() {
        ContentAccessDecision freeDecision = ContentAccessPolicy.evaluate(
                "county",
                "promoter",
                false,
                BigDecimal.ZERO,
                false,
                false,
                orders::get,
                names::get
        );
        ContentAccessDecision paidDecision = ContentAccessPolicy.evaluate(
                "promoter",
                "promoter",
                true,
                new BigDecimal("9.90"),
                false,
                false,
                orders::get,
                names::get
        );

        assertThat(freeDecision.isCanView()).isTrue();
        assertThat(freeDecision.isCanBuy()).isFalse();
        assertThat(paidDecision.isCanView()).isFalse();
        assertThat(paidDecision.isCanBuy()).isTrue();
    }
}
