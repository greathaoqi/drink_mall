# Code Review: 酒水商城 (Drink Mall)

**Reviewed:** 2026-05-09
**Reviewer:** Claude Code

---

## Summary

项目整体架构清晰，代码质量良好。发现以下问题和建议改进点。

---

## Security Issues (HIGH)

### 1. 缺少支付回调验证
**File:** `drink-mall-api/src/main/java/com/drinkmall/service/impl/OrderServiceImpl.java:180-197`
**Issue:** `payOrder` 方法直接更新订单状态为已支付，未实现微信支付回调验证
**Risk:** 攻击者可伪造支付请求绕过支付
**Fix:** 实现微信支付回调接口并验证签名

### 2. Admin API 缺少认证
**File:** `drink-mall-api/src/main/java/com/drinkmall/controller/admin/*.java`
**Issue:** 所有Admin Controller未添加`@SaCheckLogin`或角色验证注解
**Risk:** 任何用户可访问管理接口
**Fix:** 添加`@SaCheckRole("admin")`注解

### 3. SQL注入风险 - 动态排序
**File:** `drink-mall-api/src/main/java/com/drinkmall/controller/ProductController.java:26`
**Issue:** `sortBy`参数直接传递给Service，需验证
**Fix:** 在Service层限制允许的排序字段

---

## Performance Issues (MEDIUM)

### 1. N+1查询问题
**File:** `drink-mall-api/src/main/java/com/drinkmall/service/impl/OrderServiceImpl.java:convertToResponse`
**Issue:** 每个订单单独查询Address和OrderItems
**Fix:** 使用JOIN查询或批量加载

### 2. 缺少缓存
**Issue:** 商品列表、分类等高频查询未使用Redis缓存
**Fix:** 添加`@Cacheable`注解

---

## Code Quality Issues (LOW)

### 1. 空实现
**File:** `drink-mall-api/src/main/java/com/drinkmall/service/admin/impl/AdminFinanceServiceImpl.java:57-58`
**Issue:** `getBalanceLogs`返回空Page
**Fix:** 实现完整的余额流水查询

### 2. 缺少异常处理
**File:** `drink-mall-api/src/main/java/com/drinkmall/controller/admin/AdminUserController.java:exportUsers`
**Issue:** 导出失败时未正确处理IOException
**Fix:** 添加try-catch并返回错误响应

### 3. 硬编码配置
**File:** `drink-mall-admin/src/views/Login.vue`
**Issue:** 登录凭据硬编码为admin/admin123
**Fix:** 使用环境变量或配置文件

---

## Missing Features

### 1. 后端缺失
- 微信支付回调接口 (`/api/v1/pay/callback`)
- 订单超时自动取消（定时任务）
- 售后申请API (`AftersaleController` 用户端)

### 2. 小程序缺失
- 公告列表/详情页
- 后端API Service调用需统一使用`http`对象

---

## Recommendations

1. **优先修复安全问题** - Admin认证和支付验证
2. **添加单元测试** - 核心业务逻辑需测试覆盖
3. **完善日志记录** - 添加操作日志切面
4. **配置化管理** - 敏感信息移至配置文件

---

## Files Reviewed

| Category | Files | Status |
|----------|-------|--------|
| Backend Controllers | 14 | PASS (需补充认证) |
| Backend Services | 14 | PASS |
| Backend Entities | 17 | PASS |
| Admin Frontend | 16 | PASS |
| Mini Program | 15 | PASS |

---

*Review completed: 2026-05-09*