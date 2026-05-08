-- Phase 4: Content & Engagement Schema

-- Announcements table
CREATE TABLE announcements (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL COMMENT 'Announcement title',
    content TEXT NOT NULL COMMENT 'Full content',
    publisher VARCHAR(64) COMMENT 'Publisher name',
    status TINYINT DEFAULT 1 COMMENT 'Status: 1=active, 0=inactive',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Platform announcements';

-- Videos table
CREATE TABLE videos (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL COMMENT 'Video title',
    cover_url VARCHAR(512) COMMENT 'Cover image URL',
    video_url VARCHAR(512) NOT NULL COMMENT 'Video URL',
    duration INT COMMENT 'Duration in seconds',
    views INT DEFAULT 0 COMMENT 'View count',
    status TINYINT DEFAULT 1 COMMENT 'Status: 1=active, 0=inactive',
    sort_order INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_status_sort (status, sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Video content';

-- Help articles table
CREATE TABLE help_articles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL COMMENT 'Article title',
    content TEXT NOT NULL COMMENT 'Article content',
    category VARCHAR(64) COMMENT 'Category',
    sort_order INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Help center articles';

-- Points logs table
CREATE TABLE points_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT 'User ID',
    change_type VARCHAR(32) NOT NULL COMMENT 'Type: purchase, redeem, adjust',
    points INT NOT NULL COMMENT 'Points change (negative for redeem)',
    before_points INT NOT NULL COMMENT 'Points before',
    after_points INT NOT NULL COMMENT 'Points after',
    order_id BIGINT COMMENT 'Related order ID',
    remark VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Points transaction logs';

-- Seed announcements
INSERT INTO announcements (title, content, publisher, status) VALUES
('平台开业公告', '酒水商城正式上线，欢迎选购优质酒水商品！', '平台管理员', 1),
('配送说明', '全场商品包邮，下单后48小时内发货。', '平台管理员', 1);

-- Seed help articles
INSERT INTO help_articles (title, content, category, sort_order, status) VALUES
('如何下单购买？', '选择商品后加入购物车，在购物车页面点击结算即可下单。', '购物指南', 1, 1),
('如何修改收货地址？', '在个人中心-收货地址管理中添加或修改地址。', '账户相关', 2, 1);