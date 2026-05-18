package com.drinkmall.service.support;

import java.math.BigDecimal;
import java.util.function.Function;

public final class ContentAccessPolicy {

    private ContentAccessPolicy() {
    }

    public static ContentAccessDecision evaluate(
            String userLevel,
            String watchLevel,
            boolean paidContent,
            BigDecimal price,
            boolean purchased,
            boolean allowBelowLevelPurchase,
            Function<String, Integer> levelOrder,
            Function<String, String> levelName) {
        String requiredLevel = normalizeLevel(watchLevel);
        String currentLevel = normalizeLevel(userLevel);
        boolean levelAllowed = orderOf(currentLevel, levelOrder) >= orderOf(requiredLevel, levelOrder);
        boolean paymentSatisfied = !paidContent || purchased;
        boolean canView = (levelAllowed && paymentSatisfied)
                || (!levelAllowed && allowBelowLevelPurchase && paidContent && purchased);
        boolean canBuy = paidContent && !purchased && (levelAllowed || allowBelowLevelPurchase);

        return ContentAccessDecision.builder()
                .levelAllowed(levelAllowed)
                .canView(canView)
                .canBuy(canBuy)
                .watchLevel(requiredLevel)
                .levelText(displayLevel(requiredLevel, levelName))
                .lockReason(lockReason(levelAllowed, paymentSatisfied, allowBelowLevelPurchase, paidContent))
                .build();
    }

    private static String normalizeLevel(String level) {
        return level == null || level.isBlank() ? "normal" : level.trim();
    }

    private static int orderOf(String level, Function<String, Integer> levelOrder) {
        Integer order = levelOrder.apply(level);
        return order == null ? 0 : order;
    }

    private static String displayLevel(String level, Function<String, String> levelName) {
        String name = levelName.apply(level);
        return (name == null || name.isBlank() ? level : name) + "及以上";
    }

    private static String lockReason(boolean levelAllowed, boolean paymentSatisfied, boolean allowBelowLevelPurchase, boolean paidContent) {
        if (!levelAllowed && paidContent && !allowBelowLevelPurchase) {
            return "当前等级不足，且业务配置不允许通过购买绕过等级权限";
        }
        if (!levelAllowed) {
            return "当前等级不足";
        }
        if (!paymentSatisfied) {
            return "内容尚未购买";
        }
        return "";
    }
}
