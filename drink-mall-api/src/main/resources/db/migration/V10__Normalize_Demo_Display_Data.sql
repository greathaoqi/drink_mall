-- Normalize existing demo databases after V8/V9 so the mini program shows coherent liquor data.
-- Frontend renders CSS placeholders, but keeping URLs blank prevents remote image timeouts in any remaining views.

UPDATE users
SET avatar_url = ''
WHERE openid = 'demo-openid';

UPDATE categories SET icon_url = '' WHERE id BETWEEN 1 AND 10;

UPDATE products
SET main_image = '',
    images = '[]',
    status = 1
WHERE id IN (1, 2, 3, 4, 5, 6, 101, 102, 103, 104);

UPDATE products SET name = '茅台飞天53度 500ml', subtitle = '酱香型白酒', price = 1499.00, original_price = 1599.00, stock = 100, sales = 50, zone_type = 'main', sort_order = 96 WHERE id = 1;
UPDATE products SET name = '拉菲传奇波尔多干红', subtitle = '法国进口红酒', price = 298.00, original_price = 358.00, stock = 200, sales = 30, zone_type = 'main', sort_order = 95 WHERE id = 2;
UPDATE products SET name = '德国原装进口黑啤 500ml', subtitle = '麦香浓郁', price = 15.00, original_price = 20.00, stock = 500, sales = 100, zone_type = 'retail', sort_order = 94 WHERE id = 3;
UPDATE products SET name = '五粮液52度 500ml', subtitle = '浓香型白酒', price = 899.00, original_price = 999.00, stock = 80, sales = 25, zone_type = 'main', sort_order = 93 WHERE id = 4;
UPDATE products SET name = '剑南春52度 500ml', subtitle = '宴请送礼精选', price = 398.00, original_price = 450.00, stock = 120, sales = 40, zone_type = 'main', sort_order = 92 WHERE id = 5;
UPDATE products SET name = '青岛经典啤酒 500ml', subtitle = '国产啤酒', price = 6.00, original_price = 8.00, stock = 1000, sales = 500, zone_type = 'retail', sort_order = 91 WHERE id = 6;

UPDATE products SET sort_order = 120, main_image = '', images = '[]' WHERE id = 101;
UPDATE products SET sort_order = 119, main_image = '', images = '[]' WHERE id = 102;
UPDATE products SET sort_order = 118, main_image = '', images = '[]' WHERE id = 103;
UPDATE products SET sort_order = 117, main_image = '', images = '[]' WHERE id = 104;

UPDATE banners
SET image_url = ''
WHERE id IN (1, 2, 3, 101, 102);

UPDATE banners SET title = '余额支付演示：酱香礼盒现货', link_type = 'product', link_value = '101', sort_order = 120, status = 1 WHERE id = 101;
UPDATE banners SET title = '零售专区：红酒与精酿组合', link_type = 'category', link_value = '2', sort_order = 119, status = 1 WHERE id = 102;
