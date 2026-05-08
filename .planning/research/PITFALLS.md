# Domain Pitfalls: 酒水电商 (Liquor E-commerce)

**Domain:** WeChat Mini Program E-commerce for Alcohol/Liquor
**Researched:** 2026-05-08
**Confidence:** MEDIUM (based on domain expertise and patterns; specific WeChat documentation verification limited)

---

## Critical Pitfalls

Mistakes that cause rewrites, legal liability, or platform rejection.

### Pitfall 1: WeChat Mini Program 审核拒绝 (Content Review Rejection)

**What goes wrong:**
Mini program rejected during WeChat审核 for missing qualifications, inappropriate content, or non-compliant features specific to alcohol sales.

**Why it happens:**
- Missing 酒类销售许可证 (Alcohol Sales License) documentation
- Alcohol promotional content violating advertising regulations (酒类广告管理办法)
- No age verification mechanism before alcohol purchase
- User-generated content without proper moderation
- Missing ICP备案 or incorrect service category classification

**Consequences:**
- Repeated rejection delays launch by weeks
- May require significant code changes
- Risk of permanent account restrictions

**Prevention:**
1. Prepare all required licenses before development:
   - 食品经营许可证 (Food Business License) with alcohol endorsement
   - 酒类商品授权书 (Brand Authorization)
   - ICP备案号 (ICP Filing Number)
2. Implement mandatory age verification modal (18+) before any alcohol-related content display
3. Add disclaimer text: "未成年人禁止饮酒" (Minors prohibited from drinking)
4. Configure correct service category in WeChat backend: 电商平台 > 酒类
5. Content moderation for all user-generated content

**Detection:**
- Pre-submission checklist against WeChat审核规范
- Legal review of all alcohol-related content
- Test age verification flow thoroughly

**Phase to address:** Phase 1 (MVP) - Must have licenses and age verification before first submission

---

### Pitfall 2: 支付回调验证缺失 (Payment Callback Verification Missing)

**What goes wrong:**
Orders marked as paid without proper WeChat Pay callback validation, leading to:
- Fake payment exploits
- Order status inconsistent with actual payment
- Financial discrepancies

**Why it happens:**
- Developers trust client-side payment success response
- Callback signature verification skipped or incomplete
- Callback URL not properly configured or accessible
- No idempotency handling for duplicate callbacks

**Consequences:**
- Financial loss from fraudulent orders
- Order data corruption
- Customer disputes

**Prevention:**
1. **Always verify server-side callback:**
   ```javascript
   // Verify signature from WeChat Pay callback
   const sign = calculateSign(callbackData, apiKey);
   if (sign !== receivedSign) {
     return 'FAIL'; // Reject invalid callback
   }
   ```
2. Use WeChat Pay SDK for signature verification
3. Implement callback idempotency (check if order already processed)
4. Configure callback URL in WeChat Pay merchant platform
5. Log all callbacks for audit trail

**Detection:**
- Test with callback simulation tools
- Monitor for orders with payment status inconsistencies
- Regular reconciliation between order system and WeChat Pay records

**Phase to address:** Phase 1 (MVP) - Core payment security must be solid from start

---

### Pitfall 3: 库存超卖 (Inventory Overselling)

**What goes wrong:**
More orders placed than available inventory, especially during promotions/flash sales, resulting in:
- Orders that cannot be fulfilled
- Customer complaints and refunds
- Reputation damage

**Why it happens:**
- Race conditions when multiple users purchase simultaneously
- Database reads and writes not properly locked
- Caching layer inventory not synchronized with database
- No transaction isolation or optimistic locking

**Consequences:**
- Customer service burden
- Refund processing overhead
- Loss of customer trust

**Prevention:**
1. **Database-level solution (recommended for this scale):**
   ```sql
   UPDATE products 
   SET stock = stock - 1 
   WHERE id = ? AND stock > 0 AND stock - 1 >= 0;
   -- Check affected rows, if 0 = insufficient stock
   ```
2. **Use optimistic locking with version field:**
   ```sql
   UPDATE products 
   SET stock = stock - 1, version = version + 1
   WHERE id = ? AND version = ? AND stock > 0;
   ```
3. For high-concurrency scenarios (1000 users), consider:
   - Redis distributed lock (Redis SETNX)
   - Decrement stock atomically in Redis first
   - Async sync to database
4. Add inventory reservation with expiration (hold stock during checkout)

**Detection:**
- Monitor for negative inventory values
- Track orders that fail fulfillment due to stock issues
- Load test with concurrent purchases

**Phase to address:** Phase 1 (MVP) - Basic optimistic locking; Phase 2 for Redis-based solution

---

### Pitfall 4: 未成年人购酒 (Minors Purchasing Alcohol - Legal Liability)

**What goes wrong:**
Platform allows minors to purchase alcohol, violating Chinese law (未成年人保护法, 酒类流通管理办法) and risking:
- Regulatory fines (up to 100,000 CNY)
- Business license revocation
- Legal liability

**Why it happens:**
- No age verification at all
- Age verification easily bypassed (single checkbox)
- No real-name authentication integration
- Verification only at registration, not at purchase

**Consequences:**
- Legal penalties
- Platform shutdown
- Brand reputation damage

**Prevention:**
1. **Multi-layer age verification:**
   - WeChat real-name authentication (微信实名认证)
   - ID card verification for first alcohol purchase
   - Age confirmation modal at product page AND checkout
2. Store age verification status in user profile
3. Block alcohol product visibility for unverified users
4. Log all age verification attempts

**Detection:**
- Regular compliance audits
- Review orders for suspicious age patterns
- Customer feedback monitoring

**Phase to address:** Phase 1 (MVP) - Must have before selling any alcohol

---

### Pitfall 5: setData 性能问题 (setData Performance Bottleneck)

**What goes wrong:**
Mini program becomes laggy, unresponsive, or crashes when:
- Rendering long product lists (100+ items)
- Frequent data updates (real-time inventory, cart updates)
- Large image arrays in swiper/carousel

**Why it happens:**
- `setData` transfers data from JS thread to rendering thread
- Each `setData` triggers re-rendering
- Passing large objects or arrays
- Calling `setData` too frequently (multiple times per second)
- Not using virtual lists for long lists

**Consequences:**
- Poor user experience
- Users abandon app
- WeChat may kill unresponsive mini programs

**Prevention:**
1. **Minimize setData payload:**
   ```javascript
   // Bad: Setting entire list
   this.setData({ productList: newFullList });

   // Good: Set only changed items
   this.setData({ 
     'productList[3].stock': 50 
   });
   ```
2. **Use virtual list for long lists:**
   - `wx:for` with `wx:key` for efficient diffing
   - Implement lazy loading / infinite scroll
   - Only render visible items
3. **Debounce frequent updates:**
   ```javascript
   // Debounce cart updates
   const debouncedUpdate = debounce(this.updateCart.bind(this), 300);
   ```
4. **Image optimization:**
   - Use CDN with image compression
   - Lazy load images below fold
   - Use WebP format
   - Implement placeholder/skeleton screens

**Detection:**
- WeChat DevTools performance panel
- Monitor `setData` call frequency and payload size
- Test with realistic data volumes

**Phase to address:** Phase 1 (MVP) - Image optimization, lazy loading; Phase 2 - Virtual lists if needed

---

## Moderate Pitfalls

### Pitfall 6: 后台管理易用性差 (Poor Admin Panel Usability)

**What goes wrong:**
Operations team struggles with inefficient backend:
- Cannot quickly find orders/products
- Batch operations missing (bulk price update, inventory adjustment)
- Unclear data display (poor tables, missing filters)
- No export functionality for reports

**Why it happens:**
- Developer-focused UI (not operator-focused)
- Missing business workflow analysis
- No operator feedback during development
- Over-engineered or under-engineered features

**Consequences:**
- Operational inefficiency
- Errors from manual workarounds
- Staff frustration

**Prevention:**
1. Design admin workflows with operators
2. Essential features:
   - Advanced search/filter (by date, status, customer, product)
   - Batch operations (select multiple, bulk action)
   - Data export (Excel/CSV for orders, products, users)
   - Quick actions on list pages (without drill-down)
   - Clear status indicators (color-coded)
3. Responsive design for mobile admin access
4. Role-based access control clarity

**Detection:**
- Operator feedback sessions
- Time-to-complete common tasks measurement
- Error rate monitoring

**Phase to address:** Phase 1 (MVP) - Core CRUD with filters; Phase 2 - Batch operations and export

---

### Pitfall 7: 数据备份缺失 (Missing Data Backup Strategy)

**What goes wrong:**
Data loss from:
- Database corruption
- Accidental deletion
- Server failure
- No recovery point

**Why it happens:**
- Backup not configured or forgotten
- Backups not tested (corrupted backup discovered too late)
- No backup retention policy
- Only database backup, no file storage backup

**Consequences:**
- Lost orders, users, products
- Business disruption
- Customer trust loss

**Prevention:**
1. **Automated daily backups:**
   - Database: Full backup daily, incremental hourly
   - Retention: 30 days daily, 12 months monthly
   - File storage (product images): Sync to backup location
2. **Test restoration quarterly:**
   - Document restore procedure
   - Verify backup integrity
3. **Multiple backup locations:**
   - Cloud provider backup (Aliyun OSS, Tencent COS)
   - Separate region for disaster recovery
4. Log all backup activities

**Detection:**
- Monitor backup job completion
- Alert on backup failures
- Regular restore drills

**Phase to address:** Phase 1 (MVP) - Basic automated backup; Phase 2 - Tested restore procedures

---

### Pitfall 8: 微信小程序 API 限制忽略 (Ignoring WeChat API Limits)

**What goes wrong:**
APIs fail silently or return errors during peak usage:
- Login failures (频繁调用)
- Image upload failures
- Template message send failures

**Why it happens:**
- WeChat imposes rate limits on APIs
- Not handling error responses
- No retry logic
- Calling APIs from client when should be server-side

**Consequences:**
- User frustration
- Incomplete operations
- Support tickets

**Prevention:**
1. Know the limits:
   - `wx.login`: No strict limit, but don't call excessively
   - `wx.request`: 10 concurrent connections max
   - Template messages: Per-user limits
   - Image upload: Size and count limits
2. Implement server-side caching for tokens
3. Add retry logic with exponential backoff
4. Queue non-critical operations (analytics, non-urgent updates)

**Detection:**
- Monitor API error rates
- Log all WeChat API responses
- Set up alerts for limit-related errors

**Phase to address:** Phase 1 (MVP) - Basic error handling; Phase 2 - Robust retry/queue systems

---

### Pitfall 9: 积分兑换滥用 (Points Redemption Abuse)

**What goes wrong:**
Users exploit points system:
- Creating multiple accounts for signup bonuses
- Finding logic bugs in points calculation
- Redeeming without sufficient balance check

**Why it happens:**
- No server-side points validation
- Points calculated client-side
- No rate limiting on redemption
- Missing transaction audit trail

**Consequences:**
- Financial loss from "free" products
- System abuse
- Unfair advantage

**Prevention:**
1. All points logic server-side only
2. Validate points balance before redemption:
   ```sql
   UPDATE user_points 
   SET balance = balance - ? 
   WHERE user_id = ? AND balance >= ?;
   -- Check affected rows
   ```
3. Log all points transactions
4. Rate limit redemption attempts
5. One account per verified user

**Detection:**
- Monitor for unusual points activity
- Flag accounts with high redemption rates
- Regular audit of points ledger

**Phase to address:** Phase 2 (Gift Package Zone) - When points redemption implemented

---

## Minor Pitfalls

### Pitfall 10: 商品图片未压缩 (Uncompressed Product Images)

**What goes wrong:**
- Slow page load (violates 2-second requirement)
- High bandwidth costs
- Poor user experience on slow networks

**Prevention:**
- Compress images before upload (server-side)
- Use CDN with on-the-fly compression
- Implement responsive images (different sizes for different contexts)
- Lazy load below-fold images

**Phase to address:** Phase 1 (MVP)

---

### Pitfall 11: 订单状态机混乱 (Confusing Order State Machine)

**What goes wrong:**
- Orders stuck in invalid states
- No clear status transitions
- Inconsistent status across frontend and backend

**Prevention:**
- Define clear order states upfront:
  - 待支付 (Pending Payment)
  - 已支付 (Paid)
  - 待发货 (Pending Shipment)
  - 已发货 (Shipped)
  - 已完成 (Completed)
  - 已取消 (Cancelled)
  - 售后中 (After-sales)
- Implement state machine with allowed transitions
- Log all state changes with timestamp and actor

**Phase to address:** Phase 1 (MVP)

---

### Pitfall 12: 微信登录态过期处理不当 (Improper WeChat Login Session Expiry)

**What goes wrong:**
- User suddenly logged out mid-operation
- Lost cart contents
- Confusing UX

**Prevention:**
- Handle `wx.checkSession` failure gracefully
- Silently refresh token when expired
- Persist cart data server-side (not just local storage)
- Show clear re-login prompt when needed

**Phase to address:** Phase 1 (MVP)

---

## Phase-Specific Warnings

| Phase | Likely Pitfall | Mitigation |
|-------|---------------|------------|
| Phase 1 (MVP) | 审核拒绝 (Approval Rejection) | Prepare all licenses, implement age verification |
| Phase 1 (MVP) | 支付安全 (Payment Security) | Implement proper callback verification |
| Phase 1 (MVP) | 库存超卖 (Overselling) | Database-level optimistic locking |
| Phase 1 (MVP) | setData性能 (Performance) | Image optimization, lazy loading |
| Phase 2 (Post-MVP) | 积分滥用 (Points Abuse) | Server-side validation, audit trail |
| Phase 2 (Post-MVP) | 后台效率 (Admin Efficiency) | Batch operations, better UX |
| Ongoing | 数据备份 (Backup) | Automated backups, tested restore |

---

## Compliance Checklist for 酒水电商

**Legal Requirements:**
- [ ] 食品经营许可证 (Food Business License)
- [ ] 酒类销售资质 (Alcohol Sales Qualification)
- [ ] ICP备案 (ICP Filing)
- [ ] 未成年人保护机制 (Minors Protection - Age Verification)
- [ ] 酒类广告合规 (Alcohol Advertising Compliance - no minors targeting)
- [ ] 用户隐私协议 (User Privacy Policy)
- [ ] 用户协议 (User Terms of Service)

**WeChat Platform Requirements:**
- [ ] Correct service category classification
- [ ] All required licenses uploaded
- [ ] Privacy policy accessible in mini program
- [ ] Customer service contact available
- [ ] No prohibited content (no implied health benefits, no minors in imagery)

---

## Sources

**Legal/Compliance:**
- 未成年人保护法 (Minors Protection Law of PRC)
- 酒类流通管理办法 (Alcohol Circulation Management Regulations)
- 酒类广告管理办法 (Alcohol Advertising Management Regulations)
- 电子商务法 (E-commerce Law of PRC)

**Technical References:**
- WeChat Mini Program官方文档 - Performance Guidelines
- WeChat Pay开发文档 - Security Best Practices
- Distributed systems patterns for inventory management (Martinfowler.com)
- OWASP security guidelines for e-commerce

**Community Knowledge:**
- WeChat Mini Program developer communities (掘金, 知乎)
- E-commerce system design patterns

**Confidence Levels:**
- Legal compliance requirements: MEDIUM (based on Chinese law knowledge, recommend legal review)
- WeChat审核 requirements: MEDIUM (based on platform patterns, verify with latest guidelines)
- Technical pitfalls: HIGH (well-documented patterns in distributed systems and mini program development)
- Performance thresholds: HIGH (explicitly stated in project requirements)
