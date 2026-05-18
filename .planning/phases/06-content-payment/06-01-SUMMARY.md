---
phase: 06
plan: 01
subsystem: content-payment
tags: [database, schema, migration, entity, mapper]
requires: []
provides:
  - content_purchases.order_status_tracking
  - content_categories.table
  - content_likes.table
affects:
  - videos.category_id
  - help_articles.category_id
tech-stack:
  added:
    - Flyway migrations (V26, V27, V28)
    - MyBatis-Plus entities (ContentLike, ContentCategory)
    - MyBatis-Plus mappers (ContentLikeMapper, ContentCategoryMapper)
  patterns:
    - Auto-increment primary keys
    - FieldFill.INSERT/INSERT_UPDATE for timestamps
    - Unique constraints for data integrity
key-files:
  created:
    - drink-mall-api/src/main/resources/db/migration/V26__Content_Payment_Order_Status.sql
    - drink-mall-api/src/main/resources/db/migration/V27__Content_Categories_Table.sql
    - drink-mall-api/src/main/resources/db/migration/V28__Content_Likes_Table.sql
    - drink-mall-api/src/main/java/com/drinkmall/entity/ContentLike.java
    - drink-mall-api/src/main/java/com/drinkmall/entity/ContentCategory.java
    - drink-mall-api/src/main/java/com/drinkmall/mapper/ContentLikeMapper.java
    - drink-mall-api/src/main/java/com/drinkmall/mapper/ContentCategoryMapper.java
  modified:
    - drink-mall-api/src/main/java/com/drinkmall/entity/ContentPurchase.java
decisions:
  - Used VARCHAR(32) for status field to allow future status values
  - No foreign key constraints on content_likes to preserve like records when content is deleted (D-LIKE-05)
  - Added category_id as nullable foreign key to allow gradual migration from string category field
metrics:
  duration: 5 minutes
  tasks_completed: 8
  files_created: 7
  files_modified: 1
  commit: f9c8a09
---

# Phase 6 Plan 01: Database Schema for Content Payment & Engagement Summary

## One-liner

Created database migrations and entity classes for content payment order tracking, category management, and like functionality.

## What Was Done

### Task 01: V26 Migration - Content Purchase Order Status

Created Flyway migration adding payment tracking fields to `content_purchases` table:
- `status` VARCHAR(32) - Order status (pending, paid, cancelled, expired)
- `order_no` VARCHAR(64) - CP-prefixed order number
- `payment_no` VARCHAR(128) - WeChat Pay transaction ID
- `payment_time` DATETIME - Payment confirmation time
- Unique index on `order_no`
- Indexes on `status` and `user_id, status` for query optimization

### Task 02: V27 Migration - Content Categories Table

Created Flyway migration for flat category management:
- `content_categories` table with id, name, sort_order, status, created_at, updated_at
- Default categories inserted: 品酒知识, 酿造工艺, 行业动态, 其他
- Added `category_id` column to `videos` and `help_articles` tables

### Task 03: V28 Migration - Content Likes Table

Created Flyway migration for like tracking:
- `content_likes` table with id, user_id, content_type, content_id, created_at
- Unique constraint on (user_id, content_type, content_id) to prevent duplicate likes
- Index on (content_type, content_id) for counting likes per content

### Task 04: ContentPurchase Entity Update

Updated entity with new fields matching V26 migration:
- `status` with default "pending"
- `orderNo` for CP-prefixed order number
- `paymentNo` for WeChat Pay transaction ID
- `paymentTime` for payment confirmation timestamp

### Task 05: ContentLike Entity

Created new entity for `content_likes` table:
- Standard MyBatis-Plus entity pattern
- FieldFill.INSERT for createdAt

### Task 06: ContentCategory Entity

Created new entity for `content_categories` table:
- FieldFill.INSERT for createdAt
- FieldFill.INSERT_UPDATE for updatedAt

### Task 07: ContentLikeMapper

Created mapper interface extending BaseMapper<ContentLike>.

### Task 08: ContentCategoryMapper

Created mapper interface extending BaseMapper<ContentCategory>.

## Deviations from Plan

None - plan executed exactly as written.

## Verification

All files created and committed successfully:
- 3 migration files (V26, V27, V28)
- 2 new entity classes (ContentLike, ContentCategory)
- 2 new mapper interfaces (ContentLikeMapper, ContentCategoryMapper)
- 1 entity update (ContentPurchase)

## Requirements Addressed

- **CPAY-01**: Content purchase order tracking with status management
- **CPAY-04**: Flat category system for content organization
- **CPAY-06**: User like tracking with unique constraints

## Self-Check: PASSED

All verification checks passed:
- SUMMARY.md exists
- Commit f9c8a09 exists
- All migration files (V26, V27, V28) exist
- All entity files (ContentLike, ContentCategory) exist
- All mapper files (ContentLikeMapper, ContentCategoryMapper) exist
- ContentPurchase.java updated with new fields
