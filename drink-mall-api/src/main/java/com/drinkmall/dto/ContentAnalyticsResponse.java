package com.drinkmall.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Response DTO for admin analytics dashboard.
 * Per D-ANALYTICS-01: total revenue, by type, top content, over time.
 */
@Data
@Builder
public class ContentAnalyticsResponse {
    // Total metrics
    private BigDecimal totalRevenue;
    private Integer totalPurchases;
    private Integer totalUsers;

    // Revenue by content type
    private Map<String, BigDecimal> revenueByType;
    private Map<String, Integer> purchasesByType;

    // Top purchased content
    private List<TopContent> topContent;

    // Daily trend (last 30 days)
    private List<DailyStats> dailyStats;

    @Data
    @Builder
    public static class TopContent {
        private Long contentId;
        private String contentType;
        private String title;
        private Integer purchaseCount;
        private BigDecimal revenue;
    }

    @Data
    @Builder
    public static class DailyStats {
        private String date;
        private Integer purchaseCount;
        private BigDecimal revenue;
    }
}
