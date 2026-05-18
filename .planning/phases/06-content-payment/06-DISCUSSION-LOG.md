# Phase 6: Content Payment & Monetization - Discussion Log

> **Audit trail only.** Do not use as input to planning, research, or execution agents.
> Decisions are captured in CONTEXT.md — this log preserves the alternatives considered.

**Date:** 2026-05-18
**Phase:** 06-content-payment
**Areas discussed:** Content Payment Flow, Content Search Scope, Category System Design, Like Behavior & Persistence

---

## Content Payment Flow

### Callback Handling

| Option | Description | Selected |
|--------|-------------|----------|
| Same callback, dispatch by prefix | Extend PayController to detect content vs order callbacks by prefix/format. Reuse existing decryption & signature verification. | ✓ |
| Separate callback endpoint | Create a separate /api/v1/pay/content-callback endpoint. Cleaner separation but duplicates WeChat Pay decryption logic. | |

**User's choice:** Same callback, dispatch by prefix

### Pending Order Storage

| Option | Description | Selected |
|--------|-------------|----------|
| Use content_purchases as pending order | Content prepay orders stored in content_purchases with status='pending'. ContentController.buy() generates prepayId and returns PayResponse. | ✓ |
| Create a new content_pay_orders table | Separate table for pending payment orders, content_purchases only stores completed purchases. | |

**User's choice:** Use content_purchases as pending order

### Order Number Prefix

| Option | Description | Selected |
|--------|-------------|----------|
| CP prefix | Content purchase orders: CPYYYYMMDDnnnnn. Product orders: OYYYYMMDDnnnnn (existing format). | ✓ |
| C prefix | Single letter prefix 'C' for content, 'O' for orders. | |

**User's choice:** CP prefix

### Response Format

| Option | Description | Selected |
|--------|-------------|----------|
| Same PayResponse format | Return same PayResponse as product orders (appId, timeStamp, nonceStr, packageStr, signType, paySign). | ✓ |
| Custom ContentPayResponse | Custom response with additional fields like contentTitle, contentType. Frontend must handle two different response types. | |

**User's choice:** Same PayResponse format

### Post-Payment Redirect

| Option | Description | Selected |
|--------|-------------|----------|
| Redirect to content detail | On successful callback, redirect to content detail page with canView=true. User sees full content immediately. | ✓ |
| Show success page then redirect | Show a 'Purchase Successful' intermediate page with 'View Content' button. More explicit but adds extra click. | |

**User's choice:** Redirect to content detail

### Payment Timeout

| Option | Description | Selected |
|--------|-------------|----------|
| 30 minutes | Content purchase timeout matches product order timeout. Consistent with existing behavior. | ✓ |
| 15 minutes | Shorter timeout for content. Content purchases are typically impulsive decisions. | |
| 60 minutes | Longer timeout gives users more time if interrupted. | |

**User's choice:** 30 minutes (same as product orders)

### Partial Payments

| Option | Description | Selected |
|--------|-------------|----------|
| Single payment only | Content purchases are single-payment only. Simpler implementation. | ✓ |
| Support mixed payments | Allow combining points/balance with WeChat Pay for partial payments. More flexible but significantly more complex. | |

**User's choice:** Single payment only

### Refunds

| Option | Description | Selected |
|--------|-------------|----------|
| No refunds | Digital content once purchased is non-refundable. Standard practice for content monetization. | ✓ |
| Refund if not viewed | Allow refunds within 24h of purchase if content not viewed. Requires tracking view count. | |

**User's choice:** No refunds

### Payment Failure

| Option | Description | Selected |
|--------|-------------|----------|
| Show error toast, user retries | Show error message (toast), keep purchase status as 'pending'. User can retry from content detail. | ✓ |
| Dedicated failure page | Navigate to a dedicated payment failure page with retry button and order details. | |

**User's choice:** Show error toast, user retries

### Price Changes

| Option | Description | Selected |
|--------|-------------|----------|
| No price changes while published | Content price locked once published. To change price, unpublish old content and publish new. | ✓ |
| Allow price changes anytime | Allow price changes anytime. Existing purchasers keep their purchased content. | |

**User's choice:** No price changes while published

### Duplicate Purchase

| Option | Description | Selected |
|--------|-------------|----------|
| Block and show error | Second attempt returns 'Already purchased' error. User already has access. | ✓ |
| Allow re-purchase | Allow paying again (e.g., as gift). Would need gifting feature that doesn't exist yet. | |

**User's choice:** Block and show error

### Concurrent Purchases

| Option | Description | Selected |
|--------|-------------|----------|
| First wins, second fails | Only one succeeds, second gets 'already purchased' error. Prevents potential confusion. | ✓ |
| Both succeed | Both succeed, both get access. Fair for digital content. | |

**User's choice:** First wins, second fails

### Unpublish Handling

| Option | Description | Selected |
|--------|-------------|----------|
| Keep access | User keeps access to content they purchased. Standard for paid content. | ✓ |
| Allow unpublish, user loses access | Admin can unpublish, user loses access. May cause user complaints. | |

**User's choice:** Keep access (purchased content unpublishable)

---

## Content Search Scope

### Search Scope

| Option | Description | Selected |
|--------|-------------|----------|
| Videos + Help articles | Search across videos and help articles (the two paid content types). Announcements excluded as they're free. | ✓ |
| Videos only | Search videos only. Help articles typically not search-intensive. | |
| All content types | Search videos, help articles, and announcements. Most comprehensive but announcements are free content. | |

**User's choice:** Videos + Help articles

### Search Fields

| Option | Description | Selected |
|--------|-------------|----------|
| Title only | Search title field only. Fast, simple SQL LIKE query. Good enough for content discovery. | ✓ |
| Title + content | Search title + content body. More comprehensive but slower. | |
| Title + content + tags | Search title, content body, and any tags/category fields. Most comprehensive but requires tag system. | |

**User's choice:** Title only

### Result Sorting

| Option | Description | Selected |
|--------|-------------|----------|
| Relevance first | Sort by relevance (title match quality), then by created_at desc. Best user experience for finding specific content. | ✓ |
| Newest first | Sort by newest first. Simple but may not find what user wants. | |
| Most popular | Sort by views/likes. Good for discovery but not for finding specific content. | |

**User's choice:** Relevance first

### UI Placement

| Option | Description | Selected |
|--------|-------------|----------|
| Search bar on content list | Add search bar to existing content list page. Results appear in-place. | ✓ |
| Dedicated search page | Separate search page with search bar and filters. More space for advanced search but adds navigation. | |

**User's choice:** Search bar on content list

### Minimum Characters

| Option | Description | Selected |
|--------|-------------|----------|
| 1 character | Allow single-character search. Users can find short titles like '酒', '红酒'. | ✓ |
| 2 characters minimum | Require 2+ characters. Reduces noise but may miss short titles. | |

**User's choice:** 1 character

### No Results

| Option | Description | Selected |
|--------|-------------|----------|
| Show 'No results found' message | Clear feedback that search found nothing. User can try different keywords. | ✓ |
| Show recommendations | Show popular/recommended content as fallback. May confuse user if unrelated. | |

**User's choice:** Show 'No results found' message

---

## Category System Design

### Category Field for Video

| Option | Description | Selected |
|--------|-------------|----------|
| Add to Video | Add category field to Video entity. Both Video and HelpArticle use same category values. | ✓ |
| Keep Video without category | Video content doesn't need categories. Only HelpArticle uses categories. | |

**User's choice:** Add to Video

### Category Type

| Option | Description | Selected |
|--------|-------------|----------|
| Managed reference table | Admin-managed category list in a new content_categories table. CRUD via admin API. | ✓ |
| Free-form string field | Admin types any category string when creating content. Flexible but inconsistent values. | |

**User's choice:** Managed reference table

### Hierarchy

| Option | Description | Selected |
|--------|-------------|----------|
| Flat categories | Single-level categories only. Simpler implementation, easier browsing UI. | ✓ |
| Hierarchical categories | Categories can have sub-categories. More flexible but adds complexity. | |

**User's choice:** Flat categories

### Category Scope

| Option | Description | Selected |
|--------|-------------|----------|
| Shared categories | Same categories for Videos and HelpArticles. Easier management, consistent user experience. | ✓ |
| Separate category lists | Separate category lists for Videos and HelpArticles. More flexible but doubles admin work. | |

**User's choice:** Shared categories

### Category UI

| Option | Description | Selected |
|--------|-------------|----------|
| Filter on content list | Add category filter dropdown/tab on content list page. Users click category to filter. | ✓ |
| Show on detail page | Add category field on content detail page. Click navigates to filtered list. | |

**User's choice:** Filter on content list

### Default Category View

| Option | Description | Selected |
|--------|-------------|----------|
| 'All' shows everything | Default tab shows all content. Category tabs filter the list. | ✓ |
| First category as default | First category is default. Forces user into a category first. | |

**User's choice:** 'All' shows everything

---

## Like Behavior & Persistence

### Like Toggle

| Option | Description | Selected |
|--------|-------------|----------|
| Toggle like/unlike | Click to like, click again to unlike. Users can change their mind. Standard social engagement pattern. | ✓ |
| Like only (no unlike) | Click to like, no way to unlike. Simpler but may frustrate users who click by mistake. | |

**User's choice:** Toggle like/unlike

### Like Tracking

| Option | Description | Selected |
|--------|-------------|----------|
| Per-user tracking | New content_likes table tracks (user_id, content_type, content_id). Users can only like once. | ✓ |
| Anonymous counter only | Just increment likes counter in Video/HelpArticle. No record of who liked. | |

**User's choice:** Per-user tracking

### Like Count Display

| Option | Description | Selected |
|--------|-------------|----------|
| Show total count | Shows total like count (e.g., '123 likes'). Social proof for content popularity. | ✓ |
| Show user's like status only | Just show filled/empty heart. No number. | |
| Show both count and status | Show count + user's status (filled heart if liked). | |

**User's choice:** Show total count

### Like Access

| Option | Description | Selected |
|--------|-------------|----------|
| Only purchased content | Do not allow liking locked/unpurchased content. Likes are for engaged users who viewed content. | ✓ |
| Allow on any content | Anyone can like any content. More engagement but less meaningful likes. | |

**User's choice:** Only purchased content

### Delete Content Handling

| Option | Description | Selected |
|--------|-------------|----------|
| Preserve likes data | Keep likes record but hide from UI. content_likes entries preserved. | ✓ |
| Delete likes records | Delete all content_likes entries when content deleted. | |

**User's choice:** Preserve likes data

### Like Limit

| Option | Description | Selected |
|--------|-------------|----------|
| No per-user limit | User can like as many pieces of content as they want. Normal social engagement behavior. | ✓ |
| Rate limit likes | Limit user to N likes per day/hour to prevent spam. | |

**User's choice:** No per-user limit

### Like Analytics

| Option | Description | Selected |
|--------|-------------|----------|
| Admin sees total like count | Simple like count in content management list. No detailed breakdown needed. | ✓ |
| Admin sees full like history | Admin sees who liked what content with timestamps. | |

**User's choice:** Admin sees total like count

---

## Claude's Discretion

None — all decisions were user-driven with recommendations accepted.

## Deferred Ideas

None — discussion stayed within phase scope.
