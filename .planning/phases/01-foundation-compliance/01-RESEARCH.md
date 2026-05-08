# Phase 1: Foundation & Compliance - Research

**Researched:** 2026-05-08
**Domain:** WeChat Mini Program Authentication, Age Verification, Database Foundation
**Confidence:** HIGH

## Summary

This phase establishes the authentication and compliance foundation for the liquor e-commerce platform. The core deliverable is a secure WeChat Mini Program authentication flow with JWT session management, age verification for alcohol sales compliance, guest browsing capability, and customer service entry point.

**Primary recommendation:** Implement WxJava SDK for WeChat login integration with Sa-Token for JWT session management. Use checkbox-based age verification (18+) triggered at zone entry, not app launch, for optimal UX while maintaining legal compliance.

## Architectural Responsibility Map

| Capability | Primary Tier | Secondary Tier | Rationale |
|------------|-------------|----------------|-----------|
| WeChat OAuth flow | API / Backend | — | openid retrieval and user creation must happen server-side to protect session keys |
| JWT session management | API / Backend | Browser / Client | Token issued server-side; client stores in httpOnly cookie |
| Age verification prompt | Browser / Client | API / Backend | UX triggers on zone entry; verification status persisted in user profile (API) |
| Guest browsing | Browser / Client | API / Backend | Frontend controls access; API returns public data without auth for guests |
| Customer service entry | Browser / Client | — | WeChat `button open-type="contact"` component handles entire flow |

## User Constraints (from CONTEXT.md)

### Locked Decisions

#### Age Verification Flow
- **D-01:** Verify once per user, store verification status persistently in user profile
- **D-02:** Verification method: Checkbox confirmation "I am 18+" (self-declaration)
- **D-03:** Trigger timing: Prompt when user enters alcohol-related product zones (not at app launch)
- **D-04:** Re-verification: Prompt again if user uninstalls and reinstalls the mini program (handles device changes naturally)
- **D-05:** Storage location: User profile fields (`age_verified` boolean + `age_verified_at` timestamp)

#### Guest Mode Boundaries
- **D-06:** Guest access scope: Full browsing access (homepage, product list, product detail, announcements, videos) without login
- **D-07:** Login trigger: Prompt login when user attempts restricted actions (add to cart, checkout, access profile)
- **D-08:** Prompt style: Modal popup overlay with login button
- **D-09:** Prompt dismissal: Allow user to close prompt and continue browsing (not forced login)

#### WeChat Session Strategy
- **D-10:** Session duration: 7 days (long-lived for better retention)
- **D-11:** Token storage: httpOnly cookie (better XSS protection, automatic handling)
- **D-12:** Login denial handling: User stays as guest if they deny WeChat authorization (graceful UX)
- **D-13:** WeChat identifier: Store `openid` only (standard for single-app mini programs)

#### Database Tables Scope
- **D-14:** Phase 1 tables: Full foundation schema (users, categories, products skeleton, banners)
- **D-15:** User table fields: Extended profile (openid, nickname, avatar_url, phone, balance, points, age_verified, age_verified_at, status, created_at, updated_at)
- **D-16:** Include `admin_users` table in Phase 1 foundation (prepare for Phase 5)
- **D-17:** Create sample seed data for demo/testing (sample categories, products to validate guest browsing)

### Claude's Discretion

None — all decisions were user-driven with recommendations accepted.

### Deferred Ideas (OUT OF SCOPE)

(None — discussion stayed within phase scope)

## Phase Requirements

| ID | Description | Research Support |
|----|-------------|------------------|
| AUTH-01 | WeChat one-click login | WxJava integration, Sa-Token JWT configuration |
| AUTH-02 | Guest browsing of public content | UniApp conditional rendering, API public endpoints |
| AUTH-03 | Agreement checkboxes before login | UniApp checkbox binding, validation logic |
| AUTH-04 | Age verification for alcohol purchases | Checkbox modal, persistent storage in user profile |
| USER-07 | Customer service entry point | WeChat `button open-type="contact"` component |

## Standard Stack

### Core

| Library | Version | Purpose | Why Standard |
|---------|---------|---------|--------------|
| **WxJava** | 4.6.x | WeChat SDK | Official Java SDK, covers Mini Program login, actively maintained [CITED: github.com/Wechat-Group/WxJava] |
| **Sa-Token** | 1.37.x | Authentication Framework | Simpler than Spring Security for stateless JWT APIs, built-in rate limiting [CITED: sa-token.cc] |
| **MyBatis-Plus** | 3.5.5+ | ORM Framework | Enhanced MyBatis with CRUD operations, pagination [VERIFIED: Maven Central] |
| **Spring Boot** | 3.3.x | Backend Framework | Industry standard for Java enterprise [VERIFIED: spring.io] |
| **UniApp** | 4.x | Mini Program Framework | Vue 3 support, largest Chinese ecosystem [CITED: uniapp.dcloud.net.cn] |
| **Vue 3** | 3.4+ | Frontend Framework | Composition API for better code organization [VERIFIED: vuejs.org] |

### Supporting

| Library | Version | Purpose | When to Use |
|---------|---------|---------|-------------|
| **Hutool** | 5.8.x | Java Utility Library | Date handling, encryption, HTTP client - reduces boilerplate |
| **Lombok** | 1.18.x | Code Generation | Reduces getter/setter boilerplate, required by MyBatis-Plus entities |
| **MySQL Connector/J** | 8.0.x | MySQL Driver | Database connectivity |
| **Jedis/Lettuce** | Latest | Redis Client | Session storage, distributed locks |
| **uView UI** | 3.x | UniApp UI Library | Pre-built e-commerce components (product cards, modals) [CITED: uviewui.com] |

### Alternatives Considered

| Instead of | Could Use | Tradeoff |
|------------|-----------|----------|
| Sa-Token | Spring Security | Sa-Token simpler for JWT/API scenarios; Spring Security over-engineered for this use case |
| UniApp | Native WeChat | UniApp provides multi-platform capability, larger plugin ecosystem; Native locks into WeChat only |
| httpOnly Cookie | localStorage | Cookie better for XSS protection, automatic expiration; localStorage requires manual token management |

**Installation:**

```bash
# Backend (Maven pom.xml dependencies)
# Core authentication
<dependency>
    <groupId>cn.dev33</groupId>
    <artifactId>sa-token-spring-boot3-starter</artifactId>
    <version>1.37.0</version>
</dependency>
<dependency>
    <groupId>cn.dev33</groupId>
    <artifactId>sa-token-jwt</artifactId>
    <version>1.37.0</version>
</dependency>

# WeChat integration
<dependency>
    <groupId>com.github.binarywang</groupId>
    <artifactId>weixin-java-miniapp</artifactId>
    <version>4.6.0</version>
</dependency>

# Database
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
    <version>3.5.5</version>
</dependency>
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <version>8.0.33</version>
</dependency>

# Utilities
<dependency>
    <groupId>cn.hutool</groupId>
    <artifactId>hutool-all</artifactId>
    <version>5.8.25</version>
</dependency>

# Mini Program (via HBuilderX or CLI)
npm install -g @vue/cli
npx degit dcloudio/uni-preset-vue#vite-ts drink-mall-mini
cd drink-mall-mini
npm install pinia @dcloudio/uni-app
npm install uview-plus # uView UI
```

**Version verification:** Per existing STACK.md research, versions confirmed current as of 2026-05-08. Sa-Token 1.37.x and WxJava 4.6.x are stable releases.

## Architecture Patterns

### System Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────┐
│                         Client Layer                                 │
├────────────────────────────┬────────────────────────────────────────┤
│    WeChat Mini Program     │         Admin Dashboard               │
│    (UniApp + Vue 3)        │     (Element Plus + Vue 3)            │
│                            │     (Phase 5)                          │
└─────────────┬──────────────┴────────────────────┬───────────────────┘
              │                                   │
              │ HTTPS/REST API                    │ HTTPS/REST API
              │                                   │
┌─────────────┴───────────────────────────────────┴───────────────────┐
│                      API Gateway Layer                               │
│  - Sa-Token JWT Authentication                                       │
│  - Rate Limiting (Sa-Token built-in)                                 │
│  - Request Routing (Spring Boot)                                     │
└───────────────────────────────┬─────────────────────────────────────┘
                                │
┌───────────────────────────────┴─────────────────────────────────────┐
│                    Application Layer                                 │
├────────────────┬─────────────────┬───────────────────────────────────┤
│  Auth Service  │  User Service   │     Content Service              │
│  - wx.login()  │  - Profile CRUD │     - Banners (public)          │
│  - JWT issue   │  - Age verify   │     - Announcements (public)     │
│  - Token valid │  - Guest check   │     - Categories (public)       │
└────────┬───────┴────────┬────────┴──────────────────────────────────┘
         │                │
┌────────┴────────────────┴────────────────────────────────────────────┐
│                        Data Layer                                     │
├─────────────────────────┬────────────────────────────────────────────┤
│   Primary Database      │      Cache Layer                           │
│   (MySQL 8.0)           │      (Redis 7.2)                           │
│   - users table         │      - Session tokens                     │
│   - admin_users table   │      - Age verification cache             │
│   - categories table    │                                            │
│   - products skeleton   │                                            │
│   - banners table       │                                            │
└─────────────────────────┴────────────────────────────────────────────┘
              │
┌─────────────┴───────────────────────────────────────────────────────┐
│                    External Services                                  │
├─────────────────────────┬────────────────────────────────────────────┤
│   WeChat Auth API       │      WeChat Customer Service              │
│   - code2Session        │      - contact-button                     │
│   - openid retrieval    │      - Built-in WeChat handling           │
└─────────────────────────┴────────────────────────────────────────────┘
```

### Recommended Project Structure

```
drink-mall/
├── drink-mall-mini/           # Mini Program (UniApp + Vue 3)
│   ├── src/
│   │   ├── pages/
│   │   │   ├── index/          # Home page (guest accessible)
│   │   │   ├── product/        # Product list/detail (guest accessible)
│   │   │   ├── login/          # Login page with agreement checkboxes
│   │   │   ├── profile/        # User center (auth required)
│   │   │   └── cart/           # Cart (auth required)
│   │   ├── components/
│   │   │   ├── AgeVerifyModal/ # Age verification modal component
│   │   │   ├── LoginPrompt/    # Guest login prompt modal
│   │   │   └── AgreementBox/   # Agreement checkbox component
│   │   ├── store/
│   │   │   ├── user.ts         # User state (login status, age verified)
│   │   │   └── app.ts          # App global state
│   │   ├── services/
│   │   │   └── auth.ts         # Auth API calls
│   │   └── utils/
│   │       ├── request.ts      # HTTP request wrapper
│   │       └── auth-guard.ts   # Route auth guard
│   └── package.json
│
├── drink-mall-api/             # Backend (Spring Boot 3.3.x)
│   ├── src/main/java/com/drinkmall/
│   │   ├── config/
│   │   │   ├── SaTokenConfig.java      # Sa-Token JWT config
│   │   │   ├── WxMaConfig.java         # WeChat Mini Program config
│   │   │   └── MyBatisPlusConfig.java  # MyBatis-Plus config
│   │   ├── controller/
│   │   │   ├── AuthController.java     # Login/logout endpoints
│   │   │   ├── UserController.java     # User profile endpoints
│   │   │   └── PublicController.java   # Guest-accessible endpoints
│   │   ├── service/
│   │   │   ├── AuthService.java        # WeChat login flow
│   │   │   └── UserService.java        # User CRUD, age verification
│   │   ├── entity/
│   │   │   ├── User.java               # User entity
│   │   │   ├── AdminUser.java          # Admin user entity
│   │   │   ├── Category.java           # Category entity
│   │   │   ├── Product.java            # Product entity
│   │   │   └── Banner.java             # Banner entity
│   │   └── mapper/
│   │       ├── UserMapper.java
│   │       └── ...
│   └── pom.xml
│
└── drink-mall-admin/          # Admin Dashboard (Phase 5)
    └── ...
```

### Pattern 1: WeChat Mini Program Authentication Flow

**What:** Secure login flow using WeChat `wx.login()` with server-side openid retrieval and JWT token issuance.

**When to use:** Every user authentication attempt in the mini program.

**Flow Diagram:**

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│ Mini Program │     │ Backend API  │     │ WeChat API   │     │   Database   │
└──────┬───────┘     └──────┬───────┘     └──────┬───────┘     └──────┬───────┘
       │                    │                    │                    │
       │ 1. wx.login()      │                    │                    │
       ├────────────────────┤                    │                    │
       │                    │                    │                    │
       │ 2. Get code        │                    │                    │
       │<───────────────────┤                    │                    │
       │                    │                    │                    │
       │ 3. POST /auth/login {code, agreements} │                    │
       ├───────────────────>│                    │                    │
       │                    │                    │                    │
       │                    │ 4. code2Session API                    │
       │                    ├───────────────────>│                    │
       │                    │                    │                    │
       │                    │ 5. {openid, session_key}               │
       │                    │<───────────────────┤                    │
       │                    │                    │                    │
       │                    │ 6. Find/Create user by openid           │
       │                    ├───────────────────────────────────────────>│
       │                    │                    │                    │
       │                    │ 7. User entity     │                    │
       │                    │<───────────────────────────────────────────┤
       │                    │                    │                    │
       │                    │ 8. Generate JWT (Sa-Token)               │
       │                    │ (stores in Redis)  │                    │
       │                    │                    │                    │
       │ 9. Set httpOnly cookie with token       │                    │
       │<───────────────────┤                    │                    │
       │                    │                    │                    │
       │ 10. User profile   │                    │                    │
       │<───────────────────┤                    │                    │
       └────────────────────┴────────────────────┴────────────────────┘
```

**Backend Implementation:**

```java
// Source: Sa-Token documentation + WxJava integration patterns [CITED: sa-token.cc]
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final WxMaService wxMaService;
    private final UserService userService;
    
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        // 1. Validate agreement checkboxes were checked
        if (!request.isUserAgreement() || !request.isPrivacyPolicy()) {
            throw new BusinessException("请先同意用户协议和隐私政策");
        }
        
        try {
            // 2. Exchange code for openid via WxJava
            WxMaJscode2SessionResult session = wxMaService.jsCode2SessionInfo(request.getCode());
            String openid = session.getOpenid();
            
            // 3. Find or create user
            User user = userService.findByOpenid(openid);
            if (user == null) {
                user = userService.createUser(openid);
            }
            
            // 4. Generate JWT token via Sa-Token
            StpUtil.login(user.getId()); // Creates session
            String token = StpUtil.getTokenValue();
            
            // 5. Build response (token set automatically in cookie)
            return Result.success(LoginResponse.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .avatarUrl(user.getAvatarUrl())
                .ageVerified(user.getAgeVerified())
                .isNewUser(user.getCreatedAt().equals(user.getUpdatedAt()))
                .build());
                
        } catch (WxErrorException e) {
            throw new BusinessException("微信登录失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/logout")
    public Result<Void> logout() {
        StpUtil.logout();
        return Result.success();
    }
    
    @GetMapping("/check")
    public Result<UserInfo> checkAuth() {
        if (!StpUtil.isLogin()) {
            return Result.success(null); // Guest user
        }
        Long userId = StpUtil.getLoginIdAsLong();
        User user = userService.getById(userId);
        return Result.success(UserInfo.fromEntity(user));
    }
}
```

**Mini Program Implementation:**

```typescript
// Source: UniApp WeChat login patterns [CITED: uniapp.dcloud.net.cn]
// services/auth.ts
export async function wechatLogin(agreements: { userAgreement: boolean; privacyPolicy: boolean }) {
  return new Promise((resolve, reject) => {
    // 1. Get WeChat code
    uni.login({
      provider: 'weixin',
      success: async (loginRes) => {
        try {
          // 2. Send code and agreements to backend
          const response = await request({
            url: '/api/v1/auth/login',
            method: 'POST',
            data: {
              code: loginRes.code,
              userAgreement: agreements.userAgreement,
              privacyPolicy: agreements.privacyPolicy
            }
          });
          
          // 3. Store user info in Pinia store
          const userStore = useUserStore();
          userStore.setUser(response.data);
          
          resolve(response.data);
        } catch (error) {
          reject(error);
        }
      },
      fail: (err) => {
        // User denied authorization - stay as guest
        console.log('WeChat login denied:', err);
        resolve(null); // Graceful degradation
      }
    });
  });
}
```

### Pattern 2: Age Verification Modal

**What:** Triggered verification modal when user enters alcohol-related product zones.

**When to use:** First time user navigates to Main Zone or Retail Zone (alcohol product areas).

**Implementation:**

```vue
<!-- components/AgeVerifyModal/AgeVerifyModal.vue -->
<template>
  <u-popup :show="show" mode="center" :closeOnClickOverlay="false">
    <view class="age-verify-modal">
      <view class="modal-title">年龄确认</view>
      <view class="modal-content">
        <image class="warning-icon" src="/static/icons/18+.png" />
        <text class="warning-text">
          根据国家相关法律法规，未成年人禁止购买酒类商品。
          请确认您已年满18周岁。
        </text>
      </view>
      <view class="checkbox-row">
        <u-checkbox v-model="confirmed" shape="circle">
          我已年满18周岁
        </u-checkbox>
      </view>
      <view class="modal-actions">
        <u-button type="default" @click="handleCancel">取消</u-button>
        <u-button 
          type="primary" 
          :disabled="!confirmed"
          @click="handleConfirm"
        >
          确认进入
        </u-button>
      </view>
    </view>
  </u-popup>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useUserStore } from '@/store/user';
import { verifyAge } from '@/services/user';

const props = defineProps<{
  show: boolean;
}>();

const emit = defineEmits<{
  (e: 'confirm'): void;
  (e: 'cancel'): void;
}>();

const userStore = useUserStore();
const confirmed = ref(false);

async function handleConfirm() {
  if (!confirmed.value) return;
  
  try {
    // Persist verification to backend
    await verifyAge();
    userStore.setAgeVerified(true);
    emit('confirm');
  } catch (error) {
    uni.showToast({ title: '验证失败', icon: 'none' });
  }
}

function handleCancel() {
  emit('cancel');
}
</script>
```

**Zone Entry Guard:**

```typescript
// utils/auth-guard.ts
export function checkAgeVerification(toZone: 'main' | 'retail' | 'gift') {
  const userStore = useUserStore();
  
  // Gift zone is points redemption, still needs age check
  if (['main', 'retail', 'gift'].includes(toZone)) {
    if (!userStore.ageVerified) {
      // Show age verification modal
      showAgeVerifyModal();
      return false;
    }
  }
  return true;
}
```

### Pattern 3: Guest Browsing with Login Prompt

**What:** Allow full catalog browsing without login, prompt only on restricted actions.

**When to use:** Guest user attempts cart/profile/checkout access.

**Implementation:**

```vue
<!-- components/LoginPrompt/LoginPrompt.vue -->
<template>
  <u-popup :show="show" mode="center">
    <view class="login-prompt">
      <text class="prompt-title">请先登录</text>
      <text class="prompt-desc">{{ message }}</text>
      <view class="prompt-actions">
        <u-button type="default" @click="handleClose">稍后再说</u-button>
        <u-button type="primary" @click="handleLogin">立即登录</u-button>
      </view>
    </view>
  </u-popup>
</template>

<script setup lang="ts">
const props = defineProps<{
  show: boolean;
  message?: string;
}>();

const emit = defineEmits<{
  (e: 'close'): void;
  (e: 'login'): void;
}>();

function handleClose() {
  emit('close'); // Allow dismissal, continue browsing
}

function handleLogin() {
  emit('login');
}
</script>
```

**API Public Endpoints:**

```java
@RestController
@RequestMapping("/api/v1/public")
public class PublicController {
    
    private final ProductService productService;
    private final CategoryService categoryService;
    private final BannerService bannerService;
    
    @GetMapping("/banners")
    public Result<List<BannerVO>> getBanners() {
        // No auth required - public endpoint
        return Result.success(bannerService.getActiveBanners());
    }
    
    @GetMapping("/categories")
    public Result<List<CategoryVO>> getCategories() {
        // Public endpoint for category tree
        return Result.success(categoryService.getCategoryTree());
    }
    
    @GetMapping("/products")
    public Result<Page<ProductVO>> getProducts(
        @RequestParam(required = false) Long categoryId,
        @RequestParam(required = false) String keyword,
        @RequestParam(defaultValue = "1") Integer page,
        @RequestParam(defaultValue = "20") Integer size
    ) {
        // Public endpoint - guests can browse full catalog
        return Result.success(productService.getProductPage(categoryId, keyword, page, size));
    }
    
    @GetMapping("/products/{id}")
    public Result<ProductDetailVO> getProductDetail(@PathVariable Long id) {
        // Public endpoint - guests can view product details
        return Result.success(productService.getProductDetail(id));
    }
}
```

### Pattern 4: Customer Service Button

**What:** WeChat built-in customer service entry point using `button open-type="contact"`.

**When to use:** Profile page help section, order detail page for support.

**Implementation:**

```vue
<!-- pages/profile/components/HelpSection.vue -->
<template>
  <view class="help-section">
    <view class="section-title">帮助与客服</view>
    
    <view class="menu-list">
      <!-- Customer Service - Uses WeChat built-in -->
      <button class="menu-item" open-type="contact">
        <view class="menu-left">
          <u-icon name="kefu" size="40" />
          <text>在线客服</text>
        </view>
        <u-icon name="arrow-right" color="#999" />
      </button>
      
      <!-- Help Center - Navigate to help pages -->
      <view class="menu-item" @click="navigateToHelp">
        <view class="menu-left">
          <u-icon name="question-circle" size="40" />
          <text>帮助中心</text>
        </view>
        <u-icon name="arrow-right" color="#999" />
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
function navigateToHelp() {
  uni.navigateTo({ url: '/pages/help/index' });
}
</script>

<style scoped>
button[open-type="contact"] {
  background: transparent;
  border: none;
  padding: 0;
  margin: 0;
  line-height: normal;
}
button[open-type="contact"]::after {
  border: none;
}
</style>
```

**Note:** WeChat handles the entire customer service flow when using `open-type="contact"`. No backend implementation required beyond configuring the customer service account in the WeChat Mini Program backend.

### Anti-Patterns to Avoid

- **Storing session_key on frontend:** The session_key from WeChat must NEVER be stored client-side; it is used only server-side for decrypting user data.
- **Trusting client-side age verification only:** Must persist verification status to user profile database for audit trail.
- **Forced login on app launch:** Violates UX best practices; allow guest browsing, prompt only when needed.
- **Using localStorage for JWT tokens:** Prefer httpOnly cookies for automatic handling and XSS protection.

## Don't Hand-Roll

| Problem | Don't Build | Use Instead | Why |
|---------|-------------|-------------|-----|
| WeChat login flow | Custom HTTP calls to WeChat API | WxJava SDK | Handles signature, error codes, retry logic; official SDK |
| JWT token management | Manual token generation/validation | Sa-Token | Built-in Redis integration, automatic refresh, logout handling |
| Password hashing | Custom crypto implementation | BCrypt (via Spring Security or Sa-Token) | Timing-safe comparison, proper salt handling |
| API request wrapper | Fetch wrapper with auth | uView HTTP or uni.request wrapper | Automatic token refresh, error handling, retry |

**Key insight:** WeChat authentication and JWT session management have complex edge cases (token refresh, session expiry, error handling). Libraries handle these correctly; hand-rolled solutions often have security vulnerabilities.

## Common Pitfalls

### Pitfall 1: WeChat Session Key Exposure

**What goes wrong:** Storing `session_key` on the frontend or in logs, exposing user data decryption capability.

**Why it happens:** Developers unfamiliar with WeChat security model assume all OAuth data is safe to cache.

**How to avoid:**
1. Never return `session_key` to the frontend
2. Store `session_key` in Redis with TTL matching WeChat's expiry
3. Only store `openid` permanently in the database
4. Clear logs of any accidental `session_key` inclusion

**Warning signs:**
- `session_key` appearing in API responses
- Frontend code referencing session key for decryption
- Log files containing session keys

### Pitfall 2: Age Verification Bypass

**What goes wrong:** Users can bypass age verification by clearing local storage, or verification is only stored locally.

**Why it happens:** Developers treat age verification as UI state rather than compliance record.

**How to avoid:**
1. Persist verification status in user profile database (`age_verified`, `age_verified_at`)
2. Check database status on every zone entry, not just local state
3. Log all verification attempts with timestamp and user ID
4. Implement re-verification flow for suspicious patterns

**Warning signs:**
- Age verification only stored in localStorage/Vuex
- No audit trail of when users verified age
- Single checkbox without any backend persistence

### Pitfall 3: Guest Token Misuse

**What goes wrong:** Guests can access authenticated endpoints by not checking auth status properly.

**Why it happens:** API endpoints missing `@SaCheckLogin` annotation or frontend not handling 401 responses.

**How to avoid:**
1. Use Sa-Token's `@SaCheckLogin` annotation on protected endpoints
2. Configure global exception handler for `NotLoginException`
3. Return proper 401 status, not 200 with error message
4. Frontend should check `StpUtil.isLogin()` before API calls

**Warning signs:**
- Protected endpoints accessible without Authorization header
- 401 responses treated as successful by frontend
- No authentication middleware/interceptor

### Pitfall 4: Agreement Checkbox Validation

**What goes wrong:** Users can proceed without checking agreements, or validation is client-side only.

**Why it happens:** Rushed implementation, treating agreements as optional UX element.

**How to avoid:**
1. Backend must validate agreement acceptance in login endpoint
2. Use required checkbox validation on both frontend and backend
3. Store agreement acceptance timestamp in user record for compliance
4. Block login API call if agreements not accepted

**Warning signs:**
- Login succeeds without agreement checkboxes checked
- No backend validation of agreement acceptance
- Agreement acceptance not stored anywhere

## Code Examples

### WeChat Login Configuration (Spring Boot)

```java
// Source: WxJava Spring Boot Starter configuration [CITED: github.com/Wechat-Group/WxJava]
@Configuration
public class WxMaConfig {
    
    @Bean
    public WxMaService wxMaService(WxMaProperties properties) {
        WxMaDefaultConfigImpl config = new WxMaDefaultConfigImpl();
        config.setAppid(properties.getAppid());
        config.setSecret(properties.getSecret());
        
        WxMaService service = new WxMaServiceImpl();
        service.setWxMaConfig(config);
        return service;
    }
}

@ConfigurationProperties(prefix = "wx.miniapp")
@Data
public class WxMaProperties {
    private String appid;
    private String secret;
}
```

### Sa-Token JWT Configuration

```java
// Source: Sa-Token JWT module documentation [CITED: sa-token.cc]
@Configuration
public class SaTokenConfig {
    
    @Bean
    public StpLogic getStpLogicJwt() {
        // Use JWT mode for stateless authentication
        return new StpLogic(SaManager.getConfig().getTokenName()) {
            @Override
            public String createToken(Object loginId) {
                return SaJwtUtil.createToken(loginId);
            }
        };
    }
}

// application.yml
sa-token:
  token-name: Authorization
  timeout: 604800  # 7 days in seconds
  active-timeout: -1  # No activity timeout
  is-concurrent: true
  is-share: true
  token-style: jwt
  is-log: false
```

### User Entity with Age Verification

```java
// Source: MyBatis-Plus entity patterns [VERIFIED: baomidou.com]
@Data
@TableName("users")
public class User {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String openid;
    
    private String nickname;
    
    private String avatarUrl;
    
    private String phone;
    
    private BigDecimal balance;
    
    private Integer points;
    
    /**
     * Age verification for alcohol sales compliance
     * Required by: 未成年人保护法, 酒类流通管理办法
     */
    private Boolean ageVerified;
    
    /**
     * Timestamp of age verification for audit trail
     */
    private LocalDateTime ageVerifiedAt;
    
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
```

### Database Migration (Phase 1 Foundation)

```sql
-- Source: E-commerce schema patterns + project requirements
-- Phase 1: Foundation tables for authentication and guest browsing

-- Users table (extended profile for age verification and points)
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    openid VARCHAR(64) UNIQUE NOT NULL COMMENT 'WeChat OpenID',
    nickname VARCHAR(128),
    avatar_url VARCHAR(512),
    phone VARCHAR(20),
    balance DECIMAL(10,2) DEFAULT 0.00 COMMENT 'Account balance (CNY)',
    points INT DEFAULT 0 COMMENT 'Reward points',
    age_verified BOOLEAN DEFAULT FALSE COMMENT 'Age verification for alcohol sales',
    age_verified_at DATETIME COMMENT 'Timestamp of age verification',
    status TINYINT DEFAULT 1 COMMENT '1: active, 0: disabled',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_openid (openid),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='User accounts';

-- Admin users table (prepare for Phase 5)
CREATE TABLE admin_users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(64) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL COMMENT 'BCrypt hash',
    name VARCHAR(64),
    role VARCHAR(32) NOT NULL DEFAULT 'operator' COMMENT 'admin, operator',
    status TINYINT DEFAULT 1,
    last_login_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Admin user accounts';

-- Categories table (for guest browsing)
CREATE TABLE categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    parent_id BIGINT DEFAULT NULL COMMENT 'Parent category ID (null for root)',
    name VARCHAR(128) NOT NULL,
    icon_url VARCHAR(512),
    sort_order INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (parent_id) REFERENCES categories(id) ON DELETE SET NULL,
    INDEX idx_parent (parent_id),
    INDEX idx_sort (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Product categories';

-- Products table (skeleton for guest browsing validation)
CREATE TABLE products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    subtitle VARCHAR(255),
    description TEXT,
    main_image VARCHAR(512),
    images JSON COMMENT 'Array of image URLs',
    price DECIMAL(10,2) NOT NULL,
    original_price DECIMAL(10,2) COMMENT 'For showing discounts',
    stock INT NOT NULL DEFAULT 0,
    sales INT DEFAULT 0 COMMENT 'Sales count',
    zone_type VARCHAR(32) NOT NULL COMMENT 'main, retail, gift',
    status TINYINT DEFAULT 1 COMMENT '1: on-shelf, 0: off-shelf',
    sort_order INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id),
    INDEX idx_category (category_id),
    INDEX idx_zone (zone_type),
    INDEX idx_status_sort (status, sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Products';

-- Banners table (public content)
CREATE TABLE banners (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(128),
    image_url VARCHAR(512) NOT NULL,
    link_type VARCHAR(32) COMMENT 'product, category, url',
    link_value VARCHAR(255),
    sort_order INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_status_sort (status, sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Home page banners';

-- Sample seed data for guest browsing validation
INSERT INTO categories (id, parent_id, name, icon_url, sort_order) VALUES
(1, NULL, '白酒', '/static/icons/baijiu.png', 1),
(2, NULL, '葡萄酒', '/static/icons/putaojiu.png', 2),
(3, NULL, '啤酒', '/static/icons/pijiu.png', 3);

INSERT INTO categories (parent_id, name, sort_order) VALUES
(1, '茅台', 1),
(1, '五粮液', 2),
(2, '红葡萄酒', 1),
(2, '白葡萄酒', 2);

INSERT INTO products (category_id, name, price, stock, zone_type, status) VALUES
(4, '飞天茅台 53度 500ml', 1499.00, 100, 'main', 1),
(5, '五粮液 52度 500ml', 1099.00, 150, 'main', 1),
(6, '拉菲红葡萄酒 2018', 599.00, 80, 'retail', 1);

INSERT INTO banners (title, image_url, link_type, link_value, sort_order) VALUES
('新品上市', '/static/banners/banner1.jpg', 'category', '1', 1),
('限时特惠', '/static/banners/banner2.jpg', 'product', '1', 2);
```

## State of the Art

| Old Approach | Current Approach | When Changed | Impact |
|--------------|------------------|--------------|--------|
| Session-based auth with server-side storage | JWT stateless tokens | Sa-Token 1.30+ (2022) | Better scalability, simpler load balancing |
| WeChat encrypted data decryption on frontend | Server-side decryption only | WeChat API security update (2021) | Eliminates session_key exposure risk |
| Forced login on app launch | Guest-first with lazy login | UX best practice shift (2020+) | Higher conversion, better user retention |
| localStorage for tokens | httpOnly cookies | Security standard (always) | XSS protection, automatic expiry |

**Deprecated/outdated:**
- WeChat `getUserInfo` API for profile: Deprecated in 2021. Use `getUserProfile` or user-editable profiles.
- Session-based authentication for APIs: Use JWT for stateless APIs.

## Assumptions Log

| # | Claim | Section | Risk if Wrong |
|---|-------|---------|---------------|
| A1 | WeChat Mini Program AppID and Secret available from WeChat backend | Standard Stack | Cannot implement login flow |
| A2 | MySQL 8.0 and Redis 7.2 are available or will be provisioned | Environment Availability | Database dependency blocks development |
| A3 | Business has obtained required alcohol sales licenses (食品经营许可证) | Compliance | Legal liability, WeChat rejection |
| A4 | Customer service account configured in WeChat Mini Program backend | Pattern 4 | Contact button will fail |

**Recommendation:** Verify A1 and A4 in WeChat Mini Program backend before starting Phase 1 implementation. Confirm A3 with business stakeholder.

## Open Questions (RESOLVED)

1. **What is the minimum age verification required for WeChat审核?**
   - **RESOLVED:** Checkbox self-declaration "I am 18+" is standard and accepted for Chinese e-commerce apps selling alcohol. WeChat审核 typically accepts this approach. ID verification is not required at this stage.
   - Fallback prepared: If审核 rejects checkbox approach, add ID verification in future phase.

2. **What customer service account is configured?**
   - **RESOLVED:** The `button open-type="contact"` requires customer service account configuration in WeChat Mini Program backend.
   - Action required: User must verify their customer service account is enabled in WeChat Mini Program admin console before Phase 1 execution.

## Environment Availability

| Dependency | Required By | Available | Version | Fallback |
|------------|------------|-----------|---------|----------|
| Java JDK | Backend | Needs verification | 17+ required | — |
| Node.js | Mini Program build | Needs verification | 18+ required | — |
| MySQL | Database | Needs verification | 8.0+ | — |
| Redis | Session/cache | Needs verification | 7.2+ | In-memory cache (dev only) |
| HBuilderX | Mini Program IDE | Needs verification | Latest | CLI tools |

**Missing dependencies with no fallback:**
- Java 17+ and Node.js 18+ are hard requirements for Spring Boot 3.x and Vite 5.x respectively. These should be verified before starting implementation.

**Recommendation:** Run `java --version` and `node --version` to verify runtime availability.

## Validation Architecture

### Test Framework

| Property | Value |
|----------|-------|
| Framework | Jest (Mini Program) + JUnit 5 (Backend) |
| Config file | `jest.config.js` / `pom.xml` |
| Quick run command | `npm test` / `mvn test -DskipIntegration` |
| Full suite command | `npm run test:all` / `mvn verify` |

### Phase Requirements -> Test Map

| Req ID | Behavior | Test Type | Automated Command | File Exists? |
|--------|----------|-----------|-------------------|-------------|
| AUTH-01 | WeChat login returns JWT token | unit | `mvn test -Dtest=AuthControllerTest` | Wave 0 required |
| AUTH-01 | Login persists user in database | integration | `mvn test -Dtest=AuthServiceIntegrationTest` | Wave 0 required |
| AUTH-02 | Guest can access public endpoints | unit | `mvn test -Dtest=PublicControllerTest` | Wave 0 required |
| AUTH-02 | Guest blocked from protected endpoints | unit | `mvn test -Dtest=AuthGuardTest` | Wave 0 required |
| AUTH-03 | Agreement validation on login | unit | `mvn test -Dtest=AuthControllerTest.testLoginWithoutAgreements` | Wave 0 required |
| AUTH-04 | Age verification persists to database | integration | `mvn test -Dtest=UserServiceTest.testAgeVerification` | Wave 0 required |
| AUTH-04 | Age verification modal appears on zone entry | unit | `npm test -- AgeVerifyModal.spec.ts` | Wave 0 required |
| USER-07 | Customer service button renders | unit | `npm test -- HelpSection.spec.ts` | Wave 0 required |

### Sampling Rate

- **Per task commit:** `mvn test -DskipIntegration` (backend) + `npm test` (mini program, fast subset)
- **Per wave merge:** `mvn verify` (backend full suite)
- **Phase gate:** Full suite green before `/gsd-verify-work`

### Wave 0 Gaps

- [ ] `AuthControllerTest.java` — AUTH-01 login flow
- [ ] `PublicControllerTest.java` — AUTH-02 guest access
- [ ] `UserServiceTest.java` — AUTH-04 age verification persistence
- [ ] `AgeVerifyModal.spec.ts` — AUTH-04 frontend modal behavior
- [ ] `HelpSection.spec.ts` — USER-07 customer service button
- [ ] Backend test framework setup (`pom.xml` JUnit 5 dependencies)
- [ ] Mini Program test framework setup (`jest.config.js`)

## Security Domain

### Applicable ASVS Categories

| ASVS Category | Applies | Standard Control |
|---------------|---------|-----------------|
| V2 Authentication | yes | Sa-Token JWT with 7-day expiry, refresh token rotation |
| V3 Session Management | yes | Redis-backed sessions, httpOnly cookies, secure flag |
| V4 Access Control | yes | Role-based endpoint protection via `@SaCheckLogin` |
| V5 Input Validation | yes | Spring Validation annotations on all request DTOs |
| V6 Cryptography | no | No custom crypto; WeChat handles encryption |
| V8 Error Handling | yes | Global exception handler, no stack traces in responses |

### Known Threat Patterns for WeChat Mini Program Authentication

| Pattern | STRIDE | Standard Mitigation |
|---------|--------|---------------------|
| Token theft via XSS | Tampering, Information Disclosure | httpOnly cookies, Content Security Policy |
| Session fixation | Spoofing | Generate new JWT on login, invalidate old sessions |
| Replay attacks | Repudiation | Include `jti` (JWT ID) claim, implement nonce validation |
| Account enumeration | Information Disclosure | Generic error messages on login failure |
| CSRF on login | Tampering | SameSite cookie attribute, CSRF token for web admin |

## Sources

### Primary (HIGH confidence)

- Sa-Token Official Documentation: https://sa-token.cc/ - JWT configuration, WeChat login integration patterns
- WxJava GitHub Repository: https://github.com/Wechat-Group/WxJava - Mini Program login flow, openid retrieval
- UniApp Official Documentation: https://uniapp.dcloud.net.cn/ - wx.login integration, authentication patterns
- WeChat Mini Program Documentation: https://developers.weixin.qq.com/miniprogram/dev/framework/open-ability/login.html - Login flow specification

### Secondary (MEDIUM confidence)

- MyBatis-Plus Documentation: https://baomidou.com/ - Entity patterns, pagination
- uView UI Documentation: https://uviewui.com/ - Component library for e-commerce

### Tertiary (LOW confidence)

- Community tutorials on WeChat Mini Program authentication (need verification against official docs)

## Metadata

**Confidence breakdown:**
- Standard stack: HIGH - Well-documented, proven patterns in Chinese e-commerce ecosystem
- Architecture: HIGH - Standard WeChat login flow, JWT patterns are mature
- Pitfalls: HIGH - Security considerations well-documented in WeChat and Sa-Token docs
- Age verification: MEDIUM - Legal compliance needs verification; checkbox approach is standard but may require enhancement for审核

**Research date:** 2026-05-08
**Valid until:** 2026-06-08 (30 days - stable technologies, but WeChat API may change)
