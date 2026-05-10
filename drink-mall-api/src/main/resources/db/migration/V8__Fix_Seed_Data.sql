-- Fix seed data with proper Chinese characters and real image URLs

-- Clear existing data
DELETE FROM products WHERE id IN (1, 2, 3);
DELETE FROM categories WHERE id IN (1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
DELETE FROM banners WHERE id IN (1, 2);

-- Re-insert categories with Chinese names
INSERT INTO categories (id, parent_id, name, icon_url, sort_order, status) VALUES
(1, NULL, '白酒', 'https://img.yzcdn.cn/vant/custom-icon-fire.png', 1, 1),
(2, NULL, '葡萄酒', 'https://img.yzcdn.cn/vant/custom-icon-gift.png', 2, 1),
(3, NULL, '啤酒', 'https://img.yzcdn.cn/vant/custom-icon-star.png', 3, 1),
(4, 1, '茅台', 'https://img.yzcdn.cn/vant/apple-1.jpg', 1, 1),
(5, 1, '五粮液', 'https://img.yzcdn.cn/vant/apple-2.jpg', 2, 1),
(6, 1, '剑南春', 'https://img.yzcdn.cn/vant/apple-3.jpg', 3, 1),
(7, 2, '红酒', 'https://img.yzcdn.cn/vant/ipad.jpeg', 1, 1),
(8, 2, '白葡萄酒', 'https://img.yzcdn.cn/vant/ipad.jpeg', 2, 1),
(9, 3, '进口啤酒', 'https://img.yzcdn.cn/vant/tree.jpeg', 1, 1),
(10, 3, '国产啤酒', 'https://img.yzcdn.cn/vant/tree.jpeg', 2, 1);

-- Re-insert products with Chinese names and real image URLs
INSERT INTO products (id, category_id, name, subtitle, description, main_image, images, price, original_price, stock, sales, zone_type, status, sort_order) VALUES
(1, 4, '茅台飞天53度500ml', '贵州茅台酒 酱香型白酒', '茅台飞天53度500ml，贵州茅台酒，经典酱香型白酒，国酒茅台，收藏送礼首选。', 'https://img.yzcdn.cn/vant/apple-1.jpg', '["https://img.yzcdn.cn/vant/apple-1.jpg","https://img.yzcdn.cn/vant/apple-2.jpg"]', 1499.00, 1599.00, 100, 50, 'main', 1, 1),
(2, 7, '拉菲传奇波尔多干红葡萄酒', '法国拉菲红酒', '拉菲传奇波尔多干红葡萄酒750ml，法国进口红酒，口感醇厚，适合商务宴请。', 'https://img.yzcdn.cn/vant/ipad.jpeg', '["https://img.yzcdn.cn/vant/ipad.jpeg"]', 298.00, 358.00, 200, 30, 'main', 1, 2),
(3, 9, '德国原装进口黑啤酒', '德国黑啤', '德国原装进口黑啤酒500ml，口感浓郁，麦香纯正，啤酒爱好者的首选。', 'https://img.yzcdn.cn/vant/tree.jpeg', '["https://img.yzcdn.cn/vant/tree.jpeg"]', 15.00, 20.00, 500, 100, 'main', 1, 3),
(4, 5, '五粮液52度500ml', '宜宾五粮液', '五粮液52度500ml，四川宜宾五粮液，浓香型白酒代表，口感绵柔醇厚。', 'https://img.yzcdn.cn/vant/apple-2.jpg', '["https://img.yzcdn.cn/vant/apple-2.jpg"]', 899.00, 999.00, 80, 25, 'main', 1, 4),
(5, 6, '剑南春52度500ml', '四川剑南春', '剑南春52度500ml，四川绵竹剑南春，浓香型白酒，历史悠久，品质优良。', 'https://img.yzcdn.cn/vant/apple-3.jpg', '["https://img.yzcdn.cn/vant/apple-3.jpg"]', 398.00, 450.00, 120, 40, 'main', 1, 5),
(6, 10, '青岛啤酒经典500ml', '国产啤酒', '青岛啤酒经典500ml，中国知名啤酒品牌，清爽解渴，聚会必备。', 'https://img.yzcdn.cn/vant/sand.jpg', '["https://img.yzcdn.cn/vant/sand.jpg"]', 6.00, 8.00, 1000, 500, 'main', 1, 6);

-- Re-insert banners with Chinese titles and real image URLs
INSERT INTO banners (id, title, image_url, link_type, link_value, sort_order, status) VALUES
(1, '新品上市 茅台飞天限量发售', 'https://img.yzcdn.cn/vant/apple-1.jpg', 'product', '1', 1, 1),
(2, '红酒专区 法国进口红酒', 'https://img.yzcdn.cn/vant/ipad.jpeg', 'category', '2', 2, 1),
(3, '啤酒特惠 夏日清凉', 'https://img.yzcdn.cn/vant/tree.jpeg', 'category', '3', 3, 1);