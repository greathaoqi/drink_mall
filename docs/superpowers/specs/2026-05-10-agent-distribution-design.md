# 多商户代理分销系统设计文档

**项目**: 酒水商城 (Drink Mall)
**日期**: 2026-05-10
**状态**: 已批准，待实施

---

## 一、需求概述

在现有微信小程序酒水商城基础上，叠加多级代理分销体系，支持：

- **分销返佣**：代理推广专属店铺链接/二维码，客户下单后代理获得差价收益 + 平台返点
- **独立子店铺**：每个代理有独立的商品陈列和定价（从平台商品库选品，自定零售价）
- **采购分销**：代理以平台出货价进货，赚取差价

---

## 二、核心设定

| 项目 | 决策 |
|------|------|
| 代理层级 | 三级及以上（树状无限层级，典型：省代→市代→县代→客户） |
| 佣金模式 | 差价（代理价 - 平台出货价）+ 平台逐级返点 |
| 选品权限 | 只能从平台商品库选品，不能自行上传商品 |
| 客户绑定 | 扫码 + 分享链接均可绑定，先绑先得，终身归属 |
| 提现方式 | 微信企业付款（实时）+ 手动申请（平台线下打款） |
| 代理入驻 | 缴纳保证金 + 管理员审批 |

---

## 三、数据模型

### 3.1 agent_tiers（代理等级配置）

```sql
CREATE TABLE agent_tiers (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(32) NOT NULL COMMENT '等级名称，如省代/市代/县代',
    level       INT NOT NULL COMMENT '层级序号，1最高',
    deposit_amount    DECIMAL(10,2) NOT NULL COMMENT '保证金金额',
    rebate_rate       DECIMAL(5,4) NOT NULL DEFAULT 0 COMMENT '平台返点比例，如0.03=3%',
    min_price_rate    DECIMAL(5,4) NOT NULL DEFAULT 1.05 COMMENT '最低定价比例（相对平台出货价）',
    status      TINYINT DEFAULT 1,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### 3.2 agents（代理账户）

```sql
CREATE TABLE agents (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id         BIGINT NOT NULL UNIQUE COMMENT '关联users表',
    tier_id         BIGINT NOT NULL COMMENT '代理等级',
    parent_agent_id BIGINT DEFAULT NULL COMMENT '上级代理ID（树状结构）',
    store_name      VARCHAR(128) NOT NULL COMMENT '店铺名称',
    store_code      VARCHAR(32) NOT NULL UNIQUE COMMENT '唯一邀请码/店铺标识',
    store_qr_url    VARCHAR(512) COMMENT '店铺二维码图片URL',
    balance         DECIMAL(10,2) DEFAULT 0.00 COMMENT '可提现余额',
    frozen_balance  DECIMAL(10,2) DEFAULT 0.00 COMMENT '冻结中余额（订单未完成）',
    total_sales     DECIMAL(12,2) DEFAULT 0.00 COMMENT '累计销售额',
    status          VARCHAR(16) DEFAULT 'pending' COMMENT 'pending/active/disabled',
    apply_amount    DECIMAL(10,2) COMMENT '已缴保证金',
    applied_at      DATETIME,
    approved_at     DATETIME,
    approved_by     BIGINT COMMENT '审批管理员ID',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (tier_id) REFERENCES agent_tiers(id),
    FOREIGN KEY (parent_agent_id) REFERENCES agents(id),
    INDEX idx_store_code (store_code),
    INDEX idx_parent (parent_agent_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### 3.3 agent_products（代理选品与定价）

```sql
CREATE TABLE agent_products (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    agent_id     BIGINT NOT NULL,
    product_id   BIGINT NOT NULL COMMENT '关联products表',
    agent_price  DECIMAL(10,2) NOT NULL COMMENT '代理定价（不低于平台价×min_price_rate）',
    status       TINYINT DEFAULT 1,
    created_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_agent_product (agent_id, product_id),
    FOREIGN KEY (agent_id) REFERENCES agents(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### 3.4 agent_customers（客户归属）

```sql
CREATE TABLE agent_customers (
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id    BIGINT NOT NULL UNIQUE COMMENT '一个用户只能归属一个代理',
    agent_id   BIGINT NOT NULL,
    bound_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
    bound_via  VARCHAR(16) COMMENT 'qrcode/link',
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (agent_id) REFERENCES agents(id),
    INDEX idx_agent (agent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### 3.5 agent_commissions（佣金明细）

```sql
CREATE TABLE agent_commissions (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id        BIGINT NOT NULL COMMENT '关联orders表',
    agent_id        BIGINT NOT NULL COMMENT '受益代理',
    user_id         BIGINT NOT NULL COMMENT '下单客户',
    commission_type VARCHAR(16) NOT NULL COMMENT 'margin(差价)/rebate(返点)',
    tier_level      INT COMMENT '返点层级（rebate类型专用）',
    amount          DECIMAL(10,2) NOT NULL,
    status          VARCHAR(16) DEFAULT 'pending' COMMENT 'pending/settled/cancelled',
    settled_at      DATETIME,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (agent_id) REFERENCES agents(id),
    INDEX idx_order (order_id),
    INDEX idx_agent_status (agent_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### 3.6 agent_applications（入驻申请）

```sql
CREATE TABLE agent_applications (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id         BIGINT NOT NULL,
    tier_id         BIGINT NOT NULL,
    parent_agent_id BIGINT COMMENT '希望挂靠的上级代理',
    store_name      VARCHAR(128) NOT NULL,
    pay_amount      DECIMAL(10,2) NOT NULL,
    payment_no      VARCHAR(64) COMMENT '支付流水号',
    status          VARCHAR(16) DEFAULT 'pending' COMMENT 'pending/approved/rejected',
    reject_reason   VARCHAR(255),
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    reviewed_at     DATETIME,
    reviewed_by     BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_status (status),
    INDEX idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### 3.7 agent_withdrawals（提现申请）

```sql
CREATE TABLE agent_withdrawals (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    agent_id    BIGINT NOT NULL,
    amount      DECIMAL(10,2) NOT NULL,
    method      VARCHAR(16) NOT NULL COMMENT 'wechat/manual',
    status      VARCHAR(16) DEFAULT 'pending' COMMENT 'pending/processing/done/failed',
    transfer_no VARCHAR(64) COMMENT '微信转账单号或线下流水号',
    remark      VARCHAR(255),
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    finished_at DATETIME,
    FOREIGN KEY (agent_id) REFERENCES agents(id),
    INDEX idx_agent_status (agent_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

---

## 四、佣金计算逻辑

### 4.1 计算时机
- **冻结**：客户下单成功后，佣金状态为 `pending`，余额计入 `frozen_balance`
- **结算**：客户确认收货后，佣金状态改为 `settled`，转入 `balance`
- **取消**：订单取消后，佣金状态改为 `cancelled`，解冻

### 4.2 计算公式

```
客户实付 = agent_products.agent_price

直接代理差价 = agent_price - product.price（平台出货价）

各级返点（沿 parent_agent_id 链向上遍历）：
  当前代理返点 = 客户实付 × agent_tiers.rebate_rate
  上级代理返点 = 客户实付 × 上级tier.rebate_rate
  ... 直到根节点（省代）
```

### 4.3 示例

```
平台出货价 ¥100，县代定价 ¥125，客户实付 ¥125
  县代（直接代理）差价：¥125 - ¥100 = ¥25
  市代返点（rebate_rate=1.5%）：¥125 × 1.5% = ¥1.875 ≈ ¥1.88
  省代返点（rebate_rate=0.5%）：¥125 × 0.5% = ¥0.625 ≈ ¥0.63
```

---

## 五、后端架构

### 5.1 包结构

```
com.drinkmall/
├── controller/                   # 现有（不改动）
├── service/impl/                 # 现有（OrderServiceImpl 修改2处）
└── agent/
    ├── controller/
    │   ├── AgentStoreController        # GET /api/v1/store/{storeCode}/**
    │   ├── AgentDashboardController    # GET/POST /api/v1/agent/**
    │   └── AdminAgentController        # /api/v1/admin/agent/**
    ├── service/
    │   ├── AgentService
    │   ├── AgentProductService
    │   ├── AgentBindingService
    │   ├── CommissionService
    │   └── AgentWithdrawalService
    ├── entity/        # 7个实体类
    ├── mapper/        # 7个Mapper接口
    └── dto/           # 请求/响应DTO
```

### 5.2 现有代码修改点（仅2处）

**OrderServiceImpl.createOrder**：
1. 查 `agent_customers` 判断客户是否有归属代理
2. 若有，从 `agent_products` 取代理定价作为订单价格
3. 调用 `CommissionService.createPendingCommissions(orderId)` 创建冻结佣金

**OrderServiceImpl.confirmReceipt**：
1. 调用 `CommissionService.settle(orderId)` 结算佣金

### 5.3 API 列表

**客户端**
```
GET  /api/v1/store/{storeCode}             进店（绑定客户+获取店铺信息）
GET  /api/v1/store/{storeCode}/products    店铺商品列表（含代理定价）
GET  /api/v1/store/{storeCode}/products/{id} 商品详情（代理定价）
```

**代理工作台**
```
POST /api/v1/agent/apply                   申请成为代理
GET  /api/v1/agent/me                      代理基本信息
GET  /api/v1/agent/dashboard               工作台统计
GET  /api/v1/agent/qrcode                  店铺二维码
GET  /api/v1/agent/products                我的选品列表
POST /api/v1/agent/products                添加选品
PUT  /api/v1/agent/products/{id}           修改定价
DELETE /api/v1/agent/products/{id}         下架选品
GET  /api/v1/agent/commissions             佣金明细
GET  /api/v1/agent/downline                下级代理和客户
POST /api/v1/agent/withdraw                申请提现
GET  /api/v1/agent/withdrawals             提现记录
```

**管理后台**
```
GET  /api/v1/admin/agent/tiers             等级列表
PUT  /api/v1/admin/agent/tiers/{id}        修改等级配置
GET  /api/v1/admin/agent/applications      申请列表
POST /api/v1/admin/agent/applications/{id}/approve
POST /api/v1/admin/agent/applications/{id}/reject
GET  /api/v1/admin/agent/list              代理列表（树形）
GET  /api/v1/admin/agent/{id}              代理详情
POST /api/v1/admin/agent/{id}/disable      禁用代理
GET  /api/v1/admin/agent/withdrawals       提现申请列表
POST /api/v1/admin/agent/withdrawals/{id}/process 处理提现
```

---

## 六、前端改造

### 6.1 小程序新增页面

```
src/pages/
├── store/index.vue               扫码/链接落地页（绑定 + 进店）
└── agent/
    ├── apply/index.vue           代理申请
    ├── dashboard/index.vue       代理工作台
    ├── products/index.vue        选品管理
    ├── commissions/index.vue     佣金明细
    ├── downline/index.vue        下级列表
    ├── qrcode/index.vue          店铺二维码
    └── withdraw/index.vue        提现申请
```

### 6.2 小程序现有页面微调

- `profile/index.vue`：增加"代理中心"入口（已是代理）/ "成为代理"入口（未申请）
- `product/detail/index.vue`：若客户有代理归属，显示代理价格而非平台原价
- `pages.json`：注册新页面路由

### 6.3 管理后台新增

```
src/views/agent/
├── applications.vue   入驻申请审批
├── list.vue           代理列表（树形）
├── tiers.vue          等级配置
└── withdrawals.vue    提现审核
```

侧边栏 `MainLayout.vue` 增加"代理管理"菜单组。

---

## 七、改动范围总结

| 模块 | 类型 | 规模 |
|------|------|------|
| 数据库迁移（V11） | 新增 | 7张表 |
| 后端 agent 模块 | 新增 | 7实体+7Mapper+5Service+3Controller+若干DTO |
| OrderServiceImpl | 修改 | 2处集成点，约20行 |
| 小程序新增页面 | 新增 | 8个页面 |
| 小程序现有页面 | 微调 | profile、product/detail、pages.json |
| 管理后台新增页面 | 新增 | 4个页面 |
| 管理后台现有文件 | 微调 | MainLayout.vue侧边栏 |

**现有主商城功能零破坏，代理功能完全叠加式扩展。**

---

## 八、风险与注意事项

1. **微信企业付款资质**：需要企业微信认证 + 开通企业付款权限，开发阶段可先只实现手动提现
2. **佣金精度**：金额计算全部使用 `BigDecimal`，HALF_UP 取两位小数，避免浮点误差
3. **循环代理防护**：设置上级代理时需检查不能形成环状（A→B→A），数据库层加校验
4. **store_code 生成**：使用 6 位随机字母数字，生成时检查唯一性
5. **客户绑定时机**：未登录用户扫码后先记录 storeCode 到 session，登录后再绑定

---

*设计文档版本: 1.0 | 作者: Claude + Darius*
