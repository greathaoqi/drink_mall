# Feature Landscape

**Domain:** Chinese Liquor/Wine E-commerce (WeChat Mini Program)
**Researched:** 2026-05-08

## Table Stakes

Features users expect. Missing = product feels incomplete.

| Feature | Why Expected | Complexity | Notes |
|---------|--------------|------------|-------|
| WeChat Login | Seamless auth in WeChat ecosystem; users expect one-click login via WeChat account | Low | wx.login API; obtain openid, nickname, avatar |
| Product Catalog | Users must browse products; multi-level category navigation is standard | Medium | Category tree, product list with pagination |
| Product Search | E-commerce standard; users expect keyword search with filters | Medium | Search by name/brand, filter by price/sales |
| Product Detail Page | Essential for purchase decision; images, description, price, specs | Medium | Image carousel, SKU selection, add to cart |
| Shopping Cart | Standard e-commerce feature; manage items before checkout | Medium | Add/remove items, quantity adjustment, select for checkout |
| Checkout Flow | Core purchase flow; address selection, payment method, order confirmation | High | Address management, WeChat Pay integration |
| Order Management | Users must track their orders; view status, history, details | Medium | Order list with status filters, order detail |
| Order Operations | Users expect to cancel orders, request refunds, confirm receipt | Medium | Cancel (pending payment), refund request, confirm delivery |
| Address Management | Required for shipping; add/edit/delete addresses | Low | Default address, WeChat address sync API |
| Personal Center | Standard hub for user info, orders, settings | Low | Avatar/nickname display, order shortcuts |
| Customer Service | Users expect help; WeChat customer service button or chat | Low | wx.openCustomerServiceChat or contact button |

## Differentiators

Features that set product apart. Not expected, but valued.

| Feature | Value Proposition | Complexity | Notes |
|---------|-------------------|------------|-------|
| Points Exchange System | Rewards loyal customers; points can be redeemed for exclusive products in "Gift Zone" | High | Points earning rules, redemption catalog, separate "Gift Zone" |
| Video Content Module | Engages users with product videos, brand stories, tutorials | Medium | Video list, detail page, player integration |
| Platform Announcements | Keeps users informed about promotions, news, policies | Low | Announcement list, detail view, homepage banner |
| Banner Carousel | Highlights promotions, featured products on homepage | Low | wx-swiper component, deep links to products/categories |
| Multi-Zone Product Layout | Segmented shopping experience: Main Zone, Retail Zone, Gift Zone (points) | Medium | Different pricing/rules per zone; Gift Zone requires points |
| Order Status Notifications | Real-time updates via WeChat subscription messages | Medium | Template messages for order status changes |
| Product Recommendations | Suggest related products, improve discovery | Medium | Based on category, purchase history, or manual curation |

## Anti-Features

Features to explicitly NOT build.

| Anti-Feature | Why Avoid | What to Do Instead |
|--------------|-----------|-------------------|
| Distribution/Referral System | Explicitly removed per project scope; complex multi-level rewards, team management | Simple single-tier loyalty (points only) |
| Distribution Partner Levels | Removed; county/city/provincial partner hierarchy was cut | Standard user accounts only |
| DF Balance Account | Removed; was part of distribution reward system | Standard account balance for refunds only |
| Upstream User Binding | Removed; no superior-downline relationships | Direct customer-platform relationship |
| Invitation/Share Binding | Removed; no referral tracking | Standard sharing without tracking |
| Own Product Listing | Removed; "Career Partner" feature to list own products | Platform-controlled catalog only |
| Multi-Platform Support | Project constraint: WeChat Mini Program only | Focus on WeChat optimization |
| Custom Payment Gateway | Project constraint: WeChat Pay only | WeChat Pay integration |
| Full Logistics System | Project constraint: API integration only | Third-party logistics query API |
| Electronic Contracts | Project constraint: signature page only | Static signature page, no full contract system |
| Warehouse Management | Project constraint: online inventory only | Simple stock management in admin panel |

## Feature Dependencies

```
WeChat Login -> User Account -> Address Management -> Checkout Flow
                                      |
                                      v
Product Catalog -> Product Detail -> Shopping Cart -> Checkout Flow -> Order Management
      |                    |                |              |                |
      v                    v                v              v                v
Category Navigation   Add to Cart     Quantity Edit   Address Select   Order Operations
Product Search        Buy Now            |             Payment         (Cancel/Refund/Confirm)
                                     Checkout           |                    |
                                                         v                    v
                                                   WeChat Pay           Order Status
                                                         |
                                                         v
                                                   Order Creation
                                                         |
                                                         v
                                                   Points Earned (if applicable)
                                                         |
                                                         v
                                                   Points Redemption (Gift Zone)

Platform Announcements -> Homepage Display
Banner Carousel -> Homepage Display
Video Content Module -> Independent module, linked from homepage or menu
```

## MVP Recommendation

Prioritize:
1. **WeChat Login + User Account** - Foundation for all user-specific features
2. **Product Catalog + Search + Detail** - Core browsing experience
3. **Shopping Cart + Checkout + WeChat Pay** - Core purchase flow
4. **Order Management + Operations** - Essential post-purchase experience
5. **Address Management** - Required for shipping

Defer:
- **Points System**: Adds complexity; implement after core flow is stable
- **Video Module**: Nice-to-have engagement feature; not essential for MVP
- **Order Notifications**: Requires template message approval; can be added post-launch

## Admin Panel Features

| Module | Features | Complexity | Priority |
|--------|----------|------------|----------|
| **Product Management** | Category CRUD, Product CRUD, Inventory management, Price management | High | P0 - Required for catalog |
| **Order Management** | Order list, Order detail, Ship order, Handle refunds/returns | High | P0 - Required for fulfillment |
| **User Management** | User list, User detail view, Order history | Medium | P1 - Needed for support |
| **Content Management** | Banner management, Announcement management, Video management | Medium | P1 - Marketing needs |
| **Financial Management** | Transaction logs, Revenue statistics | Medium | P2 - Post-launch analytics |
| **System Management** | Admin accounts, Role permissions, Operation logs, System config | High | P0 - Security essential |
| **Help Center Management** | FAQ articles, Help categories | Low | P2 - Support efficiency |

## WeChat Mini Program Specific Features

| Feature | WeChat API | Implementation Notes |
|---------|------------|---------------------|
| User Login | wx.login, wx.getUserProfile | Obtain code, exchange for openid/session_key |
| Payment | wx.requestPayment | WeChat Pay integration, prepay_id required |
| Address Sync | wx.chooseAddress | Use WeChat's saved addresses |
| Customer Service | wx.openCustomerServiceChat | Built-in chat interface |
| Subscription Messages | Template messages | Requires user authorization, template approval |
| Share | onShareAppMessage | Share product/order to friends/groups |
| Scan Code | wx.scanCode | Could be used for product lookup (future) |

## Points/Rewards System Design

**Non-distribution points system** (simplified from removed distribution features):

| Aspect | Design |
|--------|--------|
| Earning Rules | Points earned per purchase (e.g., 1 point per 10 CNY spent) |
| Redemption | Use points in "Gift Zone" for exclusive products |
| Points Balance | Display in Personal Center |
| Transaction History | Points earned/spent log in user account |
| No Tiers | Simple points balance, no member levels |
| No Referrals | Points from own purchases only |

## Mobile-Specific UX Considerations

| Consideration | Recommendation |
|---------------|----------------|
| Screen Size | Compact layouts, efficient use of vertical space |
| Touch Navigation | Large tap targets, swipe gestures for carousel/cart |
| Loading States | Skeleton screens, pull-to-refresh |
| Error Handling | Clear error messages, retry buttons |
| Offline Support | Basic caching for product images, error pages |
| Performance | Lazy loading for images, virtual lists for long product lists |
| WeChat Environment | Follow WeChat design guidelines, native feel |

## Sources

- WeChat Mini Program Official Demo (github.com/wechat-miniprogram/miniprogram-demo) - Component and API examples
- WeChat Mini Program Cloud Development Documentation - Authentication, database, storage patterns
- PROJECT.md - Project scope and feature requirements (distribution features removed)
- Confidence: MEDIUM - Based on WeChat official demo structure and project requirements; web search results were unavailable due to network restrictions

---

## Confidence Assessment

| Area | Confidence | Reason |
|------|------------|--------|
| Table Stakes | HIGH | Standard e-commerce features, well-documented in WeChat ecosystem |
| Differentiators | MEDIUM | Based on project scope; some features like points system design may need validation |
| Anti-Features | HIGH | Explicitly documented in PROJECT.md |
| Dependencies | HIGH | Standard e-commerce flow, clear relationships |
| Admin Features | MEDIUM | Standard patterns, specific complexity depends on implementation details |

## Gaps to Address

1. **Points System Specifics**: Exact earning rates, redemption rules, product eligibility need business input
2. **Video Module Scope**: Number of videos, hosting (cloud storage vs CDN), moderation requirements unclear
3. **Financial Module Detail**: Refund handling logic, financial reporting requirements need clarification
4. **Performance Benchmarks**: "1000 concurrent users" and "2s first screen" targets may require specific architecture decisions
5. **Moderation Needs**: User-generated content (if any), video content - platform liability considerations