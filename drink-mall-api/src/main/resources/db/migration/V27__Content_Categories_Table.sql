-- Phase 6: Content Categories Table
-- Create content_categories table for flat category management per D-CAT-01 through D-CAT-05

CREATE TABLE IF NOT EXISTS content_categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(64) NOT NULL COMMENT 'Category name',
    sort_order INT DEFAULT 0 COMMENT 'Display sort order',
    status INT DEFAULT 1 COMMENT 'Status: 1=active, 0=inactive',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_category_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Content categories for videos and help articles';

-- Insert default categories
INSERT INTO content_categories (name, sort_order) VALUES
('品酒知识', 1),
('酿造工艺', 2),
('行业动态', 3),
('其他', 99);

-- Add category_id to videos table
ALTER TABLE videos ADD COLUMN category_id BIGINT COMMENT 'Category ID reference';

-- Add category_id to help_articles table
ALTER TABLE help_articles ADD COLUMN category_id BIGINT COMMENT 'Category ID reference';