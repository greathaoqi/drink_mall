# Architecture Handoff: Drink Mall

Generated for the next agent from a read-only architecture review on 2026-05-13.

Important: `.planning/STATE.md` claims the MVP is complete, but this review found major gaps against the current phase-one PRD. Treat this file as the fresher architecture snapshot unless a later review supersedes it.

No code was modified during the review.

## Repository Structure

- Mini program: `drink-mall-mini/`
  - Source: `drink-mall-mini/src/`
  - Pages and UniApp routing: `drink-mall-mini/src/pages.json`
  - App manifest: `drink-mall-mini/src/manifest.json`
  - Request wrapper: `drink-mall-mini/src/utils/request.ts`
  - User store: `drink-mall-mini/src/store/user.ts`
- PC admin: `drink-mall-admin/`
  - Source: `drink-mall-admin/src/`
  - Router: `drink-mall-admin/src/router/index.ts`
  - Request wrapper: `drink-mall-admin/src/utils/request.ts`
- Backend: `drink-mall-api/`
  - Source: `drink-mall-api/src/main/java/com/drinkmall/`
  - Controllers: `controller/` and `controller/admin/`
  - Services: `service/`, `service/impl/`, `service/admin/`
  - Entities and mappers: `entity/`, `mapper/`
  - Config: `config/`
  - Scheduler: `scheduler/`
- Database migrations: `drink-mall-api/src/main/resources/db/migration/`
  - Flyway migrations currently `V1` through `V12`.
- Runtime config:
  - Backend: `drink-mall-api/src/main/resources/application.yml`, `application-dev.yml`
  - Admin Vite: `drink-mall-admin/vite.config.ts`
  - Mini Vite: `drink-mall-mini/vite.config.ts`
  - Docker: `docker-compose.yml`
- Tests:
  - Backend test directory exists: `drink-mall-api/src/test/`
  - Only reviewed test found: `drink-mall-api/src/test/java/com/drinkmall/service/impl/UserServiceImplTest.java`
  - No project-level mini-program or admin test directory found.

## Technology Stack

- Mini program:
  - UniApp + Vue 3 + TypeScript + Vite.
  - Pinia for state.
  - Routing through `pages.json`.
  - UI dependencies: `@dcloudio/uni-ui`, `uview-plus`.
  - HTTP wrapper uses `uni.request` and adds `Authorization` from Pinia/local storage.
- Admin:
  - Vue 3 + Vite + TypeScript.
  - Element Plus and `@element-plus/icons-vue`.
  - Pinia dependency present.
  - Vue Router with a local-storage token guard.
  - Axios request wrapper.
- Backend:
  - Spring Boot `3.3.2`, Java 17.
  - MyBatis-Plus `3.5.5`.
  - MySQL Connector `8.0.33`.
  - Flyway MySQL.
  - Redis starter.
  - Sa-Token `1.37.0` with JWT.
  - WxJava MiniApp SDK `4.6.0`.
  - Hutool and Lombok.
- Build/start:
  - Backend: `mvn spring-boot:run`, `mvn package`.
  - Mini: `npm run dev:mp-weixin`, `npm run build:mp-weixin`.
  - Admin: `npm run dev`, `npm run build`.
  - Docker Compose can start MySQL, Redis, backend, admin, and Caddy.

## Current Implemented Capabilities

### Login and Registration

Implemented:
- WeChat login endpoint and demo login.
- Agreement and privacy checks in `AuthServiceImpl`.
- Sa-Token login for users and admins.

Gaps:
- Mandatory referrer binding for ordinary new users was not found.
- Invite code input, share parameter binding, scan invite image binding were not found.
- Backend seed account/invite-code creation was not found.

### User

Implemented:
- User info, member center, profile update.
- Age verification flag.
- Admin user list/detail/export.

Gaps:
- PRD-required real-name authentication submission and manual audit were not found.
- User entity does not contain a complete referral-chain model.

### Product

Implemented:
- Category and product tables.
- Product list/detail.
- Product CRUD in admin.
- Stock updates and stock logs.
- `zone_type` exists on products.

Gaps:
- Four required zones are not fully represented. Current data/code shows `main`, `retail`, `gift`; investment/招商 zone isolation is not complete.
- Product available payment methods are not modeled as configurable rules.
- Zone-specific business rules are not isolated.

### Order

Implemented:
- Create order, list/detail, cancel, confirm receipt.
- Admin order list/detail, ship, cancel, modify price.
- Auto-cancel pending orders scheduler.
- Manual logistics company/tracking number fields exist.

Gaps:
- Status strings are scattered rather than centralized enum/constants.
- Order timeout is hardcoded in scheduler.
- Some stock restoration paths use direct field updates and need careful concurrency review.

### Payment

Implemented:
- Balance payment.
- WeChat callback endpoint.
- Payment method and payment number fields.

Gaps:
- WeChat callback signature verification was not found.
- Real prepay parameter generation was not found.
- Idempotency around payment/reward settlement is incomplete.
- Product-specific allowed payment methods are not enforced.

### Rewards

Implemented:
- Points accrual/redeem paths.
- Distribution level overview page/API exists, but it is more of a display stub.

Gaps:
- 推客升级, 联营商升级, 招商奖励, 扶商奖励, 广告奖励, 联营商复购佣金 were not found as complete settlement modules.
- Level names, targets, benefit text, and reward ratios are hardcoded in `UserServiceImpl`.
- No reward ledger or idempotent reward settlement model was found.

### Assets

Implemented:
- Balance, frozen balance, points.
- Balance logs and points logs.
- `df_balance` column added to users.

Gaps:
- Balance, DF, wine beans, points, and options are not modeled as independent accounts with independent ledgers.
- DF appears as a display field, not a ledger-backed asset.
- Wine beans and options were not found.
- Asset conversion/payment isolation rules are not centrally enforced.

### Real-Name Authentication

Implemented:
- Current code has age verification only.

Gaps:
- PRD-required real-name submission, admin manual review, review status, and audit records were not found.

### Withdrawal

Implemented:
- User withdrawal request.
- Admin withdrawal approve/reject.
- Balance/frozen balance fields.

Gaps:
- There are two withdrawal table names in migrations: `withdrawals` and `withdrawal`.
- User withdrawal request subtracts balance and adds frozen balance; admin approval subtracts balance again, which looks like a duplicate-deduction risk.
- Fee, minimum amount, offline remittance record, remittance evidence, and operation log coverage are incomplete.

### Aftersale

Implemented:
- User aftersale application.
- Admin approve/reject.
- Some duplicate table naming exists: `after_sales` and `aftersale`.

Gaps:
- Frozen period rule is not config-driven.
- Refund asset flow and logs are incomplete.
- Full online return/exchange logistics is not required by phase one, and was not found.

### Content

Implemented:
- Banners, announcements, videos, help articles.
- Admin CRUD for content.

Gaps:
- Paid content and content viewing permission controls were not found.

### Configuration

Implemented:
- `system_config` and `sys_config` both exist in migrations.
- Admin config page exists.
- Seed config includes order timeout, points rate, withdrawal min/max.

Gaps:
- Most PRD-required business rules are not actually driven by config.
- Existing code hardcodes level thresholds, level names, benefits, order timeout, and points rate.

### Admin Permission

Implemented:
- Admin login.
- `@SaCheckRole("admin")` on admin controllers.
- `admin` and `operator` roles appear in system service.

Gaps:
- Fine-grained permissions were not found.
- Deep referral-chain query permission/audit controls were not found.
- Operator role likely cannot access many admin APIs because controllers require `admin`.

### Operation Logs

Implemented:
- Operation log tables/entities/list endpoints exist.

Gaps:
- Key admin operations do not appear to write operation logs.
- Required fields such as before value, after value, reason, and business source are not consistently represented.
- Duplicate table naming exists: `operation_logs` and `operation_log`.

## PRD Gap Classification

### P0: Must Have for Phase-One Launch

- Mandatory referrer binding for ordinary registration.
- Seed account and invite-code management.
- Compliant mini-program team page: inviter, direct referrals, indirect referrals, total team count only.
- Independent asset accounts and ledgers for balance, DF, wine beans, points, options.
- Config-driven rules for levels, commissions, investment package amounts, withdrawal fees, frozen period, content permissions, payment methods.
- Four zone isolation: main product, investment, retail, gift.
- Real-name authentication with manual admin review.
- Reward settlement modules for 推客, 联营商, 招商, 扶商, 广告, and repeat-purchase commission.
- Withdrawal flow fix and offline remittance record.
- Admin operation logs for key actions.
- Status enums/constants for critical business states.

### P1: Important Before Stable Operation

- WeChat Pay prepay and callback signature verification.
- Idempotent payment and reward settlement.
- Content permission and paid content.
- Fine-grained admin permission model.
- Aftersale frozen-period enforcement and refund ledger flow.
- Old-system data migration staging capability.
- Database naming cleanup plan for duplicate migration artifacts.

### P2: Polish and Maintainability

- Replace demo fallback data in mini/admin pages.
- Fix Chinese encoding/garbled text in README, pages, comments, and seed SQL where needed.
- Add mini-program/admin build checks and automated tests.
- Add broader backend tests around asset, withdrawal, order, referral, and rewards.

## Recommended Implementation Order

### Phase 1: Data Model and Compliance Foundation

Goal:
- Add referral, invite-code, real-name auth, asset-account, asset-ledger, reward-ledger, config, and audit foundations.

Directories/modules:
- Backend `db/migration`, `entity`, `mapper`, `service`, `controller`, `controller/admin`.
- Admin `views/system`, `views/user`, `views/finance`.

Risk:
- Existing duplicate table names must be reconciled carefully.

Verification:
- Empty database migrates successfully.
- Ordinary registration cannot complete without referrer.
- Seed account can exist without referrer.
- Asset accounts are separate and ledger-backed.

### Phase 2: Referral Binding and Compliant Team Display

Goal:
- Support invite code, share link, product share link, and scanned invite image binding.
- Show only inviter, direct list, indirect list, and total team count in mini program.

Directories/modules:
- Mini `pages/login`, `pages/distribution`, `pages/profile`, `services/auth`.
- Backend `AuthService`, `UserService`, `DistributionController`.

Risk:
- Do not expose third-level or deeper member details in mini APIs.

Verification:
- Self-search entry without invite code blocks registration.
- Mini team API does not return third-level detail.

### Phase 3: Commerce Flow and Zone Isolation

Goal:
- Implement strict zone-specific rules for main, investment, retail, and gift zones.
- Enforce product payment-method config, order rules, stock, and aftersale rules.

Directories/modules:
- Backend `product`, `order`, `pay`, `aftersale`, `config`.
- Mini `product`, `cart`, `checkout`, `order`.
- Admin `product`, `order`.

Risk:
- Payment, stock, and order state transitions must be idempotent and transactional.

Verification:
- Each zone can be tested with allowed and disallowed payment methods.
- Duplicate payment callback does not repeat state changes.

### Phase 4: Rewards and Assets

Goal:
- Build reward calculation and settlement with config versions, ledger entries, freeze/unfreeze, and auditability.

Directories/modules:
- Backend likely needs new `reward`, `asset`, `member`, `audit` packages or equivalent local pattern.
- Admin `finance`, `user`, `system/config`, `system/logs`.

Risk:
- Money and asset values must use `BigDecimal` or precise types.
- Idempotency keys are needed for order-based reward settlement.

Verification:
- Every reward has source order/event, rule version, before/after ledger entry, and idempotency guard.

### Phase 5: Admin Operations Completion

Goal:
- Real-name review, withdrawal review, offline payment record, content permission, config management, operation logs.

Directories/modules:
- Backend `controller/admin`, `service/admin`, `audit`.
- Admin `user`, `finance`, `content`, `system`.

Risk:
- Sensitive/deep referral queries must be permission-gated and logged.

Verification:
- Each key admin action writes operation log with operator, time, target, before value, after value, reason, and source.

### Phase 6: Verification and Release Readiness

Goal:
- Add tests/build checks and validate deployment path.

Directories/modules:
- Backend tests, mini/admin build config, Docker Compose.

Risk:
- Current demo data and encoding issues can mask actual acceptance problems.

Verification:
- Run backend tests, admin build, mini build, and database migration from empty schema.

## Questions for Product/Tech Lead

- Can a referrer ever be changed after registration, or is binding immutable?
- What are seed-account creation rules and invite-code lifetime/usage limits?
- What are the exact level names, order, upgrade conditions, commission ratios, reward caps, frozen periods, and fees?
- What are DF, wine beans, points, and options used for, and which can be withdrawn or consumed?
- Which payment methods are allowed per zone and product type?
- What fields are required for real-name auth in phase one?
- What evidence fields are required for offline withdrawal remittance?
- Is real WeChat Pay required in phase one, or is a simulated path acceptable until production certification?
- Should admin permissions be role-only or menu/button/API granular?
- What old-system data must be migrated in phase one?

## Commands Used During Review

Only read/search commands were run:

- `Get-ChildItem`
- `rg --files`
- `Get-Content`
- `Select-String`

No test/build commands were run because the requested task was architecture review only and explicitly said not to modify code.

## Suggested Next Action

If continuing implementation, do not start by adding UI. First design the backend data model changes for referral binding, real-name auth, independent asset accounts/ledgers, configurable rules, and operation audit logs. Those foundations control most PRD compliance risks.
