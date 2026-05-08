# Architecture Patterns

**Domain:** WeChat Mini Program E-commerce (Liquor Retail)
**Researched:** 2026-05-08

## Recommended Architecture

### System Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                        Client Layer                              │
├─────────────────────────┬───────────────────────────────────────┤
│   WeChat Mini Program   │        Admin Dashboard (Web)          │
│   (User-Facing)         │        (Management)                   │
└───────────┬─────────────┴───────────────┬───────────────────────┘
            │                             │
            │ HTTPS/REST API              │ HTTPS/REST API
            │                             │
┌───────────┴─────────────────────────────┴───────────────────────┐
│                        API Gateway Layer                         │
│  - Authentication/Authorization                                  │
│  - Rate Limiting                                                 │
│  - Request Routing                                               │
└───────────────────────────────┬─────────────────────────────────┘
                                │
┌───────────────────────────────┴─────────────────────────────────┐
│                     Application Layer                            │
├────────────────┬────────────────┬────────────────┬──────────────┤
│  User Service  │ Product Service│  Order Service │ Content Svc  │
│  - Auth        │  - Catalog     │  - Cart        │  - Banners   │
│  - Profile     │  - Inventory   │  - Checkout    │  - Notices   │
│  - Address     │  - Categories  │  - Payment     │  - Videos    │
└────────┬───────┴────────┬───────┴────────┬───────┴──────────────┘
         │                │                │
┌────────┴────────────────┴────────────────┴──────────────────────┐
│                     Data Layer                                   │
├─────────────────────────┬───────────────────────────────────────┤
│   Primary Database      │      Cache Layer                      │
│   (MySQL/PostgreSQL)    │      (Redis)                          │
└─────────────────────────┴───────────────────────────────────────┘
                                │
┌───────────────────────────────┴─────────────────────────────────┐
│                   External Services                              │
├─────────────────┬─────────────────┬─────────────────────────────┤
│  WeChat Pay     │  WeChat Auth    │  Logistics API              │
└─────────────────┴─────────────────┴─────────────────────────────┘
```

## Component Boundaries

### 1. WeChat Mini Program Frontend

**Responsibility:** User-facing shopping experience

| Component | Responsibility | Communicates With |
|-----------|---------------|-------------------|
| `pages/` | Page-level views (home, product, cart, orders, profile) | Backend API via `wx.request()` |
| `components/` | Reusable UI components (product-card, order-item, search-bar) | Parent pages via properties/events |
| `utils/` | Helper functions (format, validation, request wrapper) | All pages/components |
| `services/` | API call abstractions (userService, productService, orderService) | Backend API |
| `store/` | Global state management (cart, user session) | All pages via getters/actions |

**File Structure:**
```
miniprogram/
├── app.js              # App initialization, global data
├── app.json            # Pages config, tabBar, window settings
├── app.wxss            # Global styles
├── pages/
│   ├── index/          # Home page
│   ├── product/        # Product list & detail
│   ├── cart/           # Shopping cart
│   ├── order/          # Order management
│   └── profile/        # User center
├── components/
│   ├── product-card/   # Product display card
│   ├── order-item/     # Order list item
│   └── search-bar/     # Search component
├── services/
│   ├── api.js          # Base API configuration
│   ├── user.js         # User-related APIs
│   ├── product.js      # Product-related APIs
│   └── order.js        # Order-related APIs
├── utils/
│   ├── request.js      # wx.request wrapper with auth
│   ├── auth.js         # Login/token management
│   └── util.js         # Common utilities
└── store/
    └── index.js        # State management (MobX/Redux-like)
```

### 2. Backend API

**Responsibility:** Business logic and data management

| Service | Responsibility | Communicates With |
|---------|---------------|-------------------|
| **User Service** | Authentication, profile management, address book | WeChat Auth API, Database |
| **Product Service** | Product catalog, categories, inventory, search | Database, Cache (Redis) |
| **Order Service** | Cart management, order lifecycle, checkout flow | Database, WeChat Pay API |
| **Content Service** | Banners, announcements, videos, help articles | Database, Cache |
| **Payment Service** | Payment processing, refunds, transaction records | WeChat Pay API, Database |
| **Admin Service** | Management operations, permissions, statistics | Database |

**Recommended Tech Stack:**
- **Framework:** Node.js with Express/Koa OR NestJS (TypeScript)
- **Alternative:** Python Django/FastAPI OR Java Spring Boot
- **Why Node.js:** Better ecosystem for WeChat integration, JavaScript full-stack consistency

**API Structure:**
```
/api/v1/
├── /auth
│   ├── POST /login              # WeChat login
│   └── POST /logout             # User logout
├── /users
│   ├── GET /profile             # Get user info
│   ├── PUT /profile             # Update user info
│   └── /addresses               # Address CRUD
├── /products
│   ├── GET /                    # Product list (with filters)
│   ├── GET /:id                 # Product detail
│   ├── GET /categories          # Category tree
│   └── GET /search              # Search products
├── /cart
│   ├── GET /                    # Get cart
│   ├── POST /items              # Add to cart
│   ├── PUT /items/:id           # Update quantity
│   └── DELETE /items/:id        # Remove from cart
├── /orders
│   ├── POST /                   # Create order
│   ├── GET /                    # Order list
│   ├── GET /:id                 # Order detail
│   ├── PUT /:id/cancel          # Cancel order
│   └── PUT /:id/confirm         # Confirm receipt
├── /payments
│   ├── POST /create             # Create payment
│   └── POST /callback           # WeChat Pay callback
└── /content
    ├── GET /banners             # Home banners
    ├── GET /notices             # Announcements
    └── GET /videos              # Video list
```

### 3. Database Schema Design

**Core Tables:**

```sql
-- Users
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    openid VARCHAR(64) UNIQUE NOT NULL,        -- WeChat OpenID
    union_id VARCHAR(64),                       -- WeChat UnionID (if multi-app)
    nickname VARCHAR(128),
    avatar_url VARCHAR(512),
    phone VARCHAR(20),
    balance DECIMAL(10,2) DEFAULT 0.00,         -- Account balance
    points INT DEFAULT 0,                        -- Reward points
    status TINYINT DEFAULT 1,                    -- 1: active, 0: disabled
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- User Addresses
CREATE TABLE addresses (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    receiver_name VARCHAR(64) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    province VARCHAR(32),
    city VARCHAR(32),
    district VARCHAR(32),
    detail_address VARCHAR(255) NOT NULL,
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Product Categories
CREATE TABLE categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    parent_id BIGINT DEFAULT NULL,              -- For hierarchical categories
    name VARCHAR(128) NOT NULL,
    icon_url VARCHAR(512),
    sort_order INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (parent_id) REFERENCES categories(id)
);

-- Products
CREATE TABLE products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    subtitle VARCHAR(255),
    description TEXT,
    main_image VARCHAR(512),
    images JSON,                                 -- Array of image URLs
    price DECIMAL(10,2) NOT NULL,
    original_price DECIMAL(10,2),               -- For showing discounts
    stock INT NOT NULL DEFAULT 0,
    sales INT DEFAULT 0,                        -- Sales count
    zone_type VARCHAR(32) NOT NULL,             -- 'main', 'retail', 'gift'
    status TINYINT DEFAULT 1,                   -- 1: on-shelf, 0: off-shelf
    sort_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- Shopping Cart
CREATE TABLE cart_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    selected BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (product_id) REFERENCES products(id),
    UNIQUE KEY user_product (user_id, product_id)
);

-- Orders
CREATE TABLE orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(64) UNIQUE NOT NULL,       -- Unique order number
    user_id BIGINT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,        -- Total product amount
    pay_amount DECIMAL(10,2) NOT NULL,          -- Actual payment amount
    points_used INT DEFAULT 0,                   -- Points used for discount
    points_amount DECIMAL(10,2) DEFAULT 0.00,   -- Amount deducted by points
    status TINYINT DEFAULT 0,                    -- Order status
    receiver_name VARCHAR(64),
    receiver_phone VARCHAR(20),
    receiver_address VARCHAR(255),
    remark VARCHAR(255),
    paid_at TIMESTAMP,
    shipped_at TIMESTAMP,
    completed_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Order Items
CREATE TABLE order_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(255),
    product_image VARCHAR(512),
    price DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- Payments
CREATE TABLE payments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    payment_no VARCHAR(64) UNIQUE NOT NULL,
    transaction_id VARCHAR(64),                  -- WeChat Pay transaction ID
    amount DECIMAL(10,2) NOT NULL,
    payment_method VARCHAR(32) DEFAULT 'wechat',
    status TINYINT DEFAULT 0,                    -- 0: pending, 1: success, 2: failed
    paid_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id)
);

-- Points Record
CREATE TABLE points_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    points INT NOT NULL,                         -- Positive for earning, negative for using
    type VARCHAR(32) NOT NULL,                   -- 'purchase', 'exchange', 'admin'
    related_order_id BIGINT,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Banners
CREATE TABLE banners (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(128),
    image_url VARCHAR(512) NOT NULL,
    link_type VARCHAR(32),                       -- 'product', 'category', 'url'
    link_value VARCHAR(255),
    sort_order INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Announcements/Notices
CREATE TABLE notices (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    type VARCHAR(32) DEFAULT 'announcement',
    status TINYINT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Videos
CREATE TABLE videos (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    cover_url VARCHAR(512),
    video_url VARCHAR(512) NOT NULL,
    description TEXT,
    views INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Admin Users
CREATE TABLE admin_users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(64) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    name VARCHAR(64),
    role VARCHAR(32) NOT NULL,                   -- 'admin', 'operator'
    status TINYINT DEFAULT 1,
    last_login_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Order Status Flow:**
```
0 (Pending) → 1 (Paid) → 2 (Shipped) → 3 (Completed)
                ↓
            4 (Cancelled)
                ↓
            5 (Refunded)
```

### 4. WeChat Pay Integration

**Payment Flow:**
```
┌──────────┐     ┌──────────┐     ┌──────────┐     ┌──────────┐
│  Mini    │     │ Backend  │     │ WeChat   │     │  User    │
│  Program │     │  Server  │     │   Pay    │     │  Phone   │
└────┬─────┘     └────┬─────┘     └────┬─────┘     └────┬─────┘
     │                │                │                │
     │ 1. Create Order│                │                │
     ├───────────────>│                │                │
     │                │                │                │
     │ 2. Request Payment             │                │
     │<───────────────┤                │                │
     │                │                │                │
     │ 3. wx.requestPayment()         │                │
     ├────────────────┼───────────────>│                │
     │                │                │                │
     │                │                │ 4. Payment UI  │
     │                │                ├───────────────>│
     │                │                │                │
     │                │                │ 5. User Pays   │
     │                │                │<───────────────┤
     │                │                │                │
     │ 6. Payment Result              │                │
     │<───────────────┼────────────────┤                │
     │                │                │                │
     │                │ 7. Callback Notification        │
     │                │<───────────────┤                │
     │                │                │                │
     │                │ 8. Verify & Update Order        │
     │                │ (async)        │                │
     │                │                │                │
     │ 9. Confirm Payment Success     │                │
     │<───────────────┤                │                │
     └────────────────┴────────────────┴────────────────┘
```

**Backend Implementation:**
```javascript
// Payment Service
class PaymentService {
  // Create prepay order
  async createPayment(orderId, userId) {
    const order = await Order.findById(orderId);

    // Call WeChat Pay unified order API
    const prepayData = {
      appid: config.wechat.appId,
      mch_id: config.wechat.mchId,
      nonce_str: generateNonceStr(),
      body: `Order ${order.order_no}`,
      out_trade_no: order.order_no,
      total_fee: Math.round(order.pay_amount * 100), // Convert to cents
      spbill_create_ip: getClientIP(),
      notify_url: config.wechat.payNotifyUrl,
      trade_type: 'JSAPI',
      openid: await getUserOpenId(userId)
    };

    // Sign request
    prepayData.sign = generateSignature(prepayData, config.wechat.apiKey);

    const result = await requestWechatPay(prepayData);
    return result;
  }

  // Handle payment callback
  async handlePaymentCallback(xmlData) {
    const data = parseXml(xmlData);

    // Verify signature
    if (!verifySignature(data, config.wechat.apiKey)) {
      throw new Error('Invalid signature');
    }

    // Update order status
    const order = await Order.findByOrderNo(data.out_trade_no);
    await Order.update(order.id, {
      status: ORDER_STATUS.PAID,
      paid_at: new Date()
    });

    // Create payment record
    await Payment.create({
      order_id: order.id,
      transaction_id: data.transaction_id,
      amount: data.total_fee / 100,
      status: PAYMENT_STATUS.SUCCESS
    });

    // Update product stock and sales
    await ProductService.updateStockAfterPayment(order.id);

    return { return_code: 'SUCCESS', return_msg: 'OK' };
  }
}
```

### 5. Admin Dashboard Architecture

**Tech Stack:**
- **Frontend:** Vue 3 + Element Plus (Recommended for Chinese admin UIs)
- **Alternative:** React + Ant Design Pro

**Component Structure:**
```
admin-dashboard/
├── src/
│   ├── views/
│   │   ├── product/           # Product management
│   │   │   ├── CategoryList.vue
│   │   │   ├── ProductList.vue
│   │   │   └── ProductEdit.vue
│   │   ├── order/             # Order management
│   │   │   ├── OrderList.vue
│   │   │   └── OrderDetail.vue
│   │   ├── user/              # User management
│   │   ├── content/           # Content management
│   │   │   ├── BannerList.vue
│   │   │   ├── NoticeList.vue
│   │   │   └── VideoList.vue
│   │   ├── finance/           # Finance reports
│   │   └── system/            # System settings
│   ├── components/
│   │   ├── layout/            # Layout components
│   │   └── common/            # Shared components
│   ├── store/                 # Vuex/Pinia store
│   ├── router/                # Vue Router
│   ├── api/                   # API modules
│   └── utils/                 # Utilities
└── public/
```

**Key Features:**
- RBAC (Role-Based Access Control)
- Real-time order notifications
- Data visualization (sales, users, orders)
- Batch operations (bulk product import, order export)

### 6. Deployment Architecture

**Recommended Cloud Provider:** Tencent Cloud (closest integration with WeChat ecosystem)

```
┌─────────────────────────────────────────────────────────────────┐
│                         Tencent Cloud                            │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌──────────────┐      ┌──────────────┐      ┌──────────────┐  │
│  │  CLB (Load   │─────>│   CVM/ECS    │─────>│   TencentDB  │  │
│  │  Balancer)   │      │  (App Server)│      │   (MySQL)    │  │
│  └──────────────┘      └──────────────┘      └──────────────┘  │
│         │                      │                     │          │
│         │                      │                     │          │
│  ┌──────┴──────┐      ┌────────┴──────┐      ┌──────┴──────┐  │
│  │    CDN      │      │     Redis     │      │   Object    │  │
│  │ (Static/Img)│      │    Cluster    │      │  Storage    │  │
│  └─────────────┘      └───────────────┘      └─────────────┘  │
│                                                                  │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │  API Gateway                                             │  │
│  │  - Rate limiting, Authentication, Request throttling     │  │
│  └──────────────────────────────────────────────────────────┘  │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

**Alternative Deployment Options:**

| Option | Pros | Cons | When to Use |
|--------|------|------|-------------|
| **Tencent Cloud TCB** (Cloud Base) | Zero server management, seamless WeChat integration, auto-scaling | Vendor lock-in, limited customization | MVP phase, small team, < 5000 DAU |
| **Tencent Cloud CVM** (Virtual Machines) | Full control, flexible, cost-effective at scale | Requires DevOps, manual scaling | Production, team has DevOps capability |
| **Aliyun/AWS** | Mature ecosystem, multi-region | Less WeChat integration, latency | Enterprise with existing cloud contracts |

**Recommendation for this project:**
- **Development/MVP:** Tencent Cloud TCB (fast iteration, no server management)
- **Production:** Tencent Cloud CVM + TencentDB + Redis + CDN (cost-effective, scalable)

## Data Flow Patterns

### 1. User Authentication Flow

```
User Opens Mini Program
        ↓
Check local storage for token
        ↓
    ┌───┴───┐
    │       │
   Yes      No
    │       │
    │       └──> wx.login() → Get code
    │                ↓
    │           Backend /auth/login
    │                ↓
    │           Verify with WeChat API
    │                ↓
    │           Create/Update user
    │                ↓
    │           Return JWT token
    │                ↓
    └───<────────────┘
         Store token
              ↓
         Load user data
              ↓
         Show home page
```

### 2. Product Browsing Flow

```
User enters product page
        ↓
Check cache for product list
        ↓
    ┌───┴───┐
    │       │
   Cache    No Cache
   Hit      Hit
    │       │
    │       └──> Fetch from API
    │                ↓
    │           Query database
    │                ↓
    │           Cache result (5 min)
    │                ↓
    └───<────────────┘
         Display products
              ↓
    User applies filter/search
              ↓
         API request with params
              ↓
         Return filtered results
```

### 3. Order Creation Flow

```
User clicks checkout
        ↓
Validate cart items (stock, price)
        ↓
    ┌───┴───┐
    │       │
  Valid   Invalid
    │       │
    │       └──> Show error, return to cart
    │
    └──> Create order record (status: pending)
              ↓
         Deduct stock (temporary hold)
              ↓
         Return payment parameters
              ↓
         wx.requestPayment()
              ↓
    ┌─────────┴─────────┐
    │                   │
  Success             Cancel/Fail
    │                   │
    │                   └──> Restore stock, cancel order
    │
    └──> WePay callback → Update order status
              ↓
         Confirm stock deduction
              ↓
         Add points (if applicable)
              ↓
         Send notification (optional)
```

## Scalability Considerations

| Concern | At 100 Users | At 1K Users | At 10K Users |
|---------|--------------|-------------|--------------|
| **Database** | Single instance | Read replicas | Sharding + Read replicas |
| **Cache** | Optional | Redis single node | Redis cluster |
| **API Servers** | 1 instance | 2-3 instances + load balancer | Auto-scaling group |
| **Static Assets** | Local storage | CDN | CDN + Edge caching |
| **Payment Queue** | Sync processing | Async queue (basic) | Message queue (RabbitMQ/Kafka) |
| **Search** | LIKE queries | Database indexes | Elasticsearch |

**For this project (1000 concurrent users):**
- Single database instance with read replica
- Redis cluster for sessions, cart, and product cache
- 2-3 API server instances behind load balancer
- CDN for static assets and product images
- Async payment callback processing with retry queue

## Security Considerations

| Layer | Threat | Mitigation |
|-------|--------|------------|
| **Transport** | Man-in-the-middle | HTTPS only, certificate pinning |
| **Authentication** | Token theft | Short-lived JWT, refresh token rotation, secure storage |
| **API** | Rate limiting attacks | Rate limiting per user/IP, API gateway throttling |
| **Payment** | Replay attacks | Nonce + timestamp validation, idempotency keys |
| **Database** | SQL injection | Parameterized queries, ORM usage |
| **Sensitive Data** | Data leakage | Encrypt PII at rest, audit logs, minimal data collection |

## Build Order Recommendations

Based on dependencies, recommended implementation order:

### Phase 1: Foundation (Weeks 1-2)
1. **Database setup** - Core tables (users, products, categories)
2. **Backend skeleton** - Express/NestJS setup, basic API structure
3. **WeChat authentication** - Login flow, token management
4. **Mini Program shell** - Basic page structure, navigation

**Rationale:** Authentication is required for all user operations; product data is needed for browsing.

### Phase 2: Core Shopping (Weeks 3-4)
1. **Product catalog** - Categories, product list/detail APIs
2. **Mini Program product pages** - Browse, search, detail views
3. **Cart functionality** - Add/remove/update, persistent cart
4. **Basic admin panel** - Product/category management

**Rationale:** Shopping loop needs products and cart before orders.

### Phase 3: Order & Payment (Weeks 5-6)
1. **Order creation** - Checkout flow, order records
2. **WeChat Pay integration** - Payment creation, callbacks
3. **Order management** - User order list/detail, status updates
4. **Admin order management** - View, ship, refund orders

**Rationale:** Payment depends on orders; orders depend on products and cart.

### Phase 4: Content & Polish (Weeks 7-8)
1. **Content management** - Banners, notices, videos
2. **Points system** - Earn/redeem points, gift zone
3. **User profile enhancements** - Address management, balance
4. **Admin content pages** - Banner/notice/video management

**Rationale:** Content features are additive; points depend on orders.

### Phase 5: Operations & Optimization (Weeks 9-10)
1. **Admin system management** - Roles, permissions, logs
2. **Finance reports** - Transaction records, statistics
3. **Performance optimization** - Caching, indexing, CDN setup
4. **Testing & deployment** - Load testing, production deployment

**Rationale:** System management and optimization come after core features are stable.

## Anti-Patterns to Avoid

### Anti-Pattern 1: Storing Sensitive Data in Mini Program
**What:** Storing WeChat session keys, payment secrets in frontend code
**Why bad:** Mini Program code can be decompiled; sensitive data exposed
**Instead:** All sensitive operations happen server-side; frontend only holds non-sensitive tokens

### Anti-Pattern 2: Sync Payment Processing
**What:** Processing payment callbacks synchronously in the request thread
**Why bad:** WeChat Pay callback may timeout; payment status not updated
**Instead:** Use async queue for payment callbacks with retry mechanism

### Anti-Pattern 3: Direct Database Queries from Multiple Services
**What:** Each service directly querying the database without abstraction
**Why bad:** Schema changes break multiple services; no data access control
**Instead:** Use repository pattern or ORM with clear service boundaries

### Anti-Pattern 4: Monolithic Frontend State
**What:** One global state object for all pages in Mini Program
**Why bad:** Memory bloat, unnecessary re-renders, hard to debug
**Instead:** Page-level state for local data, global store only for shared data (cart, user)

### Anti-Pattern 5: No Idempotency for Critical Operations
**What:** Payment callbacks, order creation without idempotency keys
**Why bad:** Duplicate payments, duplicate orders from network retries
**Instead:** Use unique idempotency keys (order_no, payment_no) with database constraints

## Sources

- WeChat Mini Program Official Documentation (developers.weixin.qq.com)
- WeChat Pay Integration Guide (pay.weixin.qq.com)
- Tencent Cloud Documentation (cloud.tencent.com)
- Node.js Best Practices for E-commerce (various technical blogs)
- E-commerce Database Design Patterns (database design references)

**Confidence Level:** MEDIUM
- Architecture patterns are based on established WeChat ecosystem practices
- Database schema follows e-commerce conventions
- Some specific WeChat API details may require verification from official documentation during implementation
