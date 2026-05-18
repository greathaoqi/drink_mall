# Requirements: 酒水商城 (Drink Mall)

**Defined:** 2026-05-08
**Core Value:** 用户可以方便地在微信小程序中浏览和购买酒水商品，享受流畅的购物体验，平台可以高效管理商品和订单。

## v1 Requirements

### Authentication

- [ ] **AUTH-01**: 用户可以通过微信授权一键登录小程序
- [ ] **AUTH-02**: 游客模式下用户可以浏览公开内容（首页、商品列表、公告）
- [ ] **AUTH-03**: 登录前必须勾选用户协议和隐私协议
- [ ] **AUTH-04**: 酒水购买前需进行年龄验证（18岁以上）

### Product Catalog

- [ ] **PROD-01**: 用户可以按系列分类浏览商品（支持多级分类）
- [ ] **PROD-02**: 用户可以通过关键词搜索商品
- [ ] **PROD-03**: 用户可以按价格区间筛选商品
- [ ] **PROD-04**: 用户可以按销量排序商品
- [ ] **PROD-05**: 商品列表展示商品图片、名称、价格、销量
- [ ] **PROD-06**: 商品详情页展示轮播图、价格、规格、库存、详情介绍、销量

### Product Zones

- [ ] **ZONE-01**: 主产品专区展示核心酒水商品
- [ ] **ZONE-02**: 零售专区展示零售商品，所有用户价格一致
- [ ] **ZONE-03**: 礼包专区展示积分兑换商品，纯积分支付

### Shopping Cart

- [ ] **CART-01**: 用户可以将商品加入购物车
- [ ] **CART-02**: 用户可以修改购物车商品数量
- [ ] **CART-03**: 用户可以删除购物车商品
- [ ] **CART-04**: 购物车显示商品总价和数量角标
- [ ] **CART-05**: 购买数量不可超过库存上限

### Checkout & Payment

- [ ] **CHECK-01**: 结算页展示商品列表、规格、数量、单价、总价
- [ ] **CHECK-02**: 结算页自动计算实付金额（商品总价，全场包邮）
- [ ] **CHECK-03**: 用户可以选择或新增收货地址
- [ ] **CHECK-04**: 用户可以提交订单并跳转微信支付
- [ ] **CHECK-05**: 支付成功后订单状态自动更新
- [ ] **CHECK-06**: 超时未支付订单自动取消并释放库存

### Order Management

- [ ] **ORDER-01**: 用户可以查看订单列表（全部/待付款/待发货/待收货/已完成/已取消）
- [ ] **ORDER-02**: 用户可以查看订单详情（商品、地址、支付、物流信息）
- [ ] **ORDER-03**: 用户可以取消待付款订单
- [ ] **ORDER-04**: 用户可以确认收货
- [ ] **ORDER-05**: 用户可以申请售后

### Address Management

- [ ] **ADDR-01**: 用户可以新增收货地址
- [ ] **ADDR-02**: 用户可以编辑收货地址
- [ ] **ADDR-03**: 用户可以删除收货地址
- [ ] **ADDR-04**: 用户可以设置默认收货地址
- [ ] **ADDR-05**: 收货地址最多保存10个

### Content & Engagement

- [ ] **CONT-01**: 首页顶部轮播图展示3-5张图片，支持自动轮播和手动切换
- [ ] **CONT-02**: 轮播图点击可跳转至指定页面
- [ ] **CONT-03**: 平台公告列表展示发布人、摘要、发布时间
- [ ] **CONT-04**: 用户可以查看公告完整内容
- [ ] **CONT-05**: 动态页面展示短视频列表
- [ ] **CONT-06**: 用户可以播放视频、横屏观看、调整进度

### Personal Center

- [ ] **USER-01**: 用户可以查看个人信息（头像、昵称、等级、业绩、收益）
- [ ] **USER-02**: 用户可以编辑头像和昵称
- [ ] **USER-03**: 用户可以查看账户余额、冻结余额、积分
- [ ] **USER-04**: 用户可以申请余额提现
- [ ] **USER-05**: 用户可以查看余额流水、积分流水、提现记录
- [ ] **USER-06**: 用户可以查看帮助中心内容
- [ ] **USER-07**: 用户可以通过客服入口联系客服

### Points System

- [ ] **POINTS-01**: 用户购买商品可获得积分
- [ ] **POINTS-02**: 用户可在礼包专区用积分兑换商品
- [ ] **POINTS-03**: 积分不足时无法兑换，提示积分不足
- [ ] **POINTS-04**: 积分兑换商品不计入业绩和分销奖励

### Admin - Product Management

- [ ] **ADM-PROD-01**: 管理员可以新增、编辑、删除商品分类（最多3级）
- [ ] **ADM-PROD-02**: 管理员可以新增、编辑、删除、上下架商品
- [ ] **ADM-PROD-03**: 管理员可以配置商品信息（名称、图片、详情、价格、规格、库存、分类、专区、积分）
- [ ] **ADM-PROD-04**: 管理员可以调整库存、设置库存预警
- [ ] **ADM-PROD-05**: 管理员可以查看库存流水记录

### Admin - Order Management

- [ ] **ADM-ORDER-01**: 管理员可以查看所有订单列表（多条件筛选）
- [ ] **ADM-ORDER-02**: 管理员可以查看订单详情（商品、用户、地址、支付、物流、奖励）
- [ ] **ADM-ORDER-03**: 管理员可以发货、取消订单、修改价格
- [ ] **ADM-ORDER-04**: 管理员可以处理售后申请
- [ ] **ADM-ORDER-05**: 所有操作记录日志

### Admin - User Management

- [ ] **ADM-USER-01**: 管理员可以查看所有用户列表
- [ ] **ADM-USER-02**: 管理员可以查看用户详情（昵称、手机号、等级、业绩、收益）
- [ ] **ADM-USER-03**: 管理员可以导出用户数据

### Admin - Content Management

- [ ] **ADM-CONT-01**: 管理员可以管理首页、动态页轮播图
- [ ] **ADM-CONT-02**: 管理员可以发布、编辑、下架公告
- [ ] **ADM-CONT-03**: 管理员可以上传、编辑、下架视频
- [ ] **ADM-CONT-04**: 管理员可以管理帮助中心内容

### Admin - Financial Management

- [ ] **ADM-FIN-01**: 管理员可以审核提现申请
- [ ] **ADM-FIN-02**: 管理员可以查看资金流水（余额、积分、提现）
- [ ] **ADM-FIN-03**: 管理员可以查看财务统计报表

### Admin - System Management

- [ ] **ADM-SYS-01**: 管理员可以管理后台账号（新增、编辑、删除、启用/禁用）
- [ ] **ADM-SYS-02**: 管理员可以配置角色权限
- [ ] **ADM-SYS-03**: 系统记录所有操作日志
- [ ] **ADM-SYS-04**: 管理员可以配置系统参数

### Content Payment

- [ ] **CPAY-01**: 用户可以通过微信支付购买付费内容
- [ ] **CPAY-02**: 管理员可以查看内容购买记录（用户、内容、价格、支付方式、时间）
- [ ] **CPAY-03**: 用户可以通过关键词搜索内容
- [ ] **CPAY-04**: 管理员可以管理动态内容分类
- [ ] **CPAY-05**: 未购买付费内容的用户可以预览部分内容
- [ ] **CPAY-06**: 用户可以点赞/取消点赞内容，内容显示点赞数
- [ ] **CPAY-07**: 管理员可以查看内容购买统计和收益报表

## v2 Requirements

Deferred to future release.

### Notifications

- **NOTF-01**: 用户收到订单状态变更通知
- **NOTF-02**: 用户收到发货通知

### Advanced Features

- **ADV-01**: 商品评价功能
- **ADV-02**: 商品收藏功能
- **ADV-03**: 订单物流实时追踪

## Out of Scope

| Feature | Reason |
|---------|--------|
| 联营商等级体系 | 用户需求明确剔除分销内容 |
| 合伙人等级体系 | 用户需求明确剔除分销内容 |
| 招商专区 | 用户需求明确剔除分销内容 |
| 分销奖励计算 | 用户需求明确剔除分销内容 |
| 团队业绩统计 | 用户需求明确剔除分销内容 |
| DF余额账户 | 用户需求明确剔除分销内容 |
| 上级绑定规则 | 用户需求明确剔除分销内容 |
| 分享邀请绑定上级 | 用户需求明确剔除分销内容 |
| 用户等级调整功能 | 用户需求明确剔除分销内容 |
| 自有产品上架 | 依赖合伙人体系，已剔除 |
| 第三方支付系统开发 | 仅对接微信支付 |
| 物流系统开发 | 仅对接物流查询API |
| 电子合同系统 | 仅提供签字页，无完整功能 |
| 供应链/仓储管理 | 仅做线上库存管理 |
| APP/H5/支付宝小程序 | 仅微信小程序端 |

## Traceability

Which phases cover which requirements. Updated during roadmap creation.

| Requirement | Phase | Status |
|-------------|-------|--------|
| AUTH-01 | Phase 1 | Pending |
| AUTH-02 | Phase 1 | Pending |
| AUTH-03 | Phase 1 | Pending |
| AUTH-04 | Phase 1 | Pending |
| USER-07 | Phase 1 | Pending |
| PROD-01 | Phase 2 | Pending |
| PROD-02 | Phase 2 | Pending |
| PROD-03 | Phase 2 | Pending |
| PROD-04 | Phase 2 | Pending |
| PROD-05 | Phase 2 | Pending |
| PROD-06 | Phase 2 | Pending |
| ZONE-01 | Phase 2 | Pending |
| ZONE-02 | Phase 2 | Pending |
| ZONE-03 | Phase 4 | Pending |
| CART-01 | Phase 2 | Pending |
| CART-02 | Phase 2 | Pending |
| CART-03 | Phase 2 | Pending |
| CART-04 | Phase 2 | Pending |
| CART-05 | Phase 2 | Pending |
| CHECK-01 | Phase 3 | Pending |
| CHECK-02 | Phase 3 | Pending |
| CHECK-03 | Phase 3 | Pending |
| CHECK-04 | Phase 3 | Pending |
| CHECK-05 | Phase 3 | Pending |
| CHECK-06 | Phase 3 | Pending |
| ORDER-01 | Phase 3 | Pending |
| ORDER-02 | Phase 3 | Pending |
| ORDER-03 | Phase 3 | Pending |
| ORDER-04 | Phase 3 | Pending |
| ORDER-05 | Phase 3 | Pending |
| ADDR-01 | Phase 2 | Pending |
| ADDR-02 | Phase 2 | Pending |
| ADDR-03 | Phase 2 | Pending |
| ADDR-04 | Phase 2 | Pending |
| ADDR-05 | Phase 2 | Pending |
| CONT-01 | Phase 4 | Pending |
| CONT-02 | Phase 4 | Pending |
| CONT-03 | Phase 4 | Pending |
| CONT-04 | Phase 4 | Pending |
| CONT-05 | Phase 4 | Pending |
| CONT-06 | Phase 4 | Pending |
| USER-01 | Phase 2 | Pending |
| USER-02 | Phase 2 | Pending |
| USER-03 | Phase 3 | Pending |
| USER-04 | Phase 3 | Pending |
| USER-05 | Phase 3 | Pending |
| USER-06 | Phase 4 | Pending |
| POINTS-01 | Phase 4 | Pending |
| POINTS-02 | Phase 4 | Pending |
| POINTS-03 | Phase 4 | Pending |
| POINTS-04 | Phase 4 | Pending |
| ADM-PROD-01 | Phase 2 | Pending |
| ADM-PROD-02 | Phase 2 | Pending |
| ADM-PROD-03 | Phase 2 | Pending |
| ADM-PROD-04 | Phase 2 | Pending |
| ADM-PROD-05 | Phase 2 | Pending |
| ADM-ORDER-01 | Phase 3 | Pending |
| ADM-ORDER-02 | Phase 3 | Pending |
| ADM-ORDER-03 | Phase 3 | Pending |
| ADM-ORDER-04 | Phase 3 | Pending |
| ADM-ORDER-05 | Phase 3 | Pending |
| ADM-USER-01 | Phase 2 | Pending |
| ADM-USER-02 | Phase 2 | Pending |
| ADM-USER-03 | Phase 2 | Pending |
| ADM-CONT-01 | Phase 4 | Pending |
| ADM-CONT-02 | Phase 4 | Pending |
| ADM-CONT-03 | Phase 4 | Pending |
| ADM-CONT-04 | Phase 4 | Pending |
| ADM-FIN-01 | Phase 3 | Pending |
| ADM-FIN-02 | Phase 5 | Pending |
| ADM-FIN-03 | Phase 5 | Pending |
| ADM-SYS-01 | Phase 5 | Pending |
| ADM-SYS-02 | Phase 5 | Pending |
| ADM-SYS-03 | Phase 5 | Pending |
| ADM-SYS-04 | Phase 5 | Pending |
| CPAY-01 | Phase 6 | Pending |
| CPAY-02 | Phase 6 | Pending |
| CPAY-03 | Phase 6 | Pending |
| CPAY-04 | Phase 6 | Pending |
| CPAY-05 | Phase 6 | Pending |
| CPAY-06 | Phase 6 | Pending |
| CPAY-07 | Phase 6 | Pending |

**Coverage:**
- v1 requirements: 68 total
- Content Payment requirements: 7 total
- Mapped to phases: 75
- Unmapped: 0

---
*Requirements defined: 2026-05-08*
*Last updated: 2026-05-08 after roadmap creation*
