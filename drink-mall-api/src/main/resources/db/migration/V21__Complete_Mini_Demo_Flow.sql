-- Complete production demo data for the mini-program walkthrough.
-- Covers demo login, inviter display, direct/indirect team, points redemption,
-- balance purchase, main-product upgrade and investment package upgrade.

INSERT INTO users (
    id, openid, unionid, nickname, avatar_url, phone, balance, frozen_balance, points,
    distribution_level, team_performance, df_balance, wine_bean_balance, option_balance,
    inviter_id, invite_code, register_invite_code, register_source, seed_account,
    real_name_status, main_zone_paid_amount, age_verified, age_verified_at, status,
    created_at, updated_at, version
) VALUES
(10000, 'demo-seed-openid', 'demo-seed-unionid', '演示上级-总部种子', 'https://img.yzcdn.cn/vant/cat.jpeg', '13800138010', 50000.00, 0.00, 9999, 'city', 180000.00, 3000.00, 1000.00, 0.00, NULL, 'DEMO-SEED', NULL, 'admin_seed', TRUE, 'approved', 120000.00, TRUE, NOW(), 1, NOW(), NOW(), 0),
(10001, 'demo-openid', 'demo-unionid', '演示用户', 'https://img.yzcdn.cn/vant/cat.jpeg', '13800138000', 20000.00, 0.00, 5000, 'normal', 66800.00, 1500.00, 800.00, 0.00, 10000, 'DEMO1001', 'DEMO-SEED', 'demo', FALSE, 'approved', 0.00, TRUE, NOW(), 1, NOW(), NOW(), 0),
(10002, 'demo-direct-1-openid', 'demo-direct-1-unionid', '演示直推-主产品客户', 'https://img.yzcdn.cn/vant/cat.jpeg', '13800138002', 3200.00, 0.00, 1200, 'promoter', 16800.00, 260.00, 120.00, 0.00, 10001, 'DEMO-DIRECT-1', 'DEMO1001', 'share_mini', FALSE, 'approved', 399.00, TRUE, NOW(), 1, NOW(), NOW(), 0),
(10003, 'demo-direct-2-openid', 'demo-direct-2-unionid', '演示直推-招商客户', 'https://img.yzcdn.cn/vant/cat.jpeg', '13800138003', 8600.00, 0.00, 1800, 'county', 50000.00, 520.00, 240.00, 0.00, 10001, 'DEMO-DIRECT-2', 'DEMO1001', 'share_product', FALSE, 'approved', 2600.00, TRUE, NOW(), 1, NOW(), NOW(), 0),
(10004, 'demo-indirect-1-openid', 'demo-indirect-1-unionid', '演示间推-复购客户', 'https://img.yzcdn.cn/vant/cat.jpeg', '13800138004', 1500.00, 0.00, 900, 'normal', 5800.00, 80.00, 60.00, 0.00, 10002, 'DEMO-INDIRECT-1', 'DEMO-DIRECT-1', 'manual_code', FALSE, 'approved', 0.00, TRUE, NOW(), 1, NOW(), NOW(), 0),
(10005, 'demo-indirect-2-openid', 'demo-indirect-2-unionid', '演示间推-礼包客户', 'https://img.yzcdn.cn/vant/cat.jpeg', '13800138005', 900.00, 0.00, 760, 'normal', 4200.00, 60.00, 20.00, 0.00, 10003, 'DEMO-INDIRECT-2', 'DEMO-DIRECT-2', 'qr_code', FALSE, 'approved', 0.00, TRUE, NOW(), 1, NOW(), NOW(), 0),
(10006, 'demo-third-level-openid', 'demo-third-level-unionid', '演示三级统计用户', 'https://img.yzcdn.cn/vant/cat.jpeg', '13800138006', 500.00, 0.00, 300, 'normal', 1200.00, 0.00, 0.00, 0.00, 10004, 'DEMO-THIRD-1', 'DEMO-INDIRECT-1', 'share_mini', FALSE, 'approved', 0.00, TRUE, NOW(), 1, NOW(), NOW(), 0)
ON DUPLICATE KEY UPDATE
    unionid = VALUES(unionid),
    nickname = VALUES(nickname),
    avatar_url = VALUES(avatar_url),
    phone = VALUES(phone),
    balance = VALUES(balance),
    frozen_balance = VALUES(frozen_balance),
    points = VALUES(points),
    distribution_level = VALUES(distribution_level),
    team_performance = VALUES(team_performance),
    df_balance = VALUES(df_balance),
    wine_bean_balance = VALUES(wine_bean_balance),
    option_balance = VALUES(option_balance),
    inviter_id = VALUES(inviter_id),
    invite_code = VALUES(invite_code),
    register_invite_code = VALUES(register_invite_code),
    register_source = VALUES(register_source),
    seed_account = VALUES(seed_account),
    real_name_status = VALUES(real_name_status),
    main_zone_paid_amount = VALUES(main_zone_paid_amount),
    age_verified = VALUES(age_verified),
    age_verified_at = VALUES(age_verified_at),
    status = VALUES(status),
    updated_at = NOW();

INSERT INTO addresses (
    id, user_id, name, phone, province, city, district, detail, is_default, created_at, updated_at
) VALUES (
    10001, 10001, '演示收货人', '13800138000', '上海市', '上海市', '浦东新区',
    '世纪大道 100 号 演示大厦 18 层', TRUE, NOW(), NOW()
) ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    phone = VALUES(phone),
    province = VALUES(province),
    city = VALUES(city),
    district = VALUES(district),
    detail = VALUES(detail),
    is_default = TRUE,
    updated_at = NOW();

INSERT INTO products (
    id, category_id, name, subtitle, description, main_image, images, price, original_price,
    stock, sales, zone_type, allowed_payment_methods, gift_points, gift_points_price,
    investment_level_code, wine_bean_payable, status, sort_order
) VALUES
(101, 4, '演示主产品 酱香礼盒 500ml*2', '余额支付演示主推款', '适合演示浏览、加购、下单、余额支付和订单流转。', 'https://img.yzcdn.cn/vant/apple-1.jpg', '["https://img.yzcdn.cn/vant/apple-1.jpg","https://img.yzcdn.cn/vant/apple-2.jpg"]', 299.00, 399.00, 888, 216, 'main', 'online,balance,wine_bean', 99, NULL, NULL, TRUE, 1, 0),
(102, 7, '演示零售 干红葡萄酒 750ml', '多商品加购演示', '用于演示零售专区、购物车多商品和普通订单。', 'https://img.yzcdn.cn/vant/ipad.jpeg', '["https://img.yzcdn.cn/vant/ipad.jpeg"]', 128.00, 168.00, 666, 139, 'retail', 'online,balance', 20, NULL, NULL, FALSE, 1, 1),
(103, 10, '演示零售 精酿啤酒 330ml*6', '低客单价加购演示', '用于演示多商品加购、订单金额计算和售后入口。', 'https://img.yzcdn.cn/vant/sand.jpg', '["https://img.yzcdn.cn/vant/sand.jpg"]', 59.00, 79.00, 999, 521, 'retail', 'online,balance', 10, NULL, NULL, FALSE, 1, 2),
(104, 2, '积分兑换 酒具礼包', '纯积分兑换演示', '礼包专区仅支持积分兑换，不支持组合支付。', 'https://img.yzcdn.cn/vant/gift-card.png', '688.00', '888.00', 100, 38, 'gift', 'points', 0, 688, NULL, FALSE, 1, 3),
(105, 4, '演示招商升级礼包 县级联营商', '购买后演示升级为县级联营商', '招商专区演示商品，已配置实名认证要求、合作确认和投资等级升级。', 'https://img.yzcdn.cn/vant/apple-2.jpg', '["https://img.yzcdn.cn/vant/apple-2.jpg"]', 998.00, 1288.00, 200, 18, 'investment', 'online,balance,wine_bean,offline_corporate', 300, NULL, 'county', TRUE, 1, 4),
(106, 4, '演示主产品 升级推客套装', '购买后累计主产品金额达到推客门槛', '用于演示普通用户购买主产品后升级推客。', 'https://img.yzcdn.cn/vant/apple-1.jpg', '399.00', '499.00', 300, 66, 'main', 'online,balance,wine_bean', 120, NULL, NULL, TRUE, 1, 5)
ON DUPLICATE KEY UPDATE
    category_id = VALUES(category_id),
    name = VALUES(name),
    subtitle = VALUES(subtitle),
    description = VALUES(description),
    main_image = VALUES(main_image),
    images = VALUES(images),
    price = VALUES(price),
    original_price = VALUES(original_price),
    stock = VALUES(stock),
    sales = VALUES(sales),
    zone_type = VALUES(zone_type),
    allowed_payment_methods = VALUES(allowed_payment_methods),
    gift_points = VALUES(gift_points),
    gift_points_price = VALUES(gift_points_price),
    investment_level_code = VALUES(investment_level_code),
    wine_bean_payable = VALUES(wine_bean_payable),
    status = VALUES(status),
    sort_order = VALUES(sort_order),
    updated_at = NOW();

INSERT INTO cart (user_id, product_id, quantity, selected, created_at, updated_at)
VALUES
(10001, 101, 1, TRUE, NOW(), NOW()),
(10001, 102, 2, TRUE, NOW(), NOW())
ON DUPLICATE KEY UPDATE
    quantity = VALUES(quantity),
    selected = TRUE,
    updated_at = NOW();

INSERT INTO asset_accounts (user_id, asset_type, available_amount, frozen_amount, created_at, updated_at)
VALUES
(10001, 'balance', 20000.00, 0.00, NOW(), NOW()),
(10001, 'df', 1500.00, 0.00, NOW(), NOW()),
(10001, 'wine_bean', 800.00, 0.00, NOW(), NOW()),
(10001, 'points', 5000.00, 0.00, NOW(), NOW()),
(10001, 'option', 0.00, 0.00, NOW(), NOW())
ON DUPLICATE KEY UPDATE
    available_amount = VALUES(available_amount),
    frozen_amount = VALUES(frozen_amount),
    updated_at = NOW();

INSERT INTO banners (id, title, image_url, link_type, link_value, sort_order, status, location)
VALUES
(101, '生产演示：余额支付完整闭环', 'https://img.yzcdn.cn/vant/apple-1.jpg', 'product', '101', 0, 1, 'home'),
(102, '生产演示：积分兑换礼包', 'https://img.yzcdn.cn/vant/gift-card.png', 'product', '104', 1, 1, 'home'),
(103, '生产演示：招商升级礼包', 'https://img.yzcdn.cn/vant/apple-2.jpg', 'product', '105', 2, 1, 'home')
ON DUPLICATE KEY UPDATE
    title = VALUES(title),
    image_url = VALUES(image_url),
    link_type = VALUES(link_type),
    link_value = VALUES(link_value),
    sort_order = VALUES(sort_order),
    status = VALUES(status),
    location = VALUES(location);

INSERT INTO announcements (id, title, content, publisher, status, created_at)
VALUES (
    101,
    '演示说明：生产环境支持完整小程序闭环',
    '点击首页“生产演示”可一键登录演示用户。该账号已配置上级、直推、间推、默认地址、余额、DF、酒豆、积分、实名认证、礼包兑换商品、主产品升级商品和招商升级礼包，可演示登录、查看上级、团队、资产、积分兑换、加购、下单、余额支付、主产品升级推客、招商礼包升级联营商等流程。小程序端团队页仅展示直推和间推，三级人员只参与团队总人数统计。',
    '平台管理员',
    1,
    NOW()
) ON DUPLICATE KEY UPDATE
    title = VALUES(title),
    content = VALUES(content),
    publisher = VALUES(publisher),
    status = VALUES(status);

INSERT INTO points_logs (user_id, change_type, points, before_points, after_points, order_id, remark, created_at)
VALUES
(10001, 'adjust', 5000, 0, 5000, NULL, '生产演示账号初始化积分，可用于积分兑换', NOW());

INSERT INTO balance_logs (user_id, change_type, amount, before_balance, after_balance, order_id, remark, created_at)
VALUES
(10001, 'recharge', 20000.00, 0.00, 20000.00, NULL, '生产演示账号初始化余额，可用于余额支付', NOW());
