# Roadmap: 酒水商城 (Drink Mall)

**Mode:** MVP (Vertical MVP - each phase delivers end-to-end user capability)
**Granularity:** Standard (5-8 phases)
**Created:** 2026-05-08

---

## Project Goal

用户可以方便地在微信小程序中浏览和购买酒水商品，享受流畅的购物体验，平台可以高效管理商品和订单。

---

## Phases

- [ ] **Phase 1: Foundation & Compliance** - User authentication, age verification, database setup, customer service entry
- [ ] **Phase 2: Core Shopping** - Product catalog, shopping cart, address management, basic admin operations
- [ ] **Phase 3: Orders & Payment** - Checkout flow, WeChat Pay integration, order management, financial basics
- [ ] **Phase 4: Content & Engagement** - Banners, announcements, videos, points system, gift zone
- [ ] **Phase 5: Operations & Polish** - System management, financial reports, deployment and optimization
- [ ] **Phase 6: Content Payment & Monetization** - Real payment integration, content search, categories, engagement, purchase management and analytics

---

## Phase Details

### Phase 1: Foundation & Compliance

**Goal:** Users can securely access the mini program with legal compliance for alcohol sales

**Depends on:** Nothing (first phase)

**Requirements:** AUTH-01, AUTH-02, AUTH-03, AUTH-04, USER-07

**Success Criteria** (what must be TRUE):
1. User can log in via WeChat one-click authorization
2. User can browse public content (homepage, product list, announcements) without logging in
3. User must check agreement checkboxes before first login
4. User must verify age (18+) before purchasing alcohol
5. User can contact customer service from any page

**Plans:** 5 plans

Plans:
- [ ] 01-01-PLAN.md - Backend project initialization with Spring Boot, Sa-Token, WxJava, MyBatis-Plus
- [ ] 01-02-PLAN.md - Database schema and entity classes with mappers
- [ ] 01-03-PLAN.md - Authentication endpoints (login/logout/age verification)
- [ ] 01-04-PLAN.md - Mini Program foundation with auth components
- [ ] 01-05-PLAN.md - Login/home/profile pages with customer service

**UI hint:** yes

---

### Phase 2: Core Shopping

**Goal:** Users can browse products, manage cart, and prepare for checkout

**Depends on:** Phase 1

**Requirements:** PROD-01, PROD-02, PROD-03, PROD-04, PROD-05, PROD-06, ZONE-01, ZONE-02, CART-01, CART-02, CART-03, CART-04, CART-05, ADDR-01, ADDR-02, ADDR-03, ADDR-04, ADDR-05, USER-01, USER-02, ADM-PROD-01, ADM-PROD-02, ADM-PROD-03, ADM-PROD-04, ADM-PROD-05, ADM-USER-01, ADM-USER-02, ADM-USER-03

**Success Criteria** (what must be TRUE):
1. User can browse products by category (multi-level) and zone (Main, Retail)
2. User can search products by keyword and filter by price range or sort by sales
3. User can add products to cart, modify quantities, and see total price
4. User can manage delivery addresses (add, edit, delete, set default)
5. Admin can manage products, categories, and view user list

**Plans:** TBD

**UI hint:** yes

---

### Phase 3: Orders & Payment

**Goal:** Users can complete purchases with secure payment and manage their orders

**Depends on:** Phase 2

**Requirements:** CHECK-01, CHECK-02, CHECK-03, CHECK-04, CHECK-05, CHECK-06, ORDER-01, ORDER-02, ORDER-03, ORDER-04, ORDER-05, USER-03, USER-04, USER-05, ADM-ORDER-01, ADM-ORDER-02, ADM-ORDER-03, ADM-ORDER-04, ADM-ORDER-05, ADM-FIN-01

**Success Criteria** (what must be TRUE):
1. User can checkout with selected items, address, and see total amount (free shipping)
2. User can pay via WeChat Pay and see order status update automatically
3. Unpaid orders are auto-cancelled after timeout and inventory released
4. User can view order list by status and order details with logistics info
5. User can cancel unpaid orders, confirm receipt, and apply for after-sales
6. Admin can process orders (ship, cancel, modify price) and handle after-sales requests

**Plans:** TBD

**UI hint:** yes

---

### Phase 4: Content & Engagement

**Goal:** Users can discover content, earn and redeem points for gift products

**Depends on:** Phase 3

**Requirements:** CONT-01, CONT-02, CONT-03, CONT-04, CONT-05, CONT-06, ZONE-03, POINTS-01, POINTS-02, POINTS-03, POINTS-04, USER-06, ADM-CONT-01, ADM-CONT-02, ADM-CONT-03, ADM-CONT-04

**Success Criteria** (what must be TRUE):
1. User sees rotating banners on homepage and can click to navigate to linked pages
2. User can view platform announcements with full content details
3. User can browse and play videos with progress control and fullscreen mode
4. User earns points from purchases and can redeem points for gift zone products
5. User sees error message when attempting redemption with insufficient points
6. Admin can manage all content: banners, announcements, videos, help center

**Plans:** TBD

**UI hint:** yes

---

### Phase 5: Operations & Polish

**Goal:** Platform is fully operational with complete admin control and optimized performance

**Depends on:** Phase 4

**Requirements:** ADM-FIN-02, ADM-FIN-03, ADM-SYS-01, ADM-SYS-02, ADM-SYS-03, ADM-SYS-04

**Success Criteria** (what must be TRUE):
1. Admin can view complete financial reports: balance, points, withdrawal records
2. Admin can manage admin accounts and configure role-based permissions
3. All admin operations are logged with audit trail
4. Admin can configure system parameters
5. Platform passes load testing for 1000 concurrent users with performance targets met

**Plans:** TBD

**UI hint:** yes

---

### Phase 6: Content Payment & Monetization

**Goal:** Users can purchase paid content with real payment methods, admins can manage content purchases and analytics, and the content center supports search, categories, engagement, and preview

**Depends on:** Phase 4

**Requirements:** CPAY-01, CPAY-02, CPAY-03, CPAY-04, CPAY-05, CPAY-06, CPAY-07

**Success Criteria** (what must be TRUE):
1. User can purchase paid content via real WeChat Pay integration
2. User can search content by keyword and browse by dynamic categories
3. User can like/unlike content and see engagement counts
4. User can preview teaser content before purchasing
5. Admin can view content purchase history with user, content, price, payment method details
6. Admin can see content purchase analytics and revenue reports

**Plans:** 8 plans

Plans:
- [ ] 06-01-PLAN.md - Database schema for content payment, categories, likes
- [ ] 06-02-PLAN.md - Content payment flow with WeChat Pay integration
- [ ] 06-03-PLAN.md - Content keyword search endpoint
- [ ] 06-04-PLAN.md - Dynamic content category management (admin CRUD + public endpoint)
- [ ] 06-05-PLAN.md - Content preview/teaser for unpurchased content
- [ ] 06-06-PLAN.md - Like/unlike functionality with atomic counts
- [ ] 06-07-PLAN.md - Admin purchase records and analytics
- [ ] 06-08-PLAN.md - Mini Program content UI updates

**Wave Structure:**
- Wave 1: 06-01 (database foundation)
- Wave 2 *(blocked on Wave 1)*: 06-02, 06-03, 06-04 (payment, search, categories)
- Wave 3 *(blocked on Wave 2)*: 06-05, 06-06 (preview, likes)
- Wave 4 *(blocked on Wave 3)*: 06-07 (admin analytics)
- Wave 5 *(blocked on Wave 4)*: 06-08 (Mini Program UI)

**Cross-cutting constraints:**
- All payment operations use CP prefix for order dispatch
- All like operations check purchase status before allowing
- All search queries filter by status=1 (published only)

**UI hint:** yes

---

## Progress

| Phase | Plans Complete | Status | Completed |
|-------|----------------|--------|-----------|
| 1. Foundation & Compliance | 5/5 | Ready to execute | - |
| 2. Core Shopping | 0/1 | Not started | - |
| 3. Orders & Payment | 0/1 | Not started | - |
| 4. Content & Engagement | 0/1 | Not started | - |
| 5. Operations & Polish | 0/1 | Not started | - |
| 6. Content Payment & Monetization | 8/8 | Ready to execute | - |

---

## Coverage

| Requirement | Phase | Status |
|-------------|-------|--------|
| AUTH-01 | Phase 1 | Covered in 01-03, 01-04, 01-05 |
| AUTH-02 | Phase 1 | Covered in 01-03, 01-05 |
| AUTH-03 | Phase 1 | Covered in 01-03, 01-04, 01-05 |
| AUTH-04 | Phase 1 | Covered in 01-02, 01-03, 01-04 |
| USER-07 | Phase 1 | Covered in 01-05 |
| PROD-01 | Phase 2 | Pending |
| PROD-02 | Phase 2 | Pending |
| PROD-03 | Phase 2 | Pending |
| PROD-04 | Phase 2 | Pending |
| PROD-05 | Phase 2 | Pending |
| PROD-06 | Phase 2 | Pending |
| ZONE-01 | Phase 2 | Pending |
| ZONE-02 | Phase 2 | Pending |
| CART-01 | Phase 2 | Pending |
| CART-02 | Phase 2 | Pending |
| CART-03 | Phase 2 | Pending |
| CART-04 | Phase 2 | Pending |
| CART-05 | Phase 2 | Pending |
| ADDR-01 | Phase 2 | Pending |
| ADDR-02 | Phase 2 | Pending |
| ADDR-03 | Phase 2 | Pending |
| ADDR-04 | Phase 2 | Pending |
| ADDR-05 | Phase 2 | Pending |
| USER-01 | Phase 2 | Pending |
| USER-02 | Phase 2 | Pending |
| ADM-PROD-01 | Phase 2 | Pending |
| ADM-PROD-02 | Phase 2 | Pending |
| ADM-PROD-03 | Phase 2 | Pending |
| ADM-PROD-04 | Phase 2 | Pending |
| ADM-PROD-05 | Phase 2 | Pending |
| ADM-USER-01 | Phase 2 | Pending |
| ADM-USER-02 | Phase 2 | Pending |
| ADM-USER-03 | Phase 2 | Pending |
| CHECK-01 | Phase 3 | Pending |
| CHECK-02 | Phase 3 | Pending |
| CHECK-03 | Phase 3 | Pending |
| CHECK-04 | Phase 3 | Pending |
| CHECK-05 | Phase 3 | Pending |
| CHECK-06 | Phase 3 | Pending |
| ORDER-01 | Phase 3 | Pending |
| ORDER-02 | Phase 3 | Pending |
| ORDER-03 | Phase 3 | Pending |
| ORDER-04 | Phase 3 | Pending |
| ORDER-05 | Phase 3 | Pending |
| USER-03 | Phase 3 | Pending |
| USER-04 | Phase 3 | Pending |
| USER-05 | Phase 3 | Pending |
| ADM-ORDER-01 | Phase 3 | Pending |
| ADM-ORDER-02 | Phase 3 | Pending |
| ADM-ORDER-03 | Phase 3 | Pending |
| ADM-ORDER-04 | Phase 3 | Pending |
| ADM-ORDER-05 | Phase 3 | Pending |
| ADM-FIN-01 | Phase 3 | Pending |
| CONT-01 | Phase 4 | Pending |
| CONT-02 | Phase 4 | Pending |
| CONT-03 | Phase 4 | Pending |
| CONT-04 | Phase 4 | Pending |
| CONT-05 | Phase 4 | Pending |
| CONT-06 | Phase 4 | Pending |
| ZONE-03 | Phase 4 | Pending |
| POINTS-01 | Phase 4 | Pending |
| POINTS-02 | Phase 4 | Pending |
| POINTS-03 | Phase 4 | Pending |
| POINTS-04 | Phase 4 | Pending |
| USER-06 | Phase 4 | Pending |
| ADM-CONT-01 | Phase 4 | Pending |
| ADM-CONT-02 | Phase 4 | Pending |
| ADM-CONT-03 | Phase 4 | Pending |
| ADM-CONT-04 | Phase 4 | Pending |
| ADM-FIN-02 | Phase 5 | Pending |
| ADM-FIN-03 | Phase 5 | Pending |
| ADM-SYS-01 | Phase 5 | Pending |
| ADM-SYS-02 | Phase 5 | Pending |
| ADM-SYS-03 | Phase 5 | Pending |
| ADM-SYS-04 | Phase 5 | Pending |
| CPAY-01 | Phase 6 | Covered in 06-01, 06-02 |
| CPAY-02 | Phase 6 | Covered in 06-07 |
| CPAY-03 | Phase 6 | Covered in 06-03, 06-08 |
| CPAY-04 | Phase 6 | Covered in 06-01, 06-04 |
| CPAY-05 | Phase 6 | Covered in 06-05, 06-08 |
| CPAY-06 | Phase 6 | Covered in 06-01, 06-06, 06-08 |
| CPAY-07 | Phase 6 | Covered in 06-07 |

**Total:** 75/75 requirements mapped

---

## Critical Path

```
Phase 1 (Foundation) -> Phase 2 (Core Shopping) -> Phase 3 (Orders & Payment)
                                                           |
                                                           v
Phase 5 (Operations) <- Phase 4 (Content & Engagement) <---+
                                                           |
                                                           v
                                                   Phase 6 (Content Payment)
```

---

## Risk Notes

| Phase | Risk Level | Notes |
|-------|------------|-------|
| Phase 1 | MEDIUM | WeChat approval process, age verification compliance |
| Phase 2 | LOW | Standard e-commerce patterns |
| Phase 3 | HIGH | Payment security critical, WeChat Pay callback verification required |
| Phase 4 | MEDIUM | Points system rules need clarification |
| Phase 5 | LOW | Operations and deployment |
| Phase 6 | MEDIUM | WeChat Pay integration for content, purchase idempotency, content access control |

---

*Roadmap created: 2026-05-08*
*Last updated: 2026-05-18 after phase 6 planning*
