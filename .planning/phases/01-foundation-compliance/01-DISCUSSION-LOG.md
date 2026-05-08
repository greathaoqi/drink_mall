# Phase 1: Foundation & Compliance - Discussion Log

> **Audit trail only.** Do not use as input to planning, research, or execution agents.
> Decisions are captured in CONTEXT.md — this log preserves the alternatives considered.

**Date:** 2026-05-08
**Phase:** 01-foundation-compliance
**Areas discussed:** Age Verification Flow, Guest Mode Boundaries, WeChat Session Strategy, Database Tables Scope

---

## Age Verification Flow

### Timing Decision

| Option | Description | Selected |
|--------|-------------|----------|
| Verify once, store in profile | Simpler UX, re-verify on reinstall | ✓ |
| Verify each purchase | Maximum compliance, friction on every order | |
| Verify per session | Balance, need session storage | |

**User's choice:** Verify once, store in profile (Recommended)
**Notes:** User accepted recommendation for simpler UX

### Verification Method

| Option | Description | Selected |
|--------|-------------|----------|
| Checkbox confirmation "I am 18+" | Compliant, familiar pattern | ✓ |
| ID upload | More rigorous, complex, privacy concerns | |
| WeChat real-name | Leverage verified identity if available | |

**User's choice:** Checkbox confirmation "I am 18+" (Recommended)
**Notes:** Self-declaration accepted by most regulators; ID upload considered overkill for MVP

### Trigger Timing

| Option | Description | Selected |
|--------|-------------|----------|
| On app launch (first-time) | Maximum protection | |
| On alcohol zone entry | Less intrusive, verify when needed | ✓ |
| On first alcohol purchase | Just-in-time, adds friction at checkout | |

**User's choice:** On alcohol zone entry (Recommended)
**Notes:** Balance between compliance and UX

### Re-verification Policy

| Option | Description | Selected |
|--------|-------------|----------|
| Never re-verify | Simpler, no expiration | |
| Re-verify on reinstall | Handles device changes naturally | ✓ |
| Re-verify annually | Time-based expiration, periodic friction | |

**User's choice:** Re-verify on reinstall (Recommended)
**Notes:** Natural handling of device/user changes without arbitrary time limits

### Storage Location

| Option | Description | Selected |
|--------|-------------|----------|
| User profile field | Persistent and queryable | ✓ |
| Local storage | Faster but lost on reinstall | |
| Session flag only | Requires login first, not persistent | |

**User's choice:** User profile field (Recommended)
**Notes:** `age_verified` boolean + `age_verified_at` timestamp in user table

---

## Guest Mode Boundaries

### Accessible Content

| Option | Description | Selected |
|--------|-------------|----------|
| Full browsing access | Homepage, products, details, announcements, videos | ✓ |
| Limited browsing | Homepage and list only | |
| Homepage only | Strictest, require login for products | |

**User's choice:** Full browsing access (Recommended)
**Notes:** User can explore freely before committing to login

### Login Trigger

| Option | Description | Selected |
|--------|-------------|----------|
| On action (cart/checkout/profile) | Clear trigger points | ✓ |
| On alcohol zone entry | Catch earlier, may seem aggressive | |
| After N minutes | Time-based, unusual pattern | |

**User's choice:** On action (cart/checkout/profile) (Recommended)
**Notes:** Just-in-time login when user tries restricted actions

### Prompt Style

| Option | Description | Selected |
|--------|-------------|----------|
| Modal popup | Clear action, can dismiss | ✓ |
| Bottom sheet | Mobile-native feel, obscures content | |
| Inline message | Replace button, less intrusive | |

**User's choice:** Modal popup (Recommended)
**Notes:** Standard pattern for login prompts

### Dismissal Policy

| Option | Description | Selected |
|--------|-------------|----------|
| Allow dismissal | User can close and continue browsing | ✓ |
| Force login | No dismiss option | |

**User's choice:** Allow dismissal (Recommended)
**Notes:** Less pushy, standard e-commerce pattern

---

## WeChat Session Strategy

### Session Duration

| Option | Description | Selected |
|--------|-------------|----------|
| 7 days | Long-lived, better retention | ✓ |
| 24 hours | Balance security and convenience | |
| 30 days | Maximum convenience | |

**User's choice:** 7 days (Recommended)
**Notes:** Balance for e-commerce where users return frequently

### Token Storage

| Option | Description | Selected |
|--------|-------------|----------|
| httpOnly cookie | Better XSS protection, automatic handling | ✓ |
| localStorage | Simpler, vulnerable to XSS | |
| Both | JWT + refresh token, complex | |

**User's choice:** httpOnly cookie (Recommended)
**Notes:** Safer for mini program context

### Login Denial Handling

| Option | Description | Selected |
|--------|-------------|----------|
| Stay as guest | Graceful UX, try again later | ✓ |
| Show error message | More aggressive | |
| Retry automatically | Could frustrate user | |

**User's choice:** Stay as guest (Recommended)
**Notes:** Matches guest-first design approach

### WeChat Identifier

| Option | Description | Selected |
|--------|-------------|----------|
| openid only | Standard for single-app | ✓ |
| Both openid and unionid | Cross-app identification | |
| unionid only | Unusual for single-app | |

**User's choice:** openid only (Recommended)
**Notes:** Standard for single mini program; unionid requires extra approval

---

## Database Tables Scope

### Phase 1 Tables

| Option | Description | Selected |
|--------|-------------|----------|
| Minimal: users only | Fastest setup | |
| Core: users + categories | Prepare for Phase 2 | |
| Full foundation | users, categories, products, banners | ✓ |

**User's choice:** Full foundation
**Notes:** Build ahead for smoother Phase 2 transition

### User Table Fields

| Option | Description | Selected |
|--------|-------------|----------|
| Auth essentials | openid, nickname, avatar_url, age_verified, timestamps | |
| Extended profile | Add phone, balance, points fields | ✓ |
| Minimal skeleton | openid + timestamps only | |

**User's choice:** Extended profile
**Notes:** Prepare for Phase 3/4 features (balance, points) now

### Admin Users Table

| Option | Description | Selected |
|--------|-------------|----------|
| Yes, include now | Prepare for Phase 5 | ✓ |
| No, defer to Phase 5 | Focus on mini program auth | |

**User's choice:** Yes, include now (Recommended)
**Notes:** Minimal extra effort during foundation setup

### Seed Data

| Option | Description | Selected |
|--------|-------------|----------|
| Yes, sample data | Categories, products for testing | ✓ |
| No, empty tables | Pure schema setup | |

**User's choice:** Yes, sample data (Recommended)
**Notes:** Validate Phase 1 guest browsing end-to-end

---

## Claude's Discretion

(None — all decisions were user-driven with recommendations accepted)

## Deferred Ideas

(None — discussion stayed within phase scope)
