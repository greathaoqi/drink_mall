# 酒水商城 (Drink Mall)

微信小程序电商平台 - 酒水零售解决方案

## 项目概述

**核心价值:** 用户可以方便地在微信小程序中浏览和购买酒水商品，享受流畅的购物体验，平台可以高效管理商品和订单。

## 技术栈

| 组件 | 技术 |
|------|------|
| 小程序 | UniApp 4.x + Vue 3 |
| 后端 | Spring Boot 3.3.x + MyBatis-Plus |
| 数据库 | MySQL 8.0 + Redis 7.2 |
| 管理后台 | Element Plus + Vue 3 |
| 认证 | Sa-Token (JWT) |
| 支付 | 微信支付 (WxJava SDK) |

## 项目结构

```
drink_mall/
├── drink-mall-api/          # Spring Boot 后端
│   ├── src/main/java/com/drinkmall/
│   │   ├── controller/      # REST API
│   │   │   └── admin/       # 管理后台 API
│   │   ├── service/         # 业务逻辑
│   │   ├── entity/          # 数据实体
│   │   ├── mapper/          # MyBatis-Plus Mapper
│   │   └── config/          # 配置类
│   └── src/main/resources/
│   │   ├── application.yml  # 配置文件
│   │   └── db/migration/    # 数据库迁移脚本
│   └── Dockerfile
├── drink-mall-mini/         # UniApp 小程序
│   ├── src/
│   │   ├── pages/           # 页面组件
│   │   ├── components/      # 公共组件
│   │   ├── services/        # API 服务
│   │   └── store/           # Pinia 状态管理
│   └── pages.json           # 页面路由配置
├── drink-mall-admin/        # Vue 3 管理后台
│   ├── src/
│   │   ├── views/           # 管理页面
│   │   ├── layouts/         # 布局组件
│   │   ├── router/          # 路由配置
│   │   └── utils/           # 工具函数
│   └── Dockerfile
├── docker-compose.yml       # Docker 编排
└── .planning/               # 项目规划文档
```

---

## 快速开始

### 环境要求

| 工具 | 版本 |
|------|------|
| Java JDK | 17+ |
| Node.js | 18+ |
| MySQL | 8.0 |
| Redis | 7.2 |
| Docker | 24+ (可选) |
| 微信开发者工具 | 最新版 |

### 1. 克隆项目

```bash
git clone https://github.com/greathaoqi/drink_mall.git
cd drink_mall
```

---

## 开发调试流程

### 后端开发 (drink-mall-api)

#### 本地运行

```bash
# 1. 启动 MySQL 和 Redis
# MySQL: 创建数据库 drink_mall
mysql -u root -p
CREATE DATABASE drink_mall CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# Redis: 默认端口 6379
redis-server

# 2. 配置 application-dev.yml
cd drink-mall-api/src/main/resources
# 编辑配置文件，填入微信小程序 AppID/AppSecret

# 3. 运行 Flyway 迁移（自动执行）
# 或手动导入数据库脚本
mysql -u root -p drink_mall < src/main/resources/db/migration/*.sql

# 4. 启动 Spring Boot
cd drink-mall-api
./mvnw spring-boot:run
# 或使用 IDE 运行 DrinkMallApplication.java

# 后端服务地址: http://localhost:8080
```

#### 配置说明

编辑 `application-dev.yml`:

```yaml
# 微信小程序配置
wx:
  ma:
    app-id: your_app_id
    app-secret: your_app_secret

# 微信支付配置
wx:
  pay:
    app-id: your_app_id
    mch-id: your_mch_id
    mch-key: your_mch_key
    notify-url: https://your-domain.com/api/v1/pay/callback

# Sa-Token 配置
sa-token:
  token-name: Authorization
  timeout: 86400
```

#### API 测试

```bash
# 测试公开 API
curl http://localhost:8080/api/v1/public/banners
curl http://localhost:8080/api/v1/public/categories
curl http://localhost:8080/api/v1/public/products

# 测试登录 (需要微信 code)
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"code":"wx_code_from_mini_program"}'
```

---

### 小程序开发 (drink-mall-mini)

#### 本地运行

```bash
# 1. 安装依赖
cd drink-mall-mini
npm install

# 2. 配置 API 地址
# 编辑 src/utils/request.ts 中的 BASE_URL
const BASE_URL = 'http://localhost:8080/api/v1'

# 3. 运行开发服务器
npm run dev:mp-weixin

# 4. 打开微信开发者工具
# 导入项目: drink-mall-mini/dist/dev/mp-weixin
# 在微信开发者工具中预览和调试
```

#### 微信开发者工具配置

1. 下载安装 [微信开发者工具](https://developers.weixin.qq.com/miniprogram/dev/devtools/download.html)
2. 登录微信账号
3. 导入项目目录: `drink-mall-mini/dist/dev/mp-weixin`
4. 配置 AppID (在 project.config.json 中)
5. 点击"编译"预览小程序

#### 调试技巧

```javascript
// 在页面中打印日志
console.log('API Response:', res.data)

// 微信开发者工具中查看:
// - Console: 查看日志
// - Network: 查看 API 请求
// - AppData: 查看页面数据
```

---

### 管理后台开发 (drink-mall-admin)

#### 本地运行

```bash
# 1. 安装依赖
cd drink-mall-admin
npm install

# 2. 配置后端 API 代理
# vite.config.ts 已配置代理到 localhost:8080

# 3. 运行开发服务器
npm run dev

# 管理后台地址: http://localhost:3001
# 默认账号: admin / admin123
```

#### 开发调试

```bash
# 构建生产版本
npm run build

# 预览构建结果
npm run preview
```

---

## 部署流程

### Docker 部署 (推荐)

#### 一键部署

```bash
# 确保已安装 Docker 和 Docker Compose
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f backend
```

#### 服务地址

| 服务 | 地址 |
|------|------|
| 后端 API | http://localhost:8080 |
| 管理后台 | http://localhost:3001 |
| MySQL | localhost:3306 |
| Redis | localhost:6379 |

#### 数据库初始化

Docker 会自动执行 `db/migration/` 下的 SQL 脚本。

#### 停止服务

```bash
docker-compose down

# 删除数据卷（清除数据）
docker-compose down -v
```

---

### 生产环境部署

#### 1. 后端部署

```bash
# 构建 JAR 包
cd drink-mall-api
./mvnw clean package -DskipTests

# 上传到服务器
scp target/drink-mall-api-1.0.jar server:/opt/drink-mall/

# 在服务器运行
java -jar drink-mall-api-1.0.jar --spring.profiles.active=prod
```

#### 2. 管理后台部署

```bash
# 构建
cd drink-mall-admin
npm run build

# 上传 dist 目录到服务器
# 使用 nginx 配置静态文件服务
```

#### 3. Nginx 配置示例

```nginx
# /etc/nginx/sites-available/drink-mall.conf
server {
    listen 80;
    server_name your-domain.com;

    # 管理后台
    location / {
        root /opt/drink-mall/admin/dist;
        try_files $uri $uri/ /index.html;
    }

    # 后端 API
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    # 微信支付回调 (HTTPS)
    location /api/v1/pay/callback {
        proxy_pass http://localhost:8080;
    }
}
```

#### 4. 微信小程序发布

```bash
# 1. 构建生产版本
cd drink-mall-mini
npm run build:mp-weixin

# 2. 微信开发者工具上传代码
# - 打开项目: dist/build/mp-weixin
# - 点击"上传"
# - 填写版本号和备注

# 3. 微信公众平台提交审核
# - 登录 mp.weixin.qq.com
# - 版本管理 -> 开发版本 -> 提交审核
```

---

## 功能清单

### 用户端 (小程序)

| 功能 | 状态 |
|------|------|
| 微信一键登录 | ✅ |
| 年龄验证 (18+) | ✅ |
| 商品浏览 (分类/专区) | ✅ |
| 商品搜索/筛选/排序 | ✅ |
| 购物车管理 | ✅ |
| 地址管理 (最多10个) | ✅ |
| 下单/支付 | ✅ |
| 订单列表/详情 | ✅ |
| 确认收货/取消订单 | ✅ |
| 申请售后 | ✅ |
| 积分获取/兑换 | ✅ |
| 首页轮播/公告 | ✅ |
| 视频播放 | ✅ |
| 帮助中心 | ✅ |
| 客服入口 | ✅ |

### 管理端 (后台)

| 功能 | 状态 |
|------|------|
| 商品管理 (CRUD/上下架) | ✅ |
| 分类管理 (最多3级) | ✅ |
| 库存管理/流水 | ✅ |
| 订单管理 (发货/取消/改价) | ✅ |
| 售后处理 | ✅ |
| 用户列表/导出 | ✅ |
| 内容管理 (Banner/公告/视频/帮助) | ✅ |
| 提现审核 | ✅ |
| 财务流水 | ✅ |
| 系统配置 | ✅ |
| 管理员账号管理 | ✅ |
| 操作日志 | ✅ |

---

## 数据库表结构

| 表名 | 说明 |
|------|------|
| user | 用户表 |
| admin_user | 管理员表 |
| category | 商品分类 |
| product | 商品表 |
| cart | 购物车 |
| address | 收货地址 |
| order | 订单表 |
| order_item | 订单明细 |
| banner | 轮播图 |
| announcement | 公告 |
| video | 视频 |
| help_article | 帮助文章 |
| points_log | 积分流水 |
| withdrawal | 提现申请 |
| aftersale | 售后申请 |
| stock_log | 库存流水 |
| operation_log | 操作日志 |
| sys_config | 系统配置 |

---

## 安全注意事项

1. **年龄验证**: 购买酒水前必须完成年龄验证 (18+)
2. **支付回调**: 微信支付回调需验证签名
3. **Admin API**: 已添加 `@SaCheckRole("admin")` 权限控制
4. **库存管理**: 使用乐观锁防止超卖
5. **敏感配置**: 生产环境禁止使用硬编码密码

---

## 常见问题

### Q: 微信登录失败?
A: 检查 AppID/AppSecret 配置，确保小程序已绑定微信开放平台

### Q: 支付回调不触发?
A: 确保 notify-url 是 HTTPS 地址，且服务器可访问

### Q: 小程序无法请求后端?
A: 在微信公众平台配置服务器域名白名单

### Q: Docker 启动失败?
A: 检查端口占用: `netstat -tlnp | grep -E '3306|6379|8080|3001'`

---

## 更新日志

| 版本 | 日期 | 说明 |
|------|------|------|
| v1.0 | 2026-05-09 | MVP 完成，包含全部68个需求 |
| v1.1 | TBD | 待添加：订单超时自动取消、消息通知 |

---

## 联系方式

- GitHub: https://github.com/greathaoqi/drink_mall
- 问题反馈: GitHub Issues

---

*最后更新: 2026-05-09*