# Phase 1: Foundation & Compliance - Context

**Gathered:** 2026-05-08
**Status:** Ready for planning

## Phase Boundary

Users can securely access the mini program with legal compliance for alcohol sales. This phase delivers WeChat authentication flow, age verification (18+), guest browsing capability, customer service entry point, and database foundation for subsequent phases.

**In scope:**
- WeChat one-click login (AUTH-01)
- Guest browsing of public content (AUTH-02)
- Agreement checkboxes before login (AUTH-03)
- Age verification for alcohol purchases (AUTH-04)
- Customer service entry point (USER-07)
- Database schema foundation

**Out of scope:**
- Product catalog browsing (Phase 2)
- Shopping cart functionality (Phase 2)
- Payment integration (Phase 3)
- Content management (Phase 4)
- Admin dashboard (Phase 5)

## Implementation Decisions

### Age Verification Flow
- **D-01:** Verify once per user, store verification status persistently in user profile
- **D-02:** Verification method: Checkbox confirmation "I am 18+" (self-declaration)
- **D-03:** Trigger timing: Prompt when user enters alcohol-related product zones (not at app launch)
- **D-04:** Re-verification: Prompt again if user uninstalls and reinstalls the mini program (handles device changes naturally)
- **D-05:** Storage location: User profile fields (`age_verified` boolean + `age_verified_at` timestamp)

### Guest Mode Boundaries
- **D-06:** Guest access scope: Full browsing access (homepage, product list, product detail, announcements, videos) without login
- **D-07:** Login trigger: Prompt login when user attempts restricted actions (add to cart, checkout, access profile)
- **D-08:** Prompt style: Modal popup overlay with login button
- **D-09:** Prompt dismissal: Allow user to close prompt and continue browsing (not forced login)

### WeChat Session Strategy
- **D-10:** Session duration: 7 days (long-lived for better retention)
- **D-11:** Token storage: httpOnly cookie (better XSS protection, automatic handling)
- **D-12:** Login denial handling: User stays as guest if they deny WeChat authorization (graceful UX)
- **D-13:** WeChat identifier: Store `openid` only (standard for single-app mini programs)

### Database Tables Scope
- **D-14:** Phase 1 tables: Full foundation schema (users, categories, products skeleton, banners)
- **D-15:** User table fields: Extended profile (openid, nickname, avatar_url, phone, balance, points, age_verified, age_verified_at, status, created_at, updated_at)
- **D-16:** Include `admin_users` table in Phase 1 foundation (prepare for Phase 5)
- **D-17:** Create sample seed data for demo/testing (sample categories, products to validate guest browsing)

### Claude's Discretion
None — all decisions were user-driven with recommendations accepted.

## Canonical References

**Downstream agents MUST read these before planning or implementing.**

### Project Context
- `.planning/PROJECT.md` — Project overview, core value, constraints, key decisions (distribution features removed)
- `.planning/REQUIREMENTS.md` — Full v1 requirements with traceability (AUTH-01 through AUTH-04, USER-07 for this phase)
- `.planning/ROADMAP.md` — Phase structure, dependencies, success criteria for Phase 1

### Research Findings
- `.planning/research/SUMMARY.md` — Executive summary, critical success factors, confidence assessment
- `.planning/research/STACK.md` — Technology stack decisions (UniApp, Spring Boot, MyBatis-Plus, Sa-Token, WxJava)
- `.planning/research/ARCHITECTURE.md` — Architecture patterns, auth flow diagrams, WeChat Pay integration
- `.planning/research/PITFALLS.md` — Critical pitfalls (age verification compliance, payment callback verification)

### External Documentation (via Context7 during planning)
- WxJava SDK documentation for WeChat login implementation
- Sa-Token documentation for JWT session management
- UniApp documentation for mini program auth flow

## Existing Code Insights

### Reusable Assets
(None yet — greenfield project)

### Established Patterns
(None yet — this is the foundation phase)

### Integration Points
- Backend API skeleton: `/api/v1/auth/*` endpoints for login/logout
- Mini Program pages: Home, Product List, Product Detail, Profile entry
- WeChat SDK integration point: `wx.login()` → backend → openid retrieval

## Specific Ideas

- Age verification modal should appear on zone entry, not app launch, for less intrusive UX
- Guest users should see full product catalog to encourage conversion
- Session 7 days balances security with convenience for e-commerce

## Deferred Ideas

(None — discussion stayed within phase scope)

---

*Phase: 01-foundation-compliance*
*Context gathered: 2026-05-08*