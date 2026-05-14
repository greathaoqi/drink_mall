# 测试环境说明

## 环境范围

测试环境独立于现有开发和生产配置，包含：

- 后端 Spring Boot `test` Profile。
- 独立 MySQL 测试库 `drink_mall_test`。
- 独立 Redis 测试实例。
- 管理后台测试构建模式。
- 小程序测试构建模式。
- 独立 Docker Compose 测试栈。

## 端口规划

| 服务 | 测试环境地址 |
| --- | --- |
| 后端 API | `http://localhost:28080` |
| 管理后台 | `http://localhost:23001` |
| MySQL | `localhost:3307` |
| Redis | `localhost:6380` |

## 后端本地启动

先启动 MySQL 和 Redis，或者使用测试 Docker 栈中的基础服务。

```bash
cd drink-mall-api
mvn spring-boot:run -Dspring-boot.run.profiles=test
```

测试 Profile 默认读取：

- 数据库：`localhost:3307/drink_mall_test`
- Redis：`localhost:6380`
- Redis database：`1`

敏感配置通过环境变量覆盖：

```bash
DB_HOST=127.0.0.1
DB_PORT=3307
DB_NAME=drink_mall_test
DB_USERNAME=root
DB_PASSWORD=root123456
REDIS_HOST=127.0.0.1
REDIS_PORT=6380
SA_TOKEN_JWT_SECRET=change-me
WX_MINIAPP_APPID=change-me
WX_MINIAPP_SECRET=change-me
```

## Docker 测试栈

```bash
docker compose -f docker-compose.test.yml up -d --build
docker compose -f docker-compose.test.yml ps
docker compose -f docker-compose.test.yml logs -f backend-test
```

停止：

```bash
docker compose -f docker-compose.test.yml down
```

清空测试数据：

```bash
docker compose -f docker-compose.test.yml down -v
```

## 管理后台

本地测试模式：

```bash
cd drink-mall-admin
npm run dev:test
```

测试构建：

```bash
npm run build:test
```

`drink-mall-admin/.env.test` 默认将 `/api` 代理到 `http://localhost:28080`。

## 小程序

测试构建：

```bash
cd drink-mall-mini
npm run build:test:mp-weixin
```

`drink-mall-mini/.env.test` 默认接口地址为 `http://localhost:28080/api/v1`。如需连接公网测试服务器，请在发布前替换为测试域名，并在微信小程序后台配置合法请求域名。

## 注意事项

- 测试环境不得连接生产库。
- 测试环境的 JWT 密钥、微信 AppID 和 Secret 必须由部署环境变量覆盖。
- 如测试支付、提现、奖励等资金相关流程，必须使用测试账号和测试数据，并保留操作日志。
