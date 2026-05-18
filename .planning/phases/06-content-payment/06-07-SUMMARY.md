---
phase: 06
plan: 07
subsystem: admin-content-analytics
tags: [admin, analytics, content-purchase, dashboard]
requires: [06-01, 06-02]
provides: [admin-purchase-records, admin-analytics-dashboard]
affects: [AdminContentController, AdminContentService]
tech-stack:
  added:
    - ContentPurchaseRecordResponse DTO
    - ContentAnalyticsResponse DTO
  patterns:
    - MyBatis-Plus IPage pagination
    - Stream API for aggregation
key-files:
  created:
    - drink-mall-api/src/main/java/com/drinkmall/dto/ContentPurchaseRecordResponse.java
    - drink-mall-api/src/main/java/com/drinkmall/dto/ContentAnalyticsResponse.java
  modified:
    - drink-mall-api/src/main/java/com/drinkmall/service/admin/AdminContentService.java
    - drink-mall-api/src/main/java/com/drinkmall/service/admin/impl/AdminContentServiceImpl.java
    - drink-mall-api/src/main/java/com/drinkmall/controller/admin/AdminContentController.java
decisions:
  - Use IPage instead of Page for DTO return type (MyBatis-Plus convert returns IPage)
  - Resolve content titles dynamically from Video/HelpArticle tables
  - Use Java Stream API for analytics aggregation (no complex SQL)
metrics:
  duration: ~10 minutes
  completed: 2026-05-18
  tasks: 5
  files: 5
---

# Phase 6 Plan 07: Admin Purchase Records and Analytics Summary

Admin endpoints for viewing content purchase history and analytics/revenue reports.

## One-Liner

Admin purchase records with pagination and analytics dashboard including total revenue, breakdown by content type, top content, and 30-day daily trends.

## Completed Tasks

| Task | Name | Commit | Files |
| ---- | ---- | ------ | ----- |
| 01 | Create ContentPurchaseRecordResponse DTO | 7d59863 | ContentPurchaseRecordResponse.java |
| 02 | Create ContentAnalyticsResponse DTO | 569a53d | ContentAnalyticsResponse.java |
| 03 | Add methods to AdminContentService | f677dac | AdminContentService.java |
| 04 | Implement purchase/analytics service | 3984aaf | AdminContentServiceImpl.java |
| 05 | Add controller endpoints | 4e2abfa | AdminContentController.java |

## Implementation Details

### ContentPurchaseRecordResponse DTO

Response DTO for paginated purchase record list with:
- User info: userId, userNickname, userPhone
- Content info: contentType, contentId, contentTitle
- Payment details: price, paymentMethod, status, orderNo, paymentNo
- Timestamps: paymentTime, createdAt

### ContentAnalyticsResponse DTO

Response DTO for analytics dashboard with:
- Total metrics: totalRevenue, totalPurchases, totalUsers
- By type breakdown: revenueByType, purchasesByType (Map)
- Top content: topContent (List of TopContent with contentId, title, purchaseCount, revenue)
- Daily trends: dailyStats (List of DailyStats for last 30 days)

### Service Implementation

**getPurchaseRecords:**
- Filters by contentType, status, userId
- Pagination with configurable page/size
- Resolves user nickname/phone from User table
- Resolves content title from Video/HelpArticle tables based on contentType

**getAnalytics:**
- Total revenue from all paid purchases
- Unique user count (distinct userIds)
- Revenue and purchases grouped by contentType
- Top 10 content by purchase count with resolved titles
- Daily stats for last 30 days with date, purchaseCount, revenue

### Controller Endpoints

| Endpoint | Method | Description |
| -------- | ------ | ----------- |
| `/api/v1/admin/content/purchases` | GET | Paginated purchase records |
| `/api/v1/admin/content/analytics` | GET | Analytics dashboard data |

**Filters supported:**
- contentType: Filter by "video" or "article"
- status: Filter by payment status
- userId: Filter by specific user

## Deviations from Plan

None - plan executed exactly as written.

## Verification Results

- mvn compile passed successfully
- All 5 commits created with proper format

## Self-Check

| Item | Status |
| ---- | ------ |
| ContentPurchaseRecordResponse.java exists | PASSED |
| ContentAnalyticsResponse.java exists | PASSED |
| AdminContentService has getPurchaseRecords method | PASSED |
| AdminContentService has getAnalytics method | PASSED |
| AdminContentController has /purchases endpoint | PASSED |
| AdminContentController has /analytics endpoint | PASSED |
| Commit 7d59863 exists | PASSED |
| Commit 569a53d exists | PASSED |
| Commit f677dac exists | PASSED |
| Commit 3984aaf exists | PASSED |
| Commit 4e2abfa exists | PASSED |

## Self-Check: PASSED