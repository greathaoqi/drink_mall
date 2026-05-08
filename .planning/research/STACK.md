# Technology Stack

**Project:** 酒水商城 (Drink Mall) - WeChat Mini Program E-commerce
**Researched:** 2026-05-08

## Recommended Stack

### Mini Program Frontend

| Technology | Version | Purpose | Why |
|------------|---------|---------|-----|
| **UniApp** | 4.x | Mini Program Framework | Cross-platform capability, Vue 3 support, largest Chinese ecosystem, mature tooling with HBuilderX IDE, extensive plugin marketplace, strong community support in China |
| **Vue 3** | 3.4+ | Frontend Framework | Composition API for better code organization, improved TypeScript support, better performance with proxy-based reactivity, native to UniApp |
| **uView UI** | 3.x | UI Component Library | Most comprehensive UniApp component library, 60+ components, excellent documentation in Chinese, actively maintained, designed specifically for e-commerce scenarios |
| **Pinia** | 2.1+ | State Management | Official Vue 3 state management, simpler than Vuex, excellent TypeScript support, devtools integration |

### Backend Framework

| Technology | Version | Purpose | Why |
|------------|---------|---------|-----|
| **Spring Boot** | 3.3.x | Core Backend Framework | Industry standard for Java enterprise applications, mature ecosystem, excellent for e-commerce with transaction management, vast talent pool in China, proven at scale |
| **Spring Cloud Alibaba** | 2022.x | Microservices (Optional) | Nacos for config/registry if scaling beyond single app, deeply integrated with Chinese cloud infrastructure |
| **MyBatis-Plus** | 3.5.5+ | ORM Framework | Enhanced MyBatis with CRUD operations, pagination, code generation - reduces boilerplate significantly, excellent for complex e-commerce queries |
| **Sa-Token** | 1.37+ | Authentication Framework | Simpler than Spring Security for JWT/API scenarios, built-in rate limiting, supports multiple login methods, lighter weight for e-commerce APIs |

### Database & Caching

| Technology | Version | Purpose | Why |
|------------|---------|---------|-----|
| **MySQL** | 8.0+ | Primary Database | Industry standard for e-commerce, excellent transaction support, mature tooling, most DBAs familiar with it, JSON support for flexible product attributes |
| **Redis** | 7.2+ | Cache & Session Store | Shopping cart storage, session management, inventory caching, distributed lock for inventory operations, supports pub/sub for real-time features |
| **Elasticsearch** | 8.x (Optional) | Search Engine | Advanced product search with fuzzy matching, filters, and relevance scoring - defer if MVP scope limited |

### WeChat Integration

| Technology | Version | Purpose | Why |
|------------|---------|---------|-----|
| **WxJava** | 4.6+ | WeChat SDK | Official Java SDK for WeChat, covers Mini Program login, WeChat Pay, message handling, actively maintained, excellent documentation |

### Admin Dashboard

| Technology | Version | Purpose | Why |
|------------|---------|---------|-----|
| **Vue 3** | 3.4+ | Frontend Framework | Consistent with Mini Program stack, shared knowledge, Composition API |
| **Element Plus** | 2.6+ | UI Component Library | Most mature Vue 3 admin UI, comprehensive form/table components, Chinese documentation, theming support |
| **Vue Element Admin** | Template | Admin Template | Production-ready admin scaffold, permission system built-in, best practices included |
| **Vite** | 5.x | Build Tool | Fast development server, optimized production builds, native ESM support |

### Infrastructure & DevOps

| Technology | Version | Purpose | Why |
|------------|---------|---------|-----|
| **Docker** | Latest | Containerization | Consistent deployment, easy scaling, standard for modern deployments |
| **Nginx** | 1.24+ | Reverse Proxy | Static file serving, SSL termination, load balancing |
| **MinIO** | Latest | Object Storage | S3-compatible, self-hosted option for product images, cost-effective vs cloud storage |
| **Jenkins/GitHub Actions** | - | CI/CD | Automated testing and deployment |

### API & Documentation

| Technology | Version | Purpose | Why |
|------------|---------|---------|-----|
| **Knife4j** | 4.5+ | API Documentation | Enhanced Swagger UI for Spring Boot 3, beautiful interface, Chinese documentation, offline export |

## Alternatives Considered

| Category | Recommended | Alternative | Why Not |
|----------|-------------|-------------|---------|
| Mini Program Framework | UniApp | Taro | Taro excellent but smaller ecosystem; UniApp has more plugins and larger Chinese community, better for e-commerce with more pre-built solutions |
| Mini Program Framework | UniApp | Native WeChat | Multi-platform support is valuable; Native locks into WeChat only; UniApp can publish to multiple platforms with same codebase |
| Backend Framework | Spring Boot | NestJS (Node.js) | Java more prevalent in Chinese enterprise, better for complex e-commerce transactions, larger talent pool for maintenance |
| Backend Framework | Spring Boot | Django/FastAPI | Spring ecosystem more mature for e-commerce, better transaction handling, more third-party integrations available |
| Backend Framework | Spring Boot | Go (Gin) | Faster but less ecosystem for e-commerce features; team scaling harder with fewer Go developers in Chinese market |
| ORM | MyBatis-Plus | JPA/Hibernate | MyBatis better for complex e-commerce queries, more control over SQL performance, Chinese teams more familiar with MyBatis |
| Database | MySQL | PostgreSQL | PostgreSQL excellent but MySQL has larger ecosystem in China, more DBAs available, better Oracle Cloud integration for Chinese hosting |
| Admin UI | Element Plus | Ant Design Vue | Both excellent; Element Plus has slightly better Vue 3 integration and more Chinese documentation |
| Authentication | Sa-Token | Spring Security | Sa-Token simpler for stateless JWT APIs, less configuration overhead, built-in features for Chinese auth scenarios |

## Installation

```bash
# Mini Program (UniApp)
# Install via HBuilderX or CLI
npm install -g @vue/cli
npx degit dcloudio/uni-preset-vue#vite-ts drink-mall-mini

cd drink-mall-mini
npm install pinia @dcloudio/uni-app
npm install uview-plus # uView UI for UniApp

# Backend (Spring Boot)
# Use Spring Initializr: https://start.spring.io
# Dependencies: Web, MyBatis, MySQL, Redis, Validation

# Admin Dashboard
npm create vite@latest drink-mall-admin -- --template vue-ts
cd drink-mall-admin
npm install element-plus pinia vue-router axios
npm install -D unplugin-vue-components unplugin-auto-import
```

## Architecture Rationale

### Why This Stack Works for 酒水商城

**1. UniApp + Vue 3 for Mini Program**
- Single codebase can potentially deploy to H5, other mini programs later
- Vue 3 Composition API enables better code organization for complex e-commerce logic
- uView UI provides ready-made e-commerce components (product cards, carts, etc.)
- Mature ecosystem with 1000+ plugins for WeChat-specific features

**2. Spring Boot 3.3 + MyBatis-Plus for Backend**
- Java ecosystem dominates Chinese enterprise e-commerce
- MyBatis-Plus reduces boilerplate while maintaining SQL control
- Sa-Token provides simple JWT auth without Spring Security complexity
- WxJava handles all WeChat integrations (login, payment, messages)

**3. MySQL + Redis for Data Layer**
- MySQL 8 handles ACID transactions critical for orders/payments
- Redis for shopping cart (fast access), inventory cache (performance), distributed locks (prevent overselling)
- Familiar to Chinese development teams

**4. Element Plus for Admin**
- Production-ready admin templates available
- Rich table/form components for product/order management
- Consistent Vue 3 stack with Mini Program

## Version Compatibility Notes

| Component | Minimum Version | Reason |
|-----------|-----------------|--------|
| Java | 17+ | Spring Boot 3.x requires Java 17 minimum |
| Node.js | 18+ | Vite 5.x and Vue 3.4+ recommended |
| MySQL | 8.0+ | JSON support for flexible product attributes, CTEs for category trees |

## Confidence Assessment

| Area | Confidence | Reason |
|------|------------|--------|
| Mini Program Framework | HIGH | UniApp is market leader for Chinese cross-platform mini programs; well-documented |
| Backend Framework | HIGH | Spring Boot is industry standard; MyBatis-Plus widely adopted in China |
| Database | HIGH | MySQL + Redis is proven combination for e-commerce at scale |
| Admin Dashboard | HIGH | Element Plus mature, production-ready admin templates available |
| WeChat Integration | MEDIUM | WxJava well-maintained but need to verify latest WeChat API changes |

## Sources

- UniApp Official: https://uniapp.dcloud.net.cn/ (MEDIUM - official docs, could not verify latest version)
- Taro Official: https://docs.taro.zone/ (MEDIUM - official docs, could not verify latest version)
- Spring Boot Official: https://spring.io/projects/spring-boot (HIGH - official source)
- MyBatis-Plus: https://baomidou.com/ (HIGH - official docs)
- Element Plus: https://element-plus.org/ (HIGH - official docs)
- Sa-Token: https://sa-token.cc/ (HIGH - official docs)
- WxJava: https://github.com/Wechat-Group/WxJava (HIGH - widely used, actively maintained)
- Redis Official: https://redis.io/ (HIGH - official docs)
- Vue.js Official: https://vuejs.org/ (HIGH - official docs)

---

**Note:** Some version numbers are based on training knowledge and could not be verified with current documentation due to network restrictions. Versions should be confirmed with official sources before finalizing.
