CREATE TABLE IF NOT EXISTS content_purchases (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    content_type VARCHAR(32) NOT NULL,
    content_id BIGINT NOT NULL,
    price DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    idempotency_key VARCHAR(128) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_content_purchase_user_content (user_id, content_type, content_id),
    UNIQUE KEY uk_content_purchase_idempotency (idempotency_key),
    INDEX idx_content_purchase_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Mini program paid content purchase records';

INSERT IGNORE INTO sys_config (config_key, config_value, description) VALUES
('customer_service.phone', '', 'Customer service phone, pending operation config'),
('customer_service.wechat', '', 'Customer service WeChat, pending operation config'),
('customer_service.work_time', '', 'Customer service work time, pending operation config'),
('content.default.min_level', 'normal', 'Default content view level'),
('content.default.price', '0.00', 'Default content price');
