-- Add missing fields to existing tables

-- Add location and updated_at to banners
ALTER TABLE banners ADD COLUMN location VARCHAR(32) DEFAULT 'home' COMMENT 'Banner location';
ALTER TABLE banners ADD COLUMN updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- Add updated_at to announcements
ALTER TABLE announcements ADD COLUMN updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- Add updated_at to videos
ALTER TABLE videos ADD COLUMN updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- Add updated_at to help_articles
ALTER TABLE help_articles ADD COLUMN updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- Add updated_at to categories
ALTER TABLE categories ADD COLUMN updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- Add logistics fields to orders
ALTER TABLE orders ADD COLUMN logistics_company VARCHAR(64) COMMENT 'Logistics company name';
ALTER TABLE orders ADD COLUMN logistics_no VARCHAR(64) COMMENT 'Logistics tracking number';
