-- Demo-ready data for a complete mini-program walkthrough.
-- The demo account can browse, add to cart, submit an order, and pay with balance.

INSERT INTO users (
    id, openid, nickname, avatar_url, phone, balance, frozen_balance, points,
    age_verified, age_verified_at, status, created_at, updated_at
) VALUES (
    10001, 'demo-openid', '演示用户', 'https://img.yzcdn.cn/vant/cat.jpeg', '13800138000',
    9999.00, 0.00, 1888, TRUE, NOW(), 1, NOW(), NOW()
) ON DUPLICATE KEY UPDATE
    nickname = VALUES(nickname),
    avatar_url = VALUES(avatar_url),
    phone = VALUES(phone),
    balance = VALUES(balance),
    frozen_balance = VALUES(frozen_balance),
    points = VALUES(points),
    age_verified = TRUE,
    age_verified_at = NOW(),
    status = 1,
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
    stock, sales, zone_type, status, sort_order
) VALUES
(101, 4, '演示款 酱香礼盒 500ml*2', '余额支付演示主推款', '适合演示完整下单流程的高库存商品，包含商品详情、库存、销量和余额支付链路。', 'https://img.yzcdn.cn/vant/apple-1.jpg', '["https://img.yzcdn.cn/vant/apple-1.jpg","https://img.yzcdn.cn/vant/apple-2.jpg"]', 299.00, 399.00, 888, 216, 'main', 1, 0),
(102, 7, '演示款 干红葡萄酒 750ml', '聚餐配餐精选', '口感柔和，适合作为商品列表、详情页和购物车演示数据。', 'https://img.yzcdn.cn/vant/ipad.jpeg', '["https://img.yzcdn.cn/vant/ipad.jpeg"]', 128.00, 168.00, 666, 139, 'retail', 1, 1),
(103, 10, '演示款 精酿啤酒 330ml*6', '夏日清爽组合', '低客单价商品，方便演示多商品加购和订单金额计算。', 'https://img.yzcdn.cn/vant/sand.jpg', '["https://img.yzcdn.cn/vant/sand.jpg"]', 59.00, 79.00, 999, 521, 'retail', 1, 2),
(104, 2, '积分兑换 酒具礼包', '纯积分兑换演示', '礼包专区演示商品，价格字段在演示版中作为积分价格使用。', 'https://img.yzcdn.cn/vant/gift-card.png', '["https://img.yzcdn.cn/vant/gift-card.png"]', 688.00, 888.00, 100, 38, 'gift', 1, 3)
ON DUPLICATE KEY UPDATE
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
    status = VALUES(status),
    sort_order = VALUES(sort_order);

INSERT INTO banners (id, title, image_url, link_type, link_value, sort_order, status, location)
VALUES
(101, '演示闭环商品：余额支付可直接体验', 'https://img.yzcdn.cn/vant/apple-1.jpg', 'product', '101', 0, 1, 'home'),
(102, '零售专区：多商品加购演示', 'https://img.yzcdn.cn/vant/ipad.jpeg', 'category', '2', 1, 1, 'home')
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
    '演示说明：本环境支持余额支付完整闭环',
    '使用演示登录后，默认账户余额 9999 元，可直接完成浏览、加购、下单、余额支付、后台发货、确认收货流程。',
    '平台管理员',
    1,
    NOW()
) ON DUPLICATE KEY UPDATE
    title = VALUES(title),
    content = VALUES(content),
    publisher = VALUES(publisher),
    status = VALUES(status);
