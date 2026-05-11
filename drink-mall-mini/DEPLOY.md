# 小程序部署指南

## 一、环境准备

### 1. 下载工具
- **微信开发者工具**: https://developers.weixin.qq.com/miniprogram/dev/devtools/download.html
- **HBuilderX** (可选): https://www.dcloud.io/hbuilderx.html

### 2. 注册小程序
1. 访问 https://mp.weixin.qq.com 注册小程序账号
2. 获取小程序 AppID: 开发管理 → 开发设置 → AppID

---

## 二、配置小程序

### 1. 修改 AppID
编辑 `drink-mall-mini/src/manifest.json`:
```json
{
  "mp-weixin": {
    "appid": "你的小程序AppID"
  }
}
```

### 2. 配置服务器域名
在微信公众平台: 开发管理 → 开发设置 → 服务器域名

**request 合法域名**:
```
https://你的域名
```

**注意**: 开发阶段可以在开发者工具中勾选「不校验合法域名」

### 3. 修改 API 地址
编辑 `drink-mall-mini/src/utils/request.ts`:
```typescript
const BASE_URL = 'http://hy.ajiu.lol:18080/api/v1'
```

---

## 三、本地开发

### 1. 安装依赖
```bash
cd drink-mall-mini
npm install
```

### 2. 运行开发模式
```bash
npm run dev:mp-weixin
```

编译后的代码在 `dist/dev/mp-weixin` 目录

### 3. 微信开发者工具调试
1. 打开微信开发者工具
2. 导入项目，选择 `dist/dev/mp-weixin` 目录
3. 填写 AppID
4. 开始调试

---

## 四、生产部署

### 1. 编译生产版本
```bash
npm run build:mp-weixin
```

编译后的代码在 `dist/build/mp-weixin` 目录

### 2. 上传代码
方式一: 微信开发者工具上传
1. 打开微信开发者工具
2. 导入 `dist/build/mp-weixin` 目录
3. 点击「上传」按钮
4. 填写版本号和备注

方式二: CLI 上传
```bash
# 安装 miniprogram-ci
npm install miniprogram-ci -g

# 上传
miniprogram-ci upload \
  --pp ./dist/build/mp-weixin \
  --pkp ./private.key \
  --appid 你的AppID \
  -r 1 \
  --uv 1.0.0 \
  -d "首次发布"
```

### 3. 提交审核
1. 登录微信公众平台
2. 版本管理 → 开发版本 → 提交审核
3. 填写审核信息

### 4. 发布上线
审核通过后，点击「发布」即可上线

---

## 五、后端部署

### 方案一: Docker 部署 (推荐)
```bash
# 构建镜像
docker build -t drink-mall-api ./drink-mall-api

# 运行容器
docker run -d \
  --name drink-mall-api \
  -p 18080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_HOST=数据库地址 \
  -e DB_PORT=3306 \
  -e DB_NAME=drink_mall \
  -e DB_USERNAME=root \
  -e DB_PASSWORD=密码 \
  -e REDIS_HOST=Redis地址 \
  -e REDIS_PORT=6379 \
  drink-mall-api
```

### 方案二: 腾讯云部署
1. 购买云服务器 CVM
2. 安装 JDK 17、MySQL、Redis
3. 上传 JAR 包运行
```bash
java -jar drink-mall-api.jar --spring.profiles.active=prod
```

### 方案三: 使用云服务
- **数据库**: 腾讯云 MySQL (推荐，与小程序同域)
- **缓存**: 腾讯云 Redis
- **对象存储**: 腾讯云 COS (存图片)

---

## 六、HTTPS 配置

小程序要求后端必须是 HTTPS

### 1. 申请 SSL 证书
- 腾讯云 SSL 证书 (免费)
- Let's Encrypt (免费)

### 2. Nginx 配置
```nginx
server {
    listen 443 ssl;
    server_name 你的域名;

    ssl_certificate /path/to/cert.pem;
    ssl_certificate_key /path/to/key.pem;

    location /api/ {
        proxy_pass http://127.0.0.1:18080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

---

## 七、配置微信支付

### 1. 开通微信支付
- 登录微信支付商户平台
- 获取商户号 (mchId)
- 获取 API 密钥 (apiV3Key)

### 2. 配置后端
编辑 `application-prod.yml`:
```yaml
wx:
  miniapp:
    appid: 小程序AppID
    secret: 小程序Secret
  pay:
    mch-id: 商户号
    api-v3-key: API密钥
    cert-path: /path/to/apiclient_cert.p12
```

---

## 八、常见问题

### Q: 真机调试白屏
A: 检查域名是否配置正确，是否 HTTPS

### Q: 登录失败
A: 检查 AppID 和 Secret 是否正确

### Q: 支付失败
A: 检查商户号配置，确保证书路径正确

### Q: 图片不显示
A: 图片域名需要加入 downloadFile 合法域名

---

## 九、项目结构

```
drink-mall-mini/
├── src/
│   ├── pages/          # 页面
│   │   ├── index/      # 首页
│   │   ├── category/   # 分类
│   │   ├── cart/       # 购物车
│   │   ├── user/       # 我的
│   │   └── product/    # 商品详情
│   ├── services/       # API 服务
│   ├── store/          # 状态管理
│   ├── utils/          # 工具函数
│   └── manifest.json   # 配置文件
└── package.json
```

---

## 十、技术支持

- UniApp 文档: https://uniapp.dcloud.net.cn
- 微信小程序文档: https://developers.weixin.qq.com/miniprogram/dev/framework/
- WxJava 文档: https://github.com/Wechat-Group/WxJava
