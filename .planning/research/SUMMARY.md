# Research Summary: 酒水商城 (Drink Mall)

**Project:** WeChat Mini Program E-commerce for Alcohol/Liquor
**Synthesized:** 2026-05-08
**Status:** Ready for Roadmap

---

## Executive Summary

This project is a WeChat Mini Program e-commerce platform for alcohol/liquor retail in the Chinese market. The recommended architecture follows proven patterns from the WeChat ecosystem: UniApp (Vue 3) for the mini program frontend, Spring Boot 3 with MyBatis-Plus for the backend API, MySQL + Redis for data persistence and caching, and Element Plus for the admin dashboard.

The core shopping experience (browse, cart, checkout, orders) is standard and well-documented, making MVP delivery low-risk. The key differentiators for this platform are the multi-zone product layout (Main Zone, Retail Zone, Gift Zone with points redemption) and video content engagement features.

**Critical success factors:**
1. **Legal compliance** - Age verification, alcohol sales licenses, and WeChat审核 approval are prerequisites for launch
2. **Payment security** - WeChat Pay callback verification must be implemented correctly from day one
3. **Inventory management** - Optimistic locking to prevent overselling during concurrent purchases

---

## Key Findings

### From STACK.md

**Core Technologies:**
- **UniApp 4.x + Vue 3.4+** - Cross-platform mini program framework with largest Chinese ecosystem
- **Spring Boot 3.3.x** - Industry standard for Java enterprise e-commerce
- **MyBatis-Plus 3.5.5+** - Reduces boilerplate while maintaining SQL control
- **MySQL 8.0+** - Proven for e-commerce ACID transactions
- **Redis 7.2+** - Shopping cart, inventory caching, distributed locks
- **WxJava 4.6+** - Official Java SDK for WeChat integration
- **Element Plus 2.6+** - Most mature Vue 3 admin UI

**Critical Version Requirements:**
- Java 17+ required for Spring Boot 3.x
- Node.js 18+ for Vite 5.x build tooling
- MySQL 8.0+ for JSON support and CTEs

### From FEATURES.md

**Table Stakes (Must-Have):**
- WeChat Login (one-click auth via openid)
- Product Catalog with multi-level categories
- Product Search with filters (price, sales, brand)
- Shopping Cart with quantity management
- Checkout Flow with address selection
- WeChat Pay integration
- Order Management (view, cancel, refund, confirm)
- Address Management
- Customer Service button

**Differentiators (Should-Have):**
- Points Exchange System with Gift Zone redemption
- Video Content Module for engagement
- Platform Announcements
- Banner Carousel on homepage
- Multi-Zone Layout (Main, Retail, Gift zones)

**Explicitly Removed (Anti-Features):**
- Distribution/Referral System
- DF Balance Account
- Multi-Platform Support (WeChat only)
- Warehouse Management (simple admin stock only)

### From ARCHITECTURE.md

**Major Components:**
- **WeChat Mini Program Frontend** - User shopping experience
- **Backend API** - Business logic (User, Product, Order, Content, Payment services)
- **Admin Dashboard** - Management operations (Vue 3 + Element Plus)
- **Database Layer** - MySQL for persistence, Redis for caching

**Key Architecture Decisions:**
- Monolithic backend for simplicity at 1000 concurrent users scale
- JWT authentication with Sa-Token
- Optimistic locking for inventory
- Tencent Cloud deployment for WeChat ecosystem integration

### From PITFALLS.md

**Critical Pitfalls:**
1. **WeChat审核拒绝** - Missing alcohol sales licenses, no age verification
2. **支付回调验证缺失** - Trust client-side instead of server callback verification
3. **库存超卖** - Race conditions during concurrent purchases
4. **未成年人购酒** - Legal liability up to 100,000 CNY fines
5. **setData性能问题** - Large data transfers between JS and render threads

---

## Implications for Roadmap

### Suggested Phase Structure (5 phases, 10 weeks)

#### Phase 1: Foundation & Compliance (Weeks 1-2)
- WeChat authentication flow
- Age verification modal (18+)
- Database setup with core tables
- Backend API skeleton
- Mini Program shell
- **Risk Level:** MEDIUM (approval risk)

#### Phase 2: Core Shopping Experience (Weeks 3-4)
- Product Catalog (categories, list, detail)
- Product Search with filters
- Shopping Cart
- Address Management
- Admin: Product/Category CRUD
- **Risk Level:** LOW

#### Phase 3: Orders & Payment (Weeks 5-6)
- Checkout Flow
- WeChat Pay integration
- Order Creation and Management
- Order Operations (cancel, refund, confirm)
- Admin: Order management, ship orders
- **Risk Level:** HIGH (security critical)

#### Phase 4: Content & Engagement (Weeks 7-8)
- Banner Carousel
- Platform Announcements
- Video Content Module
- Points Exchange System
- Admin: Content management
- **Risk Level:** MEDIUM

#### Phase 5: Operations & Optimization (Weeks 9-10)
- Admin: Role-based permissions
- Financial reports and statistics
- Performance tuning
- Load testing and deployment
- **Risk Level:** LOW

---

## Research Flags

**Needs additional research during planning:**
- Phase 1: License requirements, WeChat审核 process, age verification options
- Phase 4: Points earning rates, redemption rules

**Standard patterns (no additional research needed):**
- Phase 2: Product catalog, cart
- Phase 3: Orders, payment (follow WxJava SDK)
- Phase 5: Operations and deployment

---

## Confidence Assessment

| Area | Confidence | Notes |
|------|------------|-------|
| Stack | HIGH | UniApp + Spring Boot proven for Chinese e-commerce |
| Features | HIGH | Standard e-commerce, anti-features clearly defined |
| Architecture | MEDIUM | WeChat API specifics may need verification |
| Pitfalls | MEDIUM-HIGH | Technical pitfalls documented; legal needs counsel |

**Overall Confidence:** MEDIUM-HIGH

---

## Gaps to Address

**During Planning:**
1. Legal compliance checklist - Verify license requirements
2. Points system rules - Confirm earning rates and redemption logic
3. Video module scope - Hosting strategy, moderation needs
4. Performance targets - "1000 concurrent" and "2s first screen" validation

**During Implementation:**
1. WxJava version verification
2. WeChat审核 timeline buffer
3. Tencent Cloud configuration

---

## Recommendations for Roadmapper

1. **Parallelize legal prep** - License acquisition takes 2-4 weeks; start before development
2. **Phase 3 is critical** - Budget extra time for payment flow testing
3. **Consider MVP scope** - Points and videos could be deferred post-launch
4. **Admin panel matters** - Invest in usability testing with operators

---

**Synthesis complete. Ready for roadmap creation.**
