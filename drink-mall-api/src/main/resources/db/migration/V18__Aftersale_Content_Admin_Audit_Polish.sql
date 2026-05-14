-- Phase one backend polish: aftersale offline handling, content pay fields, and audit-friendly indexes.

ALTER TABLE aftersale
    ADD COLUMN offline_process_result VARCHAR(1000) NULL COMMENT 'Offline handling result recorded by admin',
    ADD COLUMN offline_processed_at DATETIME NULL COMMENT 'Offline handling record time',
    ADD COLUMN offline_processor_admin_id BIGINT NULL COMMENT 'Admin user ID who recorded offline handling';

ALTER TABLE videos
    ADD COLUMN watch_level VARCHAR(32) DEFAULT 'normal' COMMENT 'Minimum watch level code',
    ADD COLUMN paid BOOLEAN DEFAULT FALSE COMMENT 'Whether content is paid',
    ADD COLUMN price DECIMAL(10,2) DEFAULT 0.00 COMMENT 'Paid content price',
    ADD COLUMN payment_methods VARCHAR(255) NULL COMMENT 'Allowed payment method codes',
    ADD COLUMN likes INT DEFAULT 0 COMMENT 'Like count',
    ADD INDEX idx_watch_paid (watch_level, paid);

ALTER TABLE help_articles
    ADD COLUMN watch_level VARCHAR(32) DEFAULT 'normal' COMMENT 'Minimum watch level code',
    ADD COLUMN paid BOOLEAN DEFAULT FALSE COMMENT 'Whether content is paid',
    ADD COLUMN price DECIMAL(10,2) DEFAULT 0.00 COMMENT 'Paid content price',
    ADD COLUMN payment_methods VARCHAR(255) NULL COMMENT 'Allowed payment method codes',
    ADD COLUMN likes INT DEFAULT 0 COMMENT 'Like count',
    ADD INDEX idx_watch_paid (watch_level, paid);
