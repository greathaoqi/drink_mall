---
phase: 6
slug: content-payment
status: draft
nyquist_compliant: false
wave_0_complete: false
created: 2026-05-18
---

# Phase 6 — Validation Strategy

> Per-phase validation contract for feedback sampling during execution.

---

## Test Infrastructure

| Property | Value |
|----------|-------|
| **Framework** | JUnit 5 + AssertJ |
| **Config file** | src/test/resources/application-test.yml |
| **Quick run command** | `mvn test -Dtest=*ContentPurchase* -q` |
| **Full suite command** | `mvn test -q` |
| **Estimated runtime** | ~45 seconds |

---

## Sampling Rate

- **After every task commit:** Run `mvn test -Dtest=*Test -q`
- **After every plan wave:** Run `mvn test -q`
- **Before `/gsd-verify-work`:** Full suite must be green
- **Max feedback latency:** 45 seconds

---

## Per-Task Verification Map

| Task ID | Plan | Wave | Requirement | Threat Ref | Secure Behavior | Test Type | Automated Command | File Exists | Status |
|---------|------|------|-------------|------------|-----------------|-----------|-------------------|-------------|--------|
| 06-01-01 | 01 | 1 | CPAY-01 | T-6-01 | WeChat Pay callback dispatches to content purchase | unit | `mvn test -Dtest=PayControllerTest#testContentCallback -q` | ❌ W0 | ⬜ pending |
| 06-01-02 | 01 | 1 | CPAY-01 | T-6-02 | Content prepay order generation with idempotency | unit | `mvn test -Dtest=ContentPurchaseServiceTest#testCreatePrepayOrder -q` | ❌ W0 | ⬜ pending |
| 06-02-01 | 02 | 1 | CPAY-02 | — | Admin purchase records list with pagination | unit | `mvn test -Dtest=AdminContentServiceTest#testGetPurchaseRecords -q` | ❌ W0 | ⬜ pending |
| 06-03-01 | 03 | 2 | CPAY-03 | T-6-03 | Search by keyword returns matching content, SQL injection protected | unit | `mvn test -Dtest=ContentControllerTest#testSearch -q` | ❌ W0 | ⬜ pending |
| 06-04-01 | 04 | 2 | CPAY-04 | — | Category CRUD operations | unit | `mvn test -Dtest=ContentCategoryTest#testCategoryCRUD -q` | ❌ W0 | ⬜ pending |
| 06-05-01 | 05 | 2 | CPAY-05 | T-6-04 | Preview shows first 150 chars, unpaid content protected | unit | `mvn test -Dtest=ContentResponseTest#testPreviewTruncation -q` | ❌ W0 | ⬜ pending |
| 06-06-01 | 06 | 3 | CPAY-06 | — | Like toggle updates count atomically | unit | `mvn test -Dtest=ContentLikeServiceTest#testToggleLike -q` | ❌ W0 | ⬜ pending |
| 06-07-01 | 07 | 3 | CPAY-07 | — | Analytics aggregates revenue correctly | unit | `mvn test -Dtest=AdminContentServiceTest#testAnalytics -q` | ❌ W0 | ⬜ pending |

*Status: ⬜ pending · ✅ green · ❌ red · ⚠️ flaky*

---

## Wave 0 Requirements

- [ ] `ContentPurchaseServiceTest.java` — stubs for CPAY-01 prepay generation
- [ ] `PayControllerTest.java` — add testContentCallback() for CP prefix dispatch
- [ ] `ContentLikeServiceTest.java` — stubs for CPAY-06 toggle
- [ ] `ContentCategoryTest.java` — stubs for CPAY-04 CRUD
- [ ] Database migration V19-V21 — schema changes for status, likes, categories

---

## Manual-Only Verifications

| Behavior | Requirement | Why Manual | Test Instructions |
|----------|-------------|------------|-------------------|
| WeChat Pay actual payment flow | CPAY-01 | Requires live WeChat Pay API | Use sandbox environment, complete payment in WeChat app |
| Content preview display in Mini Program | CPAY-05 | Visual verification | Open content detail as unpurchased user, verify teaser text |

---

## Validation Sign-Off

- [ ] All tasks have `<automated>` verify or Wave 0 dependencies
- [ ] Sampling continuity: no 3 consecutive tasks without automated verify
- [ ] Wave 0 covers all MISSING references
- [ ] No watch-mode flags
- [ ] Feedback latency < 45s
- [ ] `nyquist_compliant: true` set in frontmatter

**Approval:** pending
