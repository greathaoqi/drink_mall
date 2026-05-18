# Miniapp Design Replica Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Make the UniApp WeChat mini program match `docs/设计稿/` 1:1 as the visual and functional source of truth, while preserving the phase-one compliance and asset-accounting constraints in `AGENTS.md`.

**Architecture:** Treat the design draft as a screen contract. First extract shared visual tokens and reusable UniApp components, then implement page groups in isolated vertical slices, and only add backend/admin support when a screen needs data or actions that do not already exist. Each implementation subagent owns a disjoint page group and must keep recommendation display limited to inviter, direct members, indirect members, and total team count.

**Tech Stack:** UniApp 4.x, Vue 3, Pinia, uview-plus, uni-ui, Spring Boot 3.3.x, MyBatis-Plus, MySQL Flyway, Vue 3 + Element Plus admin.

---

## Reader And Outcome

Reader: a future implementation subagent with no prior session context.

After reading this plan, the subagent can pick one task group, know which design artboards and project files it owns, implement that slice without touching other groups, and run the correct verification commands.

## Source Of Truth

Primary design source:

- `docs/设计稿/app.jsx`: artboard index and screen order.
- `docs/设计稿/styles.css`: global colors, typography, spacing, shadows.
- `docs/设计稿/shared-components.jsx`: reference components such as status bar, nav bar, tab bar, phone frame, product image, avatar, brand mark.
- `docs/设计稿/section-a.jsx` through `section-h.jsx`, `section-extras-1.jsx`, `section-extras-2.jsx`, `section-responsive.jsx`: screen-level layouts.
- `docs/设计稿/uploads/`: design assets.
- `docs/设计稿/醇品汇 · 高保真设计稿.html`: browsable high-fidelity canvas.

Implementation target:

- `drink-mall-mini/src`: UniApp mini program.

Support targets only when required by a mini-program screen:

- `drink-mall-api/src/main/java/com/drinkmall`: backend API, service, DTO, entity, mapper.
- `drink-mall-api/src/main/resources/db/migration`: Flyway schema/seed changes.
- `drink-mall-admin/src`: admin screens needed to manage new data shown in mini program.

## Repository Snapshot

Current structure:

- Mini program: `drink-mall-mini`, using UniApp + Vue 3 + Pinia, routes in `src/pages.json`, API wrappers in `src/services`, shared utilities in `src/utils`.
- Backend: `drink-mall-api`, Spring Boot 3.3.2 + Sa-Token + MyBatis-Plus + Flyway, REST controllers split between mini APIs and `controller/admin`.
- Admin: `drink-mall-admin`, Vue 3 + Element Plus, routes in `src/router/index.ts`, permission meta already present.
- Database: Flyway migrations exist through `V22__Content_Access_Payment_Config.sql`.

Current mini-program capability:

- Pages already exist for login, home, product list/detail, cart, checkout, orders, logistics, payment result, aftersale, address, wallet, withdrawal, content, announcement, video, help, profile, settings, team, distribution level, investment zone, gift zone, and points.
- Referral capture and share path helpers exist in `src/utils/referral.ts`.
- Login guard and real-name guard exist in `src/utils/auth.ts`.
- Team service/page already use overview, direct, and indirect lists only.
- Existing tests cover referral share paths, compliant team endpoints, checkout payment method blocking, and content access payment behavior.

Confirmed scope decisions:

- Exclude F3 partner level/reward system.
- Exclude F12/F13 experience-store application/status.
- Exclude G5 coupon center.
- Exclude G7 message center.
- Include X5 system update modal with backend/system-config backing if practical, otherwise a design-matched configurable static state.

Observed gaps:

- Many visible Chinese strings are mojibake and must be replaced with design-copy text.
- Existing UI is functional but not 1:1 with the design draft tokens, navigation, spacing, panels, and modal treatments.
- The design draft includes excluded screens that must stay out of implementation: `F3 · 合伙人等级`, `F12 · 体验店申请`, `F13 · 体验店状态`, `G5 · 我的优惠券`, and `G7 · 消息中心`.
- Some draft screens are modal/state variants rather than standalone routes and should be implemented as shared components or page states.

## Design Artboard Inventory

Implementable page groups:

- A account/login: A1 login, A2 WeChat phone auth, A3 invitation binding, A4 user agreement, A5 guest limit modal.
- B home/catalog: B1 home, B2 search, B3 search result, B4 category, B5 main product zone, B6 investment zone, B7 retail zone, B8 gift zone, B9 filter/sort, B10 empty product state.
- C product/purchase: C1 product detail, C2 SKU modal, C3 share sheet, C4 product poster, C5 cart, C6 checkout, C7 address list, C8 address edit, C9 payment method, C10 payment success, C11 payment fail.
- D orders: D1 order list, D2 order detail, D3 logistics detail, D4 aftersale apply, D5 aftersale detail.
- E content: E1 dynamic home, E2 announcement list, E3 announcement detail, E4 knowledge/activity list, E5 content detail.
- F distribution: F1 distribution workbench, F2 joint-operator level, F4 reward detail, F5 commission detail, F6 withdrawal, F7 withdrawal records, F8 team management, F9 team member detail, F10 invite share, F11 invite poster.
- G profile/assets: G1 profile, G2 assets, G3 balance detail, G4 points detail, G6 settings, G8 customer service, G9 wine-bean detail, G10 DF gift, G11 DF gift records.
- H common states: H1 loading, H2 empty, H3 network error, H4 auth gate, H5 toast, H6 confirm modal, H7 action sheet.
- X supplemental: X1 order wait-ship, X2 order wait-receive, X3 order done, X4 cancel reason, X5 system update modal, X6 profile edit, X7 real-name, X8 real-name forced modal, X9 invite code, X10 address management.
- R responsive home: verify 375 x 667, 390 x 844, 393 x 852, 430 x 932.

Do not implement without explicit business confirmation:

- F3 partner level/reward system. This conflicts with `AGENTS.md` phase-one exclusion for partner rewards.
- Any multi-level team tree or third-level member detail. The mini program may show inviter, direct list, indirect list, and total team count only.
- Any wording implying unlimited hierarchy, team fission revenue, or multi-level expandable organization.

Confirmed exclusions:

- F12/F13 experience-store application/status.
- G5 coupon center.
- G7 message center.

## Global Implementation Rules

- Use design draft copy as visible text and fix all mojibake in touched mini-program files.
- Preserve backend-driven rule configuration. Do not hardcode levels, rates, caps, fees, frozen windows, content permissions, product payment methods, or package amounts.
- Do not introduce combined asset payments unless backend configuration and PRD explicitly allow that exact method.
- Use BigDecimal/backend precise numeric types for money and asset quantities.
- Use enum/constant-backed statuses; do not scatter status strings.
- Any backend/admin write operation for assets, rewards, withdrawal, level changes, real-name review, manual adjustment, or deep referral lookup must be transactional, idempotent where applicable, permission-checked, and audit-logged.
- Mini-program team UI must not contain expandable tree data, third-level details, descendants, or children lists.
- Prefer existing service wrappers, request format, exception handling, stores, and component patterns.
- Avoid broad rewrites outside the current task group.

## Subagent Execution Model

Use one worker subagent per task group. Workers are not alone in the codebase: they must not revert edits made by other workers, must avoid touching files outside their ownership, and must adapt to shared component changes.

Recommended order:

1. Foundation tokens/components.
2. Account/home/product slices in parallel after foundation lands.
3. Orders/content/distribution/profile slices in parallel after shared page states land.
4. Backend/admin support slices only for missing contracts found by page workers.
5. Responsive and visual verification pass.

Each worker must report:

- Artboards implemented.
- Files changed.
- API contract changes, if any.
- Verification commands and result.
- Remaining mismatches against the design draft.

## Task 1: Visual Foundation And Shared Components

**Files:**

- Modify: `drink-mall-mini/src/App.vue`
- Modify: `drink-mall-mini/src/pages.json`
- Modify: `drink-mall-mini/src/components/PageState/PageState.vue`
- Modify/Create: shared components under `drink-mall-mini/src/components`
- Create: `drink-mall-mini/src/styles` or equivalent project-local style module if following UniApp conventions allows it.
- Read only: `docs/设计稿/styles.css`, `docs/设计稿/shared-components.jsx`, `docs/设计稿/section-h.jsx`, `docs/设计稿/section-responsive.jsx`
- Test: `drink-mall-mini/tests/mini-contract.test.cjs` plus any new contract tests for route/state invariants.

- [ ] Extract design colors, typography, radius, shadows, safe-area spacing, and page background into shared SCSS/CSS variables.
- [ ] Build shared UniApp equivalents for design navigation bar, status spacing, bottom action bar, tab-like section tabs, modal mask, action sheet, toast wrapper, empty/loading/error/auth-gate states.
- [ ] Align existing tab bar colors/icons with the design draft; replace only assets owned by this task.
- [ ] Fix global mojibake strings in common components touched by this task.
- [ ] Add contract tests that common auth/team constraints still hold.
- [ ] Run `cd drink-mall-mini; npm test`.
- [ ] Run `cd drink-mall-mini; npm run build:test:mp-weixin`.
- [ ] Commit after this slice when requested by the user.

Expected result: page workers can compose screens using shared tokens/components instead of re-creating local visual systems.

## Task 2: Account, Referral, Agreement, And Auth Gates

**Files:**

- Modify: `drink-mall-mini/src/pages/login/index.vue`
- Modify: `drink-mall-mini/src/App.vue`
- Modify: `drink-mall-mini/src/utils/auth.ts`
- Modify: `drink-mall-mini/src/utils/referral.ts`
- Modify/Create: agreement page route/component if A4 is not already route-backed.
- Modify/Create: guest/auth gate component if not covered by Task 1.
- Backend only if contract gap exists: `AuthController`, `RegistrationController`, `AuthServiceImpl`, login/register DTOs.
- Tests: `drink-mall-mini/tests/referral-share.test.cjs`, backend auth tests if backend changes.
- Artboards: A1-A5, H4.

- [ ] Map current login flow to A1, A2, and A3. Keep required invitation binding for ordinary new users.
- [ ] Support referral capture from mini share path, product share path, manual invite code, and QR scene parsing.
- [ ] Implement agreement/privacy confirmation matching A4 text treatment and A1/A2 checkbox behavior.
- [ ] Implement guest restriction modal matching A5/H4 for purchase, cart, share-for-commission, withdrawal, and protected pages.
- [ ] Ensure users entering without inviter cannot complete registration and receive design-copy guidance.
- [ ] Verify seed/demo account flow remains available only where existing business logic permits.
- [ ] Run `cd drink-mall-mini; npm test`.
- [ ] Run backend tests if backend changed: `cd drink-mall-api; mvn test`.

Expected result: account flow visually matches the design draft and still enforces mandatory referral binding.

## Task 3: Home, Search, Category, And Four Zones

**Files:**

- Modify: `drink-mall-mini/src/pages/index/index.vue`
- Modify: `drink-mall-mini/src/pages/product/list.vue`
- Modify: `drink-mall-mini/src/pages/zone/investment/index.vue`
- Modify: `drink-mall-mini/src/pages/zone/gift/index.vue`
- Create/Modify: retail/main zone handling if current `product/list` query mode is insufficient.
- Modify: `drink-mall-mini/src/services/product.ts`
- Modify: `drink-mall-mini/src/services/config.ts`
- Backend/admin only if contract gap exists: public banner/announcement/category/product APIs, product zone enum/config, admin product zone fields.
- Artboards: B1-B10, R1-R4.

- [ ] Rebuild home to match B1: dark top brand area, search, banner, notice, service entry, and four zones.
- [ ] Rebuild search page/state to match B2/B3. If search is currently only query mode, add route/state in `pages.json`.
- [ ] Rebuild category/filter/sort states to match B4/B9/B10.
- [ ] Implement four zone entry points with design-copy labels: main product, investment, retail, gift.
- [ ] Preserve backend-configured product payment methods and zone rules.
- [ ] Add or update contract tests for route existence and zone query mapping.
- [ ] Run `cd drink-mall-mini; npm test`.
- [ ] Run `cd drink-mall-mini; npm run build:test:mp-weixin`.

Expected result: first viewport and catalog flows are visually indistinguishable from B/R artboards at the supported phone widths.

## Task 4: Product Detail, Cart, Checkout, Address, And Payment Result

**Files:**

- Modify: `drink-mall-mini/src/pages/product/detail/index.vue`
- Modify: `drink-mall-mini/src/pages/cart/index.vue`
- Modify: `drink-mall-mini/src/pages/checkout/index.vue`
- Modify: `drink-mall-mini/src/pages/address/list/index.vue`
- Modify: `drink-mall-mini/src/pages/address/edit/index.vue`
- Modify: `drink-mall-mini/src/pages/payment/result/index.vue`
- Modify: `drink-mall-mini/src/components/BottomActionBar`
- Modify: `drink-mall-mini/src/components/PayMethodSelector`
- Modify: `drink-mall-mini/src/services/cart.ts`, `order.ts`, `payment.ts`, `address.ts`, `product.ts`
- Backend only if contract gap exists: product detail/pay methods, cart, checkout preview, order creation, payment initiation/result.
- Tests: mini checkout/payment tests plus backend order/payment tests if touched.
- Artboards: C1-C11, H5-H7.

- [ ] Rebuild product detail top image, price, title, tags, benefits, rules, detail section, and sticky action bar to C1.
- [ ] Implement SKU selector, share sheet, and poster entry per C2-C4. Poster can use canvas/share image only if supported by existing mini-program constraints; otherwise keep a design-matched preview and mark generation as follow-up.
- [ ] Rebuild cart and checkout to C5/C6.
- [ ] Rebuild address list/edit to C7/C8 and X10.
- [ ] Rebuild payment method and result states to C9-C11.
- [ ] Enforce single payment method and disabled method blocking before creating orders.
- [ ] Preserve gift-zone pure-points redemption and investment-zone real-name/cooperation confirmation constraints.
- [ ] Run `cd drink-mall-mini; npm test`.
- [ ] Run `cd drink-mall-mini; npm run build:test:mp-weixin`.
- [ ] Run `cd drink-mall-api; mvn test` if backend changed.

Expected result: the complete browse-to-pay path matches the design and does not violate asset independence.

## Task 5: Orders, Logistics, Cancel Reasons, And Aftersale

**Files:**

- Modify: `drink-mall-mini/src/pages/order/list/index.vue`
- Modify: `drink-mall-mini/src/pages/order/detail/index.vue`
- Modify: `drink-mall-mini/src/pages/order/logistics/index.vue`
- Modify: `drink-mall-mini/src/pages/aftersale/index.vue`
- Modify: `drink-mall-mini/src/pages/aftersale/detail/index.vue`
- Modify: `drink-mall-mini/src/services/order.ts`, `aftersale.ts`
- Backend only if contract gap exists: order status tabs, cancel reasons, logistics display, aftersale submit/detail.
- Artboards: D1-D5, X1-X4, H2, H6.

- [ ] Rebuild order list with status tabs and variant pages for wait-ship, wait-receive, done.
- [ ] Rebuild order detail, logistics, cancel-confirm, and cancel-reason flows.
- [ ] Rebuild aftersale apply/detail screens and keep current phase-one limits: no complete online return logistics loop unless explicitly requested.
- [ ] Centralize order and aftersale status labels with enum-backed mappings.
- [ ] Ensure backend order operations remain permission-checked and auditable if changed.
- [ ] Run `cd drink-mall-mini; npm test`.
- [ ] Run `cd drink-mall-mini; npm run build:test:mp-weixin`.
- [ ] Run `cd drink-mall-api; mvn test` if backend changed.

Expected result: all order states shown in the design are covered without adding unsupported logistics integrations.

## Task 6: Dynamic Content, Announcements, Help, Video, And Paid Content

**Files:**

- Modify: `drink-mall-mini/src/pages/content/list/index.vue`
- Modify: `drink-mall-mini/src/pages/content/detail/index.vue`
- Modify: `drink-mall-mini/src/pages/announcement/list/index.vue`
- Modify: `drink-mall-mini/src/pages/announcement/detail/index.vue`
- Modify: `drink-mall-mini/src/pages/video/index.vue`
- Modify: `drink-mall-mini/src/pages/help/index.vue`
- Modify: `drink-mall-mini/src/services/content.ts`, `config.ts`
- Backend/admin only if contract gap exists: content access policy, payment methods, admin content fields.
- Tests: `drink-mall-mini/tests/content-permission.test.cjs`, backend content policy tests if touched.
- Artboards: E1-E5, G8.

- [ ] Rebuild dynamic home, announcement list/detail, knowledge/activity list, and article/detail layout.
- [ ] Preserve backend `canView` as the source of truth for paid/permission content.
- [ ] Ensure content purchase sends content type and selected backend-configured pay method.
- [ ] Rebuild video/help/customer-service screens according to design.
- [ ] Run `cd drink-mall-mini; npm test`.
- [ ] Run `cd drink-mall-mini; npm run build:test:mp-weixin`.
- [ ] Run `cd drink-mall-api; mvn test` if backend changed.

Expected result: content UI matches E/G8 and permission decisions remain backend controlled.

## Task 7: Distribution, Team, Invite, Rewards, Withdrawal

**Files:**

- Modify: `drink-mall-mini/src/pages/team/index.vue`
- Modify: `drink-mall-mini/src/pages/distribution/level/index.vue`
- Modify/Create: distribution workbench, reward detail, commission detail, withdrawal record, invite share, invite poster routes if not present.
- Modify: `drink-mall-mini/src/pages/wallet/withdraw/index.vue`
- Modify: `drink-mall-mini/src/services/team.ts`, `asset.ts`
- Backend only if contract gap exists: team overview/directs/indirects, level overview, reward records, withdrawal records, invite code endpoint, DF transfer.
- Admin/backend audit if any withdrawal/reward/asset write behavior changes.
- Tests: `drink-mall-mini/tests/mini-contract.test.cjs`, backend asset/reward/withdrawal tests if touched.
- Artboards: F1-F2, F4-F11, X9.

- [ ] Rebuild distribution workbench and joint-operator level screens using backend-configured names/rules.
- [ ] Do not implement F3 partner rewards in phase one. Keep it out of routes unless business explicitly approves.
- [ ] Rebuild reward and commission detail screens.
- [ ] Rebuild withdrawal and withdrawal record screens, using backend-configured minimum amount and fee.
- [ ] Rebuild team management and member detail with only inviter, direct, indirect, and total counts. No tree, children, descendants, third-level detail, or expandable hierarchy.
- [ ] Rebuild invite share and invite poster screens using current user's invite code.
- [ ] Keep DF gift operation idempotent/audited if backend changes are required.
- [ ] Run `cd drink-mall-mini; npm test`.
- [ ] Run `cd drink-mall-mini; npm run build:test:mp-weixin`.
- [ ] Run `cd drink-mall-api; mvn test` if backend changed.

Expected result: distribution UX matches the design while staying inside compliance boundaries.

## Task 8: Profile, Assets, Logs, Settings, Real Name, Profile Edit

**Files:**

- Modify: `drink-mall-mini/src/pages/profile/index.vue`
- Modify: `drink-mall-mini/src/pages/wallet/index.vue`
- Modify: `drink-mall-mini/src/pages/wallet/history/index.vue`
- Modify: `drink-mall-mini/src/pages/auth/realname/index.vue`
- Modify: `drink-mall-mini/src/pages/settings/index.vue`
- Modify/Create: profile edit route/component if missing.
- Modify/Create: wine-bean, DF gift, DF gift log display states if current history page cannot cover them.
- Modify: `drink-mall-mini/src/services/user.ts`, `asset.ts`
- Backend only if contract gap exists: asset overview/log filters, profile update, real-name submit/status.
- Artboards: G1-G4, G6, G9-G11, X6-X8.

- [ ] Rebuild profile center to G1 and preserve all navigation entries required by implemented screens.
- [ ] Rebuild assets overview and asset logs to G2-G4/G9, keeping balance, frozen balance, DF, wine bean, points, and option value independent.
- [ ] Rebuild settings, profile edit, real-name submit, and forced real-name modal.
- [ ] Rebuild DF gift and gift-record screens or route them through a design-matched history filter if no separate backend contract is needed.
- [ ] Do not implement coupon center/message center. They are explicitly excluded from this implementation scope.
- [ ] Run `cd drink-mall-mini; npm test`.
- [ ] Run `cd drink-mall-mini; npm run build:test:mp-weixin`.
- [ ] Run `cd drink-mall-api; mvn test` if backend changed.

Expected result: profile and asset screens match the design and preserve asset isolation.

## Task 9: Backend And Admin Contract Backfill

**Files:**

- Modify only the backend/admin files required by gaps discovered in Tasks 2-8.
- Typical backend areas: controllers, services, DTOs, mappers, enums, migrations, admin services.
- Typical admin areas: product zone configuration, content permissions/payment methods, withdrawal review/logs, system config, invite code/seed account management.

- [ ] For each missing mini-program field/action, first check whether an existing endpoint already returns it.
- [ ] If adding fields, update DTOs and mappers without breaking existing clients.
- [ ] If adding configuration, store it in database/system config/enum tables as appropriate; mark unknown PRD values as "待业务确认" in seed/config notes.
- [ ] If adding admin writes, enforce permission checks and operation logs with before/after values, reason, object, operator, source, and time.
- [ ] If touching assets/rewards/withdrawal, use transactions, precise numeric types, ledger rows, and idempotency guards.
- [ ] Add Flyway migrations only for durable schema/config changes.
- [ ] Run `cd drink-mall-api; mvn test`.
- [ ] Run `cd drink-mall-admin; npm run build:test` if admin changed.

Expected result: mini-program screens have stable backend contracts without hardcoded business rules.

## Task 10: Visual QA, Responsive QA, And Release Readiness

**Files:**

- Modify only defects discovered during QA.
- Read only: `docs/设计稿/醇品汇 · 高保真设计稿.html`, design section files, implementation pages.

- [ ] Build the mini program with `cd drink-mall-mini; npm run build:test:mp-weixin`.
- [ ] Compare implemented pages against every artboard listed in this plan.
- [ ] Verify home at widths 375, 390, 393, and 430 according to R1-R4.
- [ ] Verify no visible mojibake remains in mini-program files touched by this project.
- [ ] Verify no mini-program team route shows third-level detail, tree, descendants, or expandable organization.
- [ ] Verify all protected flows show login/real-name gates matching design.
- [ ] Run `cd drink-mall-mini; npm test`.
- [ ] Run `cd drink-mall-admin; npm run build:test` if admin changed.
- [ ] Run `cd drink-mall-api; mvn test` if backend changed.

Expected result: the app is visually ready for stakeholder review and has automated checks for the highest-risk business constraints.

## Verification Matrix

Always run for mini-program-only slices:

```powershell
cd D:\project\drink_mall\drink-mall-mini
npm test
npm run build:test:mp-weixin
```

Run when backend changes:

```powershell
cd D:\project\drink_mall\drink-mall-api
mvn test
```

Run when admin changes:

```powershell
cd D:\project\drink_mall\drink-mall-admin
npm run build:test
```

Manual/visual verification:

- Open `docs/设计稿/醇品汇 · 高保真设计稿.html` and compare each implemented screen against its artboard.
- Use WeChat Developer Tools or a browser-capable UniApp preview flow where available.
- Capture mismatches as task-specific follow-up notes rather than broad rewrites.

## Risks And Controls

- Scope risk: the design draft contains 60+ screens. Control by implementing one artboard group per subagent and requiring each worker to list remaining mismatches.
- Compliance risk: distribution pages can accidentally expose deeper hierarchy. Control with existing team tests plus source checks for tree/children/descendants/third-level terms.
- Business-rule risk: design copy may imply rules not confirmed in PRD. Control by showing UI placeholders and pulling real values from backend config.
- Encoding risk: existing files contain mojibake. Control by replacing visible text in touched files with UTF-8 Chinese from the design draft.
- Asset risk: generated or copied design assets may not match mini-program packaging constraints. Control by keeping assets under `src/static` and verifying build output.
- Parallel-work risk: many workers could touch shared components. Control by landing Task 1 first, then assigning disjoint page groups.

## Open Questions For Human Confirmation

- Should X5 system update modal be implemented as a static version notice or backed by system config?
- Should product poster and invite poster require real canvas-generated share images, or is a design-matched poster preview acceptable for the first implementation pass?
