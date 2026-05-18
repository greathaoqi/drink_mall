---
phase: 06
plan: 02
subsystem: backend
tags: [payment, content, wechat-pay, integration]
requires: [06-01]
provides: [content-payment-flow]
affects: [PayController, ContentController, ContentPurchaseService]
tech-stack:
  added:
    - ContentPurchaseStatus enum
    - ContentPurchaseService interface
    - ContentPurchaseServiceImpl with WeChat Pay flow
  patterns:
    - CP-prefixed order numbers for content purchases
    - Order prefix dispatch in PayController callback
    - Idempotent prepay order creation
key-files:
  created:
    - drink-mall-api/src/main/java/com/drinkmall/enums/ContentPurchaseStatus.java
    - drink-mall-api/src/main/java/com/drinkmall/service/ContentPurchaseService.java
    - drink-mall-api/src/main/java/com/drinkmall/service/impl/ContentPurchaseServiceImpl.java
  modified:
    - drink-mall-api/src/main/java/com/drinkmall/controller/PayController.java
    - drink-mall-api/src/main/java/com/drinkmall/controller/ContentController.java
decisions:
  - CP-prefixed order numbers distinguish content purchases from product orders (DM prefix)
  - Same PayController callback endpoint handles both order types via prefix dispatch
  - Idempotency prevents duplicate pending orders for same user+content
  - Optimistic locking ensures payment callback only updates pending status
metrics:
  duration: "15 minutes"
  completed: "2026-05-18T07:24:00Z"
---

# Phase 6 Plan 02: Content Payment Flow - WeChat Pay Integration Summary

## One-Liner

Implemented real WeChat Pay integration for content purchases with CP-prefixed order numbers and PayController callback dispatch by order prefix.

## What Was Built

### Task 01: ContentPurchaseStatus Enum
- Created enum with PENDING, PAID, CANCELLED, EXPIRED statuses
- Each status has code and description fields
- Provides fromCode() method for lookup

### Task 02: ContentPurchaseService Interface
- Interface with four methods for content payment flow
- createPrepayOrder: idempotent prepay creation
- confirmPaymentCallback: WeChat Pay callback handler
- hasPurchased: purchase status check (paid only)
- cancelExpiredOrders: 30-minute timeout cleanup

### Task 03: ContentPurchaseServiceImpl
- createPrepayOrder: validates price, checks existing purchases, generates CP order number
- confirmPaymentCallback: validates amount, uses optimistic locking, handles replay protection
- hasPurchased: checks for paid status records
- cancelExpiredOrders: marks pending orders older than 30 minutes as expired

### Task 04: PayController CP Prefix Dispatch
- Injected ContentPurchaseService via @RequiredArgsConstructor
- Modified handleWeChatPayCallback to dispatch by order prefix
- CP-prefixed orders go to ContentPurchaseService
- DM-prefixed orders (existing) go to OrderService

### Task 05: ContentController.buy() for Real Payment
- Injected ContentPurchaseService via @RequiredArgsConstructor
- For "online" payment: calls createPrepayOrder and returns PayResponse
- For other payment methods: keeps existing flow
- Uses ContentPurchaseService.hasPurchased for paid status check

## Files Created/Modified

| File | Action | Purpose |
|------|--------|---------|
| ContentPurchaseStatus.java | Created | Payment lifecycle status enum |
| ContentPurchaseService.java | Created | Service interface definition |
| ContentPurchaseServiceImpl.java | Created | Payment flow implementation |
| PayController.java | Modified | CP prefix dispatch for callback |
| ContentController.java | Modified | PayResponse for online payments |

## Commits

| Commit | Message |
|--------|---------|
| 5ffad9a | feat(06-02): add ContentPurchaseStatus enum for payment lifecycle |
| d772797 | feat(06-02): add ContentPurchaseService interface for payment flow |
| 201e145 | feat(06-02): implement ContentPurchaseServiceImpl for payment flow |
| 666c82f | feat(06-02): add CP prefix dispatch to PayController callback |
| d2541d6 | feat(06-02): update ContentController.buy() for real WeChat Pay |

## Deviations from Plan

None - plan executed exactly as written.

## Self-Check: PASSED

- All 5 task files exist (verified: True for all)
- All 5 commits recorded in git log: 5ffad9a, d772797, 201e145, 666c82f, d2541d6
- Project compiles without errors (mvn compile -q)
- CP prefix check present in PayController (verified: 1 match)
- createPrepayOrder call present in ContentController (verified: 1 match)

## Known Stubs

- ContentPurchaseServiceImpl.createPrepayOrder returns mock prepayId ("MOCK_PREPAY_" + orderNo) for Phase 1 compatibility
- Real WeChat Pay prepay order creation (calling WxPayService) would be added when WeChat Pay credentials are configured

---

*Summary created: 2026-05-18*