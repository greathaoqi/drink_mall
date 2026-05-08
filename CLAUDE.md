# 酒水商城 (Drink Mall) - Project Instructions

## Project Overview

WeChat Mini Program e-commerce platform for alcohol/liquor retail in the Chinese market.

**Core Value:** 用户可以方便地在微信小程序中浏览和购买酒水商品，享受流畅的购物体验，平台可以高效管理商品和订单。

## Technology Stack

- **Mini Program:** UniApp 4.x + Vue 3
- **Backend:** Spring Boot 3.3.x + MyBatis-Plus
- **Database:** MySQL 8.0 + Redis 7.2
- **Admin Panel:** Element Plus + Vue 3
- **Payment:** WeChat Pay (WxJava SDK)

## Key Architecture Decisions

1. **Monolithic backend** for simplicity at 1000 concurrent users scale
2. **JWT authentication** with Sa-Token
3. **Optimistic locking** for inventory management
4. **Tencent Cloud deployment** for WeChat ecosystem integration

## Critical Requirements

1. **Legal Compliance** - Age verification (18+), alcohol sales licenses required
2. **Payment Security** - WeChat Pay callback verification mandatory
3. **Inventory Management** - Prevent overselling with proper locking

## Project Structure

```
.planning/
├── PROJECT.md      # Project context and requirements
├── REQUIREMENTS.md # Detailed requirements with traceability
├── ROADMAP.md      # Phase structure and progress
├── STATE.md        # Current project state
├── config.json     # Workflow configuration
└── research/       # Domain research findings
```

## Workflow

1. `/gsd-discuss-phase 1` - Gather context before planning
2. `/gsd-plan-phase 1` - Create implementation plan
3. `/gsd-execute-phase 1` - Execute the plan
4. `/gsd-verify-work` - Verify phase completion

## Out of Scope

All distribution features have been removed:
- 联营商/合伙人等级体系
- 分销奖励计算
- 招商专区
- 团队业绩统计
- DF余额
- 上级绑定

---
*Last updated: 2026-05-08*