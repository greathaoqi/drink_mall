-- Drink Mall Foundation Schema
-- Phase 1: User Authentication, Age Verification, Guest Browsing

-- Users table (per D-15: openid, nickname, avatar_url, phone, balance, points, age_verified, age_verified_at, status)
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    openid VARCHAR(64) UNIQUE NOT NULL COMMENT 'WeChat OpenID',
    nickname VARCHAR(128) COMMENT 'User nickname',
    avatar_url VARCHAR(512) COMMENT 'Avatar URL',
    phone VARCHAR(20) COMMENT 'Phone number',
    balance DECIMAL(10,2) DEFAULT 0.00 COMMENT 'Account balance in CNY',
    points INT DEFAULT 0 COMMENT 'Reward points for gift zone redemption',
    age_verified BOOLEAN DEFAULT FALSE COMMENT 'Age verification status for alcohol sales compliance',
    age_verified_at DATETIME COMMENT 'Timestamp of age verification for audit trail',
    status TINYINT DEFAULT 1 COMMENT 'User status: 1=active, 0=disabled',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_openid (openid),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='User accounts';

-- Admin users table (per D-16)
CREATE TABLE admin_users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(64) UNIQUE NOT NULL COMMENT 'Admin username',
    password_hash VARCHAR(255) NOT NULL COMMENT 'Password hash',
    name VARCHAR(64) COMMENT 'Admin name',
    role VARCHAR(32) NOT NULL DEFAULT 'operator' COMMENT 'Role: admin, operator',
    status TINYINT DEFAULT 1 COMMENT 'Status: 1=active, 0=disabled',
    last_login_at DATETIME COMMENT 'Last login timestamp',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Admin users';

-- Categories table (multi-level hierarchy)
CREATE TABLE categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    parent_id BIGINT DEFAULT NULL COMMENT 'Parent category ID',
    name VARCHAR(128) NOT NULL COMMENT 'Category name',
    icon_url VARCHAR(512) COMMENT 'Category icon URL',
    sort_order INT DEFAULT 0 COMMENT 'Sort order',
    status TINYINT DEFAULT 1 COMMENT 'Status: 1=active, 0=disabled',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (parent_id) REFERENCES categories(id) ON DELETE SET NULL,
    INDEX idx_parent (parent_id),
    INDEX idx_sort (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Product categories';

-- Products table (guest browsing support)
CREATE TABLE products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category_id BIGINT NOT NULL COMMENT 'Category ID',
    name VARCHAR(255) NOT NULL COMMENT 'Product name',
    subtitle VARCHAR(255) COMMENT 'Product subtitle',
    description TEXT COMMENT 'Product description',
    main_image VARCHAR(512) COMMENT 'Main product image URL',
    images JSON COMMENT 'Product images JSON array',
    price DECIMAL(10,2) NOT NULL COMMENT 'Current price',
    original_price DECIMAL(10,2) COMMENT 'Original price',
    stock INT NOT NULL DEFAULT 0 COMMENT 'Stock quantity',
    sales INT DEFAULT 0 COMMENT 'Sales count',
    zone_type VARCHAR(32) NOT NULL COMMENT 'Zone type: main, retail, gift',
    status TINYINT DEFAULT 1 COMMENT 'Status: 1=active, 0=disabled',
    sort_order INT DEFAULT 0 COMMENT 'Sort order',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id),
    INDEX idx_category (category_id),
    INDEX idx_zone (zone_type),
    INDEX idx_status_sort (status, sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Products';

-- Banners table (homepage display)
CREATE TABLE banners (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(128) COMMENT 'Banner title',
    image_url VARCHAR(512) NOT NULL COMMENT 'Banner image URL',
    link_type VARCHAR(32) COMMENT 'Link type: product, category, url',
    link_value VARCHAR(255) COMMENT 'Link value',
    sort_order INT DEFAULT 0 COMMENT 'Sort order',
    status TINYINT DEFAULT 1 COMMENT 'Status: 1=active, 0=disabled',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_status_sort (status, sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Homepage banners';

-- Seed data: Categories (per D-17)
INSERT INTO categories (id, parent_id, name, icon_url, sort_order, status) VALUES
(1, NULL, '白酒', '/static/icons/baijiu.png', 1, 1),
(2, NULL, '葡萄酒', '/static/icons/wine.png', 2, 1),
(3, NULL, '啤酒', '/static/icons/beer.png', 3, 1),
(4, 1, '茅台', '/static/icons/maotai.png', 1, 1),
(5, 1, '五粮液', '/static/icons/wuliangye.png', 2, 1),
(6, 1, '剑南春', '/static/icons/jiannanchun.png', 3, 1),
(7, 2, '红酒', '/static/icons/redwine.png', 1, 1),
(8, 2, '白葡萄酒', '/static/icons/whitewine.png', 2, 1),
(9, 3, '进口啤酒', '/static/icons/imported.png', 1, 1),
(10, 3, '国产啤酒', '/static/icons/domestic.png', 2, 1);

-- Seed data: Products for guest browsing (per D-17)
INSERT INTO products (category_id, name, subtitle, description, main_image, price, stock, sales, zone_type, status, sort_order) VALUES
(4, '茅台飞天53度', '贵州茅台酒', '茅台飞天53度500ml，经典酱香型白酒', '/static/products/maotai.png', 1499.00, 100, 50, 'main', 1, 1),
(7, '法国拉菲红酒', '拉菲传奇波尔多', '拉菲传奇波尔多干红葡萄酒750ml', '/static/products/lafite.png', 298.00, 200, 30, 'main', 1, 2),
(9, '德国黑啤', '德国原装进口黑啤酒', '德国原装进口黑啤酒500ml', '/static/products/german-beer.png', 15.00, 500, 100, 'main', 1, 3);

-- Seed data: Banners for homepage (per D-17)
INSERT INTO banners (title, image_url, link_type, link_value, sort_order, status) VALUES
('新品上市', '/static/banners/banner1.png', 'category', '1', 1, 1),
('限时优惠', '/static/banners/banner2.png', 'url', '/pages/promotion/index', 2, 1);

-- Seed data: Default admin user (per D-16)
INSERT INTO admin_users (username, password_hash, name, role, status) VALUES
('admin', '0192023a7bbd73250516f069df18b500', '系统管理员', 'admin', 1);