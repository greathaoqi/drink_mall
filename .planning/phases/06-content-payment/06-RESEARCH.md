# Phase 6: Content Payment & Monetization - Research

**Researched:** 2026-05-18
**Domain:** WeChat Pay Content Monetization, Search, Categories, Likes, Analytics
**Confidence:** HIGH

## Summary

This phase implements real WeChat Pay integration for content purchases, extending the existing PayController callback infrastructure with a CP-prefix order dispatch system. The content_purchases table already exists with all required fields. The ContentController.buy() method currently handles mock online payments - it needs to be extended to generate real WeChat Pay prepay orders. Additional features include keyword search, category management, like functionality, preview teasers, and admin analytics.

**Primary recommendation:** Extend PayController.callback() to dispatch by order prefix (CP vs product orders), implement ContentPurchaseService for payment flow, add content_likes and content_categories tables, and create admin analytics endpoints following existing AdminFinanceController patterns.

<user_constraints>
## User Constraints (from CONTEXT.md)

### Locked Decisions

**Content Payment Flow:**
- D-CPAY-01: Same callback endpoint as product orders, dispatch by order number prefix (CP prefix). Reuse existing PayController WeChat Pay decryption and signature verification.
- D-CPAY-02: Use content_purchases table with status='pending' for pending orders. No new table needed.
- D-CPAY-03: Content purchase order numbers use CPYYYYMMDDnnnnn format (CP prefix).
- D-CPAY-04: Return same PayResponse format as product orders for WeChat Pay.
- D-CPAY-05: On successful payment, redirect user directly to content detail page (canView=true).
- D-CPAY-06: Payment timeout is 30 minutes (same as product orders).
- D-CPAY-07: Single payment method only - no mixing payment methods.
- D-CPAY-08: No refunds for content purchases. Standard practice for digital content.
- D-CPAY-09: On payment failure, show error toast. User retries from content detail page.
- D-CPAY-10: Content price cannot be changed while published. Admin must unpublish then republish.
- D-CPAY-11: Block duplicate purchases - show "Already purchased" error.
- D-CPAY-12: On concurrent purchase attempts, first payment wins, second fails.
- D-CPAY-13: Purchased content cannot be unpublished by admin. User keeps access.

**Content Search Scope:**
- D-SEARCH-01: Search covers videos and help articles only. Announcements excluded.
- D-SEARCH-02: Search matches title field only using SQL LIKE.
- D-SEARCH-03: Results sorted by relevance (title match quality) first, then created_at desc.
- D-SEARCH-04: Search UI is a search bar on existing content list page.
- D-SEARCH-05: Minimum 1 character to trigger search.
- D-SEARCH-06: Show "No results found" message when search returns empty.

**Category System Design:**
- D-CAT-01: Add category field to Video entity. Both Video and HelpArticle have categories.
- D-CAT-02: Categories managed via new content_categories reference table with admin CRUD.
- D-CAT-03: Flat categories only - no hierarchy/sub-categories.
- D-CAT-04: Video and HelpArticle share the same category list.
- D-CAT-05: Category filter on content list page as tabs/dropdown. "All" shows everything.

**Like Behavior:**
- D-LIKE-01: Like button toggles like/unlike. Users can change their mind.
- D-LIKE-02: Per-user tracking in new content_likes table.
- D-LIKE-03: UI shows total like count (e.g., "123 likes").
- D-LIKE-04: Users can only like content they have purchased.
- D-LIKE-05: Like records preserved when content deleted.
- D-LIKE-06: No per-user rate limit on likes.
- D-LIKE-07: Admin sees total like count per content in content management.

**Content Preview:**
- D-PREVIEW-01: Show first 150 characters of content as teaser for unpurchased paid content.

**Admin Analytics:**
- D-ANALYTICS-01: Admin dashboard shows total revenue, revenue by content type, top purchased content, purchase count over time.
- D-ANALYTICS-02: Purchase history list shows user, content title, price, payment method, purchase time. Paginated with filters.

### Claude's Discretion
None - all decisions were user-driven with recommendations accepted.

### Deferred Ideas (OUT OF SCOPE)
None - discussion stayed within phase scope.
</user_constraints>

<phase_requirements>
## Phase Requirements

| ID | Description | Research Support |
|----|-------------|------------------|
| CPAY-01 | WeChat Pay for content purchases | Extend PayController callback dispatch by CP prefix; ContentPurchaseService generates prepay orders |
| CPAY-02 | Admin content purchase records | New admin endpoint in AdminContentController; ContentPurchaseMapper for queries |
| CPAY-03 | Content keyword search | Add search endpoint to ContentController; SQL LIKE on title field |
| CPAY-04 | Dynamic content category management | New content_categories table; AdminContentController CRUD endpoints |
| CPAY-05 | Content preview before purchase | Modify ContentResponse to include teaser field (first 150 chars) |
| CPAY-06 | Like/unlike with engagement counts | New content_likes table; toggle endpoint; increment/decrement likes on Video/HelpArticle |
| CPAY-07 | Admin purchase analytics and revenue reports | Follow AdminFinanceController.getStatistics() pattern; aggregate queries |
</phase_requirements>

## Architectural Responsibility Map

| Capability | Primary Tier | Secondary Tier | Rationale |
|------------|-------------|----------------|-----------|
| WeChat Pay callback handling | API / Backend | - | Payment processing is backend-only for security |
| Content purchase order creation | API / Backend | - | Prepay order generation requires server-side signing |
| Content search | API / Backend | Database / Storage | SQL LIKE query executed on database |
| Category management | API / Backend | Database / Storage | Reference table CRUD |
| Like toggle | API / Backend | Database / Storage | Atomic increment/decrement on entity |
| Content preview | API / Backend | - | Teaser truncation in ContentResponse builder |
| Admin analytics | API / Backend | Database / Storage | Aggregate SQL queries |
| Payment UI | Browser / Client | - | Mini program pay method selector |
| Search UI | Browser / Client | - | Search bar component on content list |

## Standard Stack

### Core
| Library | Version | Purpose | Why Standard |
|---------|---------|---------|--------------|
| Spring Boot | 3.3.2 | Backend framework | [VERIFIED: pom.xml] Project standard |
| MyBatis-Plus | 3.5.5 | ORM | [VERIFIED: pom.xml] Project standard |
| Sa-Token | 1.37.0 | Authentication | [VERIFIED: pom.xml] JWT auth |
| WxJava SDK | 4.6.0 | WeChat integration | [VERIFIED: pom.xml] Mini program + Pay |

### Supporting
| Library | Version | Purpose | When to Use |
|---------|---------|---------|-------------|
| Hutool | 5.8.25 | ID generation | Order number generation with IdUtil.getSnowflakeNextIdStr() |
| Lombok | 1.18.30 | Boilerplate | Entity and DTO classes |

### Alternatives Considered
| Instead of | Could Use | Tradeoff |
|------------|-----------|----------|
| SQL LIKE search | Full-text search (Elasticsearch) | LIKE sufficient for title-only search on expected data volume |
| Flat categories | Hierarchical categories | Flat simpler for content taxonomy; no deep nesting needed |
| In-memory like count | Redis counter | Database increment sufficient for expected engagement volume |

**Installation:**
No new dependencies needed - existing stack covers all requirements.

**Version verification:**
All versions verified from pom.xml at drink-mall-api/pom.xml.

## Architecture Patterns

### System Architecture Diagram

```
                                    +------------------+
                                    | WeChat Pay API   |
                                    +--------+---------+
                                             |
                                             | Callback (encrypted)
                                             v
+------------------+              +---------+----------+
| Mini Program      |              |   PayController    |
| (UniApp)          |              +---------+----------+
|                   |                        |
| /content/{id}/buy |                        | Dispatch by order prefix
+--------+---------+                        |
         |                                  v
         | POST                    +-------+--------+
         |                         | OrderService   | (product orders, "O" or "DM" prefix)
         |                         | ContentPurchase| (content orders, "CP" prefix)
         |                         |    Service     |
         |                         +-------+--------+
         v                                 |
+--------+---------+                       | Update status
| ContentController |<---------------------+
+--------+---------+
         |
         | GET /content?search=keyword&type=&category=
         v
+--------+---------+
| VideoMapper      |
| HelpArticleMapper|
+--------+---------+
         |
         v
+--------+---------+
| MySQL Database   |
| - videos         |
| - help_articles  |
| - content_purchases|
| - content_likes  |
| - content_categories|
+------------------+
```

### Recommended Project Structure

```
drink-mall-api/src/main/java/com/drinkmall/
├── controller/
│   ├── ContentController.java          # Add search, like endpoints
│   ├── PayController.java              # Add CP prefix dispatch
│   └── admin/
│       └── AdminContentController.java # Add purchase records, analytics, categories
├── service/
│   ├── ContentPurchaseService.java     # NEW: content payment flow
│   └── admin/
│       └── AdminContentService.java    # Extend: purchase CRUD, analytics
├── entity/
│   ├── ContentPurchase.java            # EXISTS: add status field
│   ├── ContentLike.java                # NEW: like tracking
│   └── ContentCategory.java            # NEW: category reference
├── mapper/
│   ├── ContentPurchaseMapper.java      # EXISTS
│   ├── ContentLikeMapper.java          # NEW
│   └── ContentCategoryMapper.java      # NEW
└── dto/
    ├── ContentPurchaseResponse.java    # NEW: admin view
    └── ContentAnalyticsResponse.java   # NEW: dashboard stats

drink-mall-api/src/main/resources/db/migration/
├── V19__Content_Payment_Order_System.sql    # Add status to content_purchases, order_no
├── V20__Content_Categories_Table.sql         # content_categories table
└── V21__Content_Likes_Table.sql              # content_likes table

drink-mall-mini/src/pages/content/
├── list/index.vue     # Add search bar, category filter
└── detail/index.vue   # Add like button, preview handling

drink-mall-admin/src/views/content/
├── video.vue         # Add category selector
├── help.vue          # Add category selector
├── category.vue      # NEW: category management
├── purchases.vue     # NEW: purchase records
└── analytics.vue     # NEW: revenue dashboard
```

### Pattern 1: PayController Callback Dispatch by Prefix

**What:** Dispatch callback handling based on order number prefix.

**When to use:** Same callback endpoint handles multiple order types.

**Example:**
```java
// Source: Based on existing PayController.java pattern
@PostMapping("/callback")
public String handleWeChatPayCallback(@RequestBody String body, @RequestHeader HttpHeaders headers) {
    // ... verification and decryption ...
    String orderNo = text(root, "out_trade_no");
    
    if (orderNo.startsWith("CP")) {
        // Content purchase order
        contentPurchaseService.confirmPaymentCallback(orderNo, paidAmount, paymentNo);
    } else {
        // Product order (existing flow)
        orderService.confirmOnlinePaymentCallback(orderNo, paidAmount, paymentNo);
    }
    return "SUCCESS";
}
```

### Pattern 2: Content Purchase Order Number Format

**What:** Generate unique order numbers with CP prefix.

**When to use:** Creating content purchase prepay orders.

**Example:**
```java
// Source: Based on OrderServiceImpl.java orderNo pattern
// Product orders: "DM" + IdUtil.getSnowflakeNextIdStr()
// Content orders: "CP" + YYYYMMDD + sequence
String orderNo = "CP" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) 
    + String.format("%05d", sequence);
```

### Pattern 3: Like Toggle with Atomic Update

**What:** Atomic like/unlike with count update.

**When to use:** User likes or unlikes content.

**Example:**
```java
// Source: Standard pattern for engagement tracking
@Transactional
public void toggleLike(Long userId, String contentType, Long contentId) {
    ContentLike existing = contentLikeMapper.selectOne(
        new LambdaQueryWrapper<ContentLike>()
            .eq(ContentLike::getUserId, userId)
            .eq(ContentLike::getContentType, contentType)
            .eq(ContentLike::getContentId, contentId)
    );
    
    if (existing != null) {
        // Unlike: delete record, decrement count
        contentLikeMapper.deleteById(existing.getId());
        if ("video".equals(contentType)) {
            videoMapper.update(null, new LambdaUpdateWrapper<Video>()
                .eq(Video::getId, contentId)
                .setSql("likes = GREATEST(0, likes - 1)"));
        }
    } else {
        // Like: insert record, increment count
        ContentLike like = new ContentLike();
        like.setUserId(userId);
        like.setContentType(contentType);
        like.setContentId(contentId);
        like.setCreatedAt(LocalDateTime.now());
        contentLikeMapper.insert(like);
        
        if ("video".equals(contentType)) {
            videoMapper.update(null, new LambdaUpdateWrapper<Video>()
                .eq(Video::getId, contentId)
                .setSql("likes = likes + 1"));
        }
    }
}
```

### Anti-Patterns to Avoid

- **Don't generate prepay orders synchronously in buy endpoint without idempotency:** Use idempotency key from content_purchases table to prevent duplicate orders
- **Don't update likes field without atomic increment:** Race conditions cause incorrect counts; use setSql("likes = likes + 1")
- **Don't search content body for keyword search:** Only title field per D-SEARCH-02; full-text search overkill

## Don't Hand-Roll

| Problem | Don't Build | Use Instead | Why |
|---------|-------------|-------------|-----|
| WeChat Pay callback verification | Custom signature verification | PayCallbackVerifier (existing) | AEAD_AES_256_GCM decryption, signature verification already implemented |
| Order number generation | UUID or random strings | Hutool IdUtil.getSnowflakeNextIdStr() | Snowflake IDs are unique, sortable, no collisions |
| Pagination response | Custom page wrapper | MyBatis-Plus IPage | Built-in pagination with convert() for DTO mapping |
| Admin auth check | Manual role validation | @SaCheckRole("admin") | Sa-Token annotation handles JWT validation and role check |

**Key insight:** Existing payment infrastructure is robust - extend rather than rebuild. The PayController callback flow with decryption and verification is production-ready.

## Common Pitfalls

### Pitfall 1: Content Purchase Duplicate Orders
**What goes wrong:** User clicks "Buy" twice rapidly, creates two pending orders for same content.
**Why it happens:** No idempotency check before creating prepay order.
**How to avoid:** Check for existing pending purchase before creating new order:
```java
ContentPurchase existing = contentPurchaseMapper.selectOne(
    new LambdaQueryWrapper<ContentPurchase>()
        .eq(ContentPurchase::getUserId, userId)
        .eq(ContentPurchase::getContentType, type)
        .eq(ContentPurchase::getContentId, id)
        .eq(ContentPurchase::getStatus, "pending")
);
if (existing != null) {
    // Return existing prepay order instead of creating new one
    return getPayResponse(existing);
}
```
**Warning signs:** Multiple content_purchases records for same user+content with status='pending'.

### Pitfall 2: WeChat Pay Callback Not Processing CP Orders
**What goes wrong:** Content payments succeed in WeChat but backend doesn't update status.
**Why it happens:** PayController only calls orderService.confirmOnlinePaymentCallback() which looks up product orders.
**How to avoid:** Add prefix check in PayController.callback() to dispatch to ContentPurchaseService.
**Warning signs:** content_purchases stuck in 'pending' status after user sees payment success in WeChat.

### Pitfall 3: Like Count Desynchronization
**What goes wrong:** likes field on Video/HelpArticle doesn't match actual count in content_likes.
**Why it happens:** Non-atomic updates, deleted records not accounted for, transaction rollback.
**How to avoid:** Use atomic SQL updates (setSql("likes = likes + 1")), consider periodic reconciliation job.
**Warning signs:** User sees "5 likes" but content_likes table has 7 records.

### Pitfall 4: Search Returns Unpublished Content
**What goes wrong:** Search shows content that was unpublished (status=0).
**Why it happens:** Search query doesn't filter by status.
**How to avoid:** Always add .eq(Video::getStatus, 1) and .eq(HelpArticle::getStatus, 1) in search queries.
**Warning signs:** Users can find and click content that shows "not found" on detail page.

## Code Examples

Verified patterns from existing codebase:

### Content Purchase Entity (EXISTING)
```java
// Source: drink-mall-api/src/main/java/com/drinkmall/entity/ContentPurchase.java
@Data
@TableName("content_purchases")
public class ContentPurchase {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String contentType;
    private Long contentId;
    private BigDecimal price;
    private String paymentMethod;
    private String idempotencyKey;
    private LocalDateTime createdAt;
    // NEEDS TO ADD: status, orderNo, paymentNo, paymentTime
}
```

### PayResponse Format (EXISTING - REUSE)
```java
// Source: drink-mall-api/src/main/java/com/drinkmall/dto/PayResponse.java
@Data
@Builder
public class PayResponse {
    private String orderNo;
    private String prepayId;
    private String appId;
    private String timeStamp;
    private String nonceStr;
    private String packageStr;
    private String signType;
    private String paySign;
}
```

### Content Response with Preview (EXTEND)
```java
// Source: drink-mall-api/src/main/java/com/drinkmall/dto/ContentResponse.java
// Add preview field for teaser content
@Data
@Builder
public class ContentResponse {
    // ... existing fields ...
    private String preview;  // NEW: first 150 characters for teaser
    
    public static ContentResponse fromArticle(HelpArticle article, boolean purchased, ...) {
        return ContentResponse.builder()
            // ... existing mapping ...
            .content(access.isCanView() ? article.getContent() : null)
            .preview(!access.isCanView() && article.getContent() != null 
                ? article.getContent().substring(0, Math.min(150, article.getContent().length())) 
                : null)
            .build();
    }
}
```

### Search Endpoint Pattern
```java
// Based on existing ContentController.getContent() pattern
@GetMapping("/search")
public Result<IPage<ContentResponse>> searchContent(
        @RequestParam String keyword,
        @RequestParam(defaultValue = "video") String type,
        @RequestParam(defaultValue = "1") Integer page,
        @RequestParam(defaultValue = "10") Integer size) {
    Long userId = currentUserId();
    String likePattern = "%" + keyword + "%";
    
    if ("article".equals(type)) {
        IPage<HelpArticle> results = helpArticleMapper.selectPage(
            new Page<>(page, size),
            new LambdaQueryWrapper<HelpArticle>()
                .eq(HelpArticle::getStatus, 1)
                .like(HelpArticle::getTitle, likePattern)
                .orderByDesc(HelpArticle::getCreatedAt)
        );
        return Result.success(results.convert(a -> articleResponse(a, userId)));
    }
    // Similar for video...
}
```

## State of the Art

| Old Approach | Current Approach | When Changed | Impact |
|--------------|------------------|--------------|--------|
| Mock online payment in ContentController.buy() | Real WeChat Pay prepay order generation | Phase 6 | Users can pay with real money |
| No content categories | Flat category system with admin CRUD | Phase 6 | Content discoverability improves |
| No content search | Title keyword search with LIKE | Phase 6 | Users can find specific content |
| Static like button (UI only) | Persistent like tracking per user | Phase 6 | Engagement metrics visible |

**Deprecated/outdated:**
- ContentController.buy() "online" case with mock synchronous payment: Replace with real prepay order generation

## Assumptions Log

> All claims in this research were verified against existing codebase. No assumptions made.

| # | Claim | Section | Risk if Wrong |
|---|-------|---------|---------------|
| - | (No unverified claims) | - | - |

**If this table is empty:** All claims in this research were verified or cited - no user confirmation needed.

## Open Questions

1. **WeChat Pay prepay order generation API**
   - What we know: WxJava SDK 4.6.0 is installed; product orders use OrderServiceImpl.payOrder()
   - What's unclear: Exact API call pattern for content purchase prepay orders
   - Recommendation: Check WxPayService.createOrder() usage in OrderServiceImpl; follow same pattern with different order prefix

2. **Content purchase analytics aggregation period**
   - What we know: D-ANALYTICS-01 specifies "purchase count over time"
   - What's unclear: Time granularity (daily/weekly/monthly) for charts
   - Recommendation: Start with daily aggregation, can add granularity filter later

## Environment Availability

> Phase has external dependency on WeChat Pay API (same as Phase 3).

| Dependency | Required By | Available | Version | Fallback |
|------------|------------|-----------|---------|----------|
| MySQL | content_purchases, content_likes, content_categories tables | N/A (cloud) | 8.0 | - |
| Redis | Session cache | N/A (cloud) | 7.2 | - |
| WeChat Pay API | Content payments | N/A | - | Sandbox mode for dev |

**Missing dependencies with no fallback:**
- None - infrastructure already in place from Phase 3.

**Missing dependencies with fallback:**
- None.

## Validation Architecture

### Test Framework
| Property | Value |
|----------|-------|
| Framework | JUnit 5 + AssertJ |
| Config file | src/test/resources/application-test.yml |
| Quick run command | `mvn test -Dtest=*ContentPurchase* -q` |
| Full suite command | `mvn test -q` |

### Phase Requirements - Test Map
| Req ID | Behavior | Test Type | Automated Command | File Exists? |
|--------|----------|-----------|-------------------|-------------|
| CPAY-01 | WeChat Pay callback dispatches to content purchase | unit | `mvn test -Dtest=PayControllerTest#testContentCallback -q` | Wave 0 |
| CPAY-01 | Content prepay order generation | unit | `mvn test -Dtest=ContentPurchaseServiceTest#testCreatePrepayOrder -q` | Wave 0 |
| CPAY-02 | Admin purchase records list | unit | `mvn test -Dtest=AdminContentServiceTest#testGetPurchaseRecords -q` | Wave 0 |
| CPAY-03 | Search by keyword returns matching content | unit | `mvn test -Dtest=ContentControllerTest#testSearch -q` | Wave 0 |
| CPAY-04 | Category CRUD operations | unit | `mvn test -Dtest=AdminContentServiceTest#testCategoryCRUD -q` | Wave 0 |
| CPAY-05 | Preview shows first 150 chars | unit | `mvn test -Dtest=ContentResponseTest#testPreviewTruncation -q` | Wave 0 |
| CPAY-06 | Like toggle updates count atomically | unit | `mvn test -Dtest=ContentLikeServiceTest#testToggleLike -q` | Wave 0 |
| CPAY-07 | Analytics aggregates revenue correctly | unit | `mvn test -Dtest=AdminContentServiceTest#testAnalytics -q` | Wave 0 |

### Sampling Rate
- Per task commit: `mvn test -Dtest=*Test -q`
- Per wave merge: `mvn test -q`
- Phase gate: Full suite green before /gsd-verify-work

### Wave 0 Gaps
- [ ] `ContentPurchaseServiceTest.java` - covers CPAY-01 prepay generation
- [ ] `PayControllerTest.java` - add testContentCallback() for CP prefix dispatch
- [ ] `ContentLikeServiceTest.java` - covers CPAY-06 toggle
- [ ] `ContentCategoryTest.java` - covers CPAY-04 CRUD
- [ ] Database migration V19-V21 - schema changes for status, likes, categories

## Security Domain

### Applicable ASVS Categories

| ASVS Category | Applies | Standard Control |
|----------------|---------|------------------|
| V2 Authentication | yes | Sa-Token JWT with @SaCheckLogin |
| V3 Session Management | yes | Sa-Token manages session |
| V4 Access Control | yes | ContentAccessPolicy for view/buy decisions |
| V5 Input Validation | yes | Spring @Valid, keyword sanitization for LIKE |
| V6 Cryptography | yes | WeChat Pay callback AEAD_AES_256_GCM (existing) |

### Known Threat Patterns for Spring Boot + WeChat Pay

| Pattern | STRIDE | Standard Mitigation |
|---------|--------|---------------------|
| Callback replay attack | Tampering | Check paymentNo not already processed |
| Order amount manipulation | Tampering | Compare paidAmount with stored price before confirming |
| SQL injection in search | Tampering | Use MyBatis-Plus parameterized queries, sanitize keyword |
| Duplicate purchase race | Denial of Service | Unique constraint on (user_id, content_type, content_id) |
| Unpaid content access | Information Disclosure | ContentAccessPolicy checks purchased status |

## Sources

### Primary (HIGH confidence)
- drink-mall-api/src/main/java/com/drinkmall/controller/PayController.java - Callback handling pattern
- drink-mall-api/src/main/java/com/drinkmall/service/impl/OrderServiceImpl.java - Payment flow pattern
- drink-mall-api/src/main/java/com/drinkmall/entity/ContentPurchase.java - Entity structure
- drink-mall-api/src/main/resources/db/migration/V17__Mini_Program_Content_Purchases.sql - Table schema

### Secondary (MEDIUM confidence)
- CONTEXT.md decisions - User locked requirements

### Tertiary (LOW confidence)
- None - all findings verified against codebase

## Metadata

**Confidence breakdown:**
- Standard stack: HIGH - All versions verified from pom.xml
- Architecture: HIGH - Patterns verified from existing code
- Pitfalls: HIGH - Based on known issues from similar implementations

**Research date:** 2026-05-18
**Valid until:** 30 days - stable architecture patterns
