# Phase 6: Content Payment & Monetization - Context

**Gathered:** 2026-05-18
**Status:** Ready for planning

## Phase Boundary

Users can purchase paid content via real WeChat Pay, search content by keyword, browse by dynamic categories, engage with likes, preview teaser content before purchase, and admins can view purchase history and analytics.

**In scope:**
- Real WeChat Pay integration for content purchases (CPAY-01)
- Content purchase record viewing for admins (CPAY-02)
- Keyword-based content search (CPAY-03)
- Dynamic content category management (CPAY-04)
- Content preview before purchase (CPAY-05)
- Like/unlike functionality with engagement counts (CPAY-06)
- Admin purchase analytics and revenue reports (CPAY-07)

**Out of scope:**
- Product order payment (already implemented in Phase 3)
- Content creation/editing (already implemented in Phase 4)
- User level management (already implemented)
- Physical goods checkout (Phase 3)

## Implementation Decisions

### Content Payment Flow
- **D-CPAY-01:** Same callback endpoint as product orders, dispatch by order number prefix (CP prefix). Reuse existing `PayController` WeChat Pay decryption and signature verification.
- **D-CPAY-02:** Use `content_purchases` table with `status='pending'` for pending orders. No new table needed — existing table has all required fields.
- **D-CPAY-03:** Content purchase order numbers use `CPYYYYMMDDnnnnn` format (CP prefix). Distinct from product orders (`OYYYYMMDDnnnnn`).
- **D-CPAY-04:** Return same `PayResponse` format as product orders for WeChat Pay. Mini program already has code to handle this format.
- **D-CPAY-05:** On successful payment, redirect user directly to content detail page (canView=true). No intermediate success page.
- **D-CPAY-06:** Payment timeout is 30 minutes (same as product orders). Consistent timeout behavior across platform.
- **D-CPAY-07:** Single payment method only — no mixing payment methods (e.g., points + WeChat Pay). Simpler implementation.
- **D-CPAY-08:** No refunds for content purchases. Standard practice for digital content.
- **D-CPAY-09:** On payment failure, show error toast. User retries from content detail page.
- **D-CPAY-10:** Content price cannot be changed while published. To change price, admin must unpublish then republish.
- **D-CPAY-11:** Block duplicate purchases — show "Already purchased" error if user tries to buy content they own.
- **D-CPAY-12:** On concurrent purchase attempts, first payment wins, second fails with error.
- **D-CPAY-13:** Purchased content cannot be unpublished by admin. User keeps access to content they paid for.

### Content Search Scope
- **D-SEARCH-01:** Search covers videos and help articles only. Announcements excluded as free content.
- **D-SEARCH-02:** Search matches title field only using SQL LIKE. Fast, simple query.
- **D-SEARCH-03:** Results sorted by relevance (title match quality) first, then by created_at desc.
- **D-SEARCH-04:** Search UI is a search bar on existing content list page (`/content/list`). Results appear in-place.
- **D-SEARCH-05:** Minimum 1 character to trigger search. Supports short Chinese titles.
- **D-SEARCH-06:** Show "No results found" message when search returns empty. Clear user feedback.

### Category System Design
- **D-CAT-01:** Add `category` field to `Video` entity. Both Video and HelpArticle have categories.
- **D-CAT-02:** Categories are managed via a new `content_categories` reference table with admin CRUD.
- **D-CAT-03:** Flat categories only — no hierarchy/sub-categories. Simpler implementation.
- **D-CAT-04:** Video and HelpArticle share the same category list. One unified category system.
- **D-CAT-05:** Category filter is on content list page as tabs/dropdown. "All" shows everything by default.

### Like Behavior & Persistence
- **D-LIKE-01:** Like button toggles like/unlike. Users can change their mind.
- **D-LIKE-02:** Per-user tracking in new `content_likes` table. Records (user_id, content_type, content_id).
- **D-LIKE-03:** UI shows total like count (e.g., "123 likes").
- **D-LIKE-04:** Users can only like content they have purchased. Engagement from engaged users.
- **D-LIKE-05:** Like records preserved when content deleted. Data kept for analytics.
- **D-LIKE-06:** No per-user rate limit on likes. Normal social engagement behavior.
- **D-LIKE-07:** Admin sees total like count per content in content management list. No detailed breakdown needed.

### Content Preview (CPAY-05)
- **D-PREVIEW-01:** Show first 150 characters of content as teaser for unpurchased paid content. Low barrier to preview.

### Admin Purchase Analytics (CPAY-07)
- **D-ANALYTICS-01:** Admin dashboard shows: total revenue, revenue by content type, top purchased content, purchase count over time.
- **D-ANALYTICS-02:** Purchase history list shows: user, content title, price, payment method, purchase time. Paginated with filters.

### Claude's Discretion
None — all decisions were user-driven with recommendations accepted.

## Canonical References

**Downstream agents MUST read these before planning or implementing.**

### Project Context
- `.planning/PROJECT.md` — Project overview, core value, constraints (distribution features removed)
- `.planning/REQUIREMENTS.md` — Full requirements with CPAY-01 through CPAY-07 traceability
- `.planning/ROADMAP.md` — Phase 6 success criteria and dependencies
- `.planning/STATE.md` — Current project state and completed features

### Existing Payment Implementation
- `drink-mall-api/src/main/java/com/drinkmall/controller/PayController.java` — WeChat Pay callback handler (extend for content)
- `drink-mall-api/src/main/java/com/drinkmall/dto/PayResponse.java` — Payment response format to reuse
- `drink-mall-api/src/main/java/com/drinkmall/enums/PaymentMethod.java` — Payment method enum

### Existing Content Implementation
- `drink-mall-api/src/main/java/com/drinkmall/controller/ContentController.java` — Content API with mock buy endpoint
- `drink-mall-api/src/main/java/com/drinkmall/entity/ContentPurchase.java` — Content purchase entity
- `drink-mall-api/src/main/java/com/drinkmall/entity/Video.java` — Video entity (add category field)
- `drink-mall-api/src/main/java/com/drinkmall/entity/HelpArticle.java` — HelpArticle entity (has category)
- `drink-mall-api/src/main/java/com/drinkmall/service/support/ContentAccessPolicy.java` — Access control logic

### Mini Program Pages
- `drink-mall-mini/src/pages/content/list/index.vue` — Content list page (add search bar, category filter)
- `drink-mall-mini/src/pages/content/detail/index.vue` — Content detail with paywall (add like button)
- `drink-mall-mini/src/components/PayMethodSelector/PayMethodSelector.vue` — Reusable payment selector

### Database Schema
- `drink-mall-api/src/main/resources/db/migration/V17__Mini_Program_Content_Purchases.sql` — content_purchases table
- `drink-mall-api/src/main/resources/db/migration/V18__Aftersale_Content_Admin_Audit_Polish.sql` — likes field added

## Existing Code Insights

### Reusable Assets
- **PayController callback infrastructure** — AEAD_AES_256_GCM decryption, signature verification already implemented. Extend to dispatch by order prefix.
- **ContentController.buy()** — Mock "online" case exists. Replace with real WeChat Pay prepay order generation.
- **ContentPurchase entity** — Has all fields for pending orders: idempotencyKey, paymentMethod, status.
- **PayMethodSelector component** — Mini program component for payment method selection. Already handles WeChat Pay option.
- **ContentAccessPolicy** — Evaluates access control. Already checks purchased status.

### Established Patterns
- **Payment flow pattern** — Create order → Generate prepay → Callback → Update status. Follow product order pattern.
- **Admin CRUD pattern** — Controller → Service interface → Service impl → Mapper → Entity. Apply to content_categories.
- **Content response building** — ContentResponse.fromVideo/fromArticle with access decision. Extend for preview/teaser field.

### Integration Points
- **Backend:** Extend `ContentController.buy()` for real WeChat Pay; extend `PayController.callback()` for content dispatch; add search endpoint; add like endpoint; add category management.
- **Database:** Add `content_categories` table; add `content_likes` table; add `category_id` to `videos` table.
- **Mini Program:** Add search bar to content list; add like button to content detail; handle payment redirect flow.

## Specific Ideas

- Content purchase order numbers starting with "CP" makes debugging easier
- Like button should only be active after content is purchased (engagement from real viewers)
- Category system should be flat for simplicity — content taxonomy doesn't need deep hierarchy
- Search on title-only is sufficient for content discovery, avoids performance issues with large article bodies

## Deferred Ideas

None — discussion stayed within phase scope.

---

*Phase: 06-content-payment*
*Context gathered: 2026-05-18*
