-- Replace unstable third-party demo images with bundled mini-program assets.

UPDATE users
SET avatar_url = '/static/images/page-profile.png',
    updated_at = NOW()
WHERE avatar_url LIKE 'https://img.yzcdn.cn/vant/%';

UPDATE categories
SET icon_url = CASE
        WHEN name LIKE '%礼包%' OR name LIKE '%礼%' THEN '/static/images/zone-gift.png'
        WHEN name LIKE '%葡萄%' OR name LIKE '%红酒%' THEN '/static/images/zone-retail.png'
        WHEN name LIKE '%啤%' THEN '/static/images/zone-retail.png'
        ELSE '/static/images/zone-main.png'
    END,
    updated_at = NOW()
WHERE icon_url LIKE 'https://img.yzcdn.cn/vant/%';

UPDATE products
SET main_image = CASE
        WHEN zone_type = 'gift' THEN '/static/images/zone-gift.png'
        ELSE '/static/images/product-wine.png'
    END,
    images = CASE
        WHEN zone_type = 'gift' THEN '["/static/images/zone-gift.png"]'
        ELSE '["/static/images/product-wine.png"]'
    END,
    updated_at = NOW()
WHERE main_image LIKE 'https://img.yzcdn.cn/vant/%'
   OR images LIKE '%https://img.yzcdn.cn/vant/%';

UPDATE banners
SET image_url = CASE
        WHEN link_value = '104' OR title LIKE '%积分%' OR title LIKE '%礼包%' THEN '/static/images/zone-gift.png'
        ELSE '/static/images/banner-wine.png'
    END,
    updated_at = NOW()
WHERE image_url LIKE 'https://img.yzcdn.cn/vant/%';
