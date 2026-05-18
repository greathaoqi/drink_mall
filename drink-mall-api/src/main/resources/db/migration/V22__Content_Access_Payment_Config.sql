ALTER TABLE content_purchases
    ADD COLUMN payment_method VARCHAR(32) NULL COMMENT 'Configured payment method used for paid content';

INSERT IGNORE INTO sys_config (config_key, config_value, description) VALUES
('content.purchase.allow_below_level', 'false', 'Whether paid purchase can unlock content when user level is below watch_level'),
('content.default.payment_methods', 'balance', 'Default content payment methods when content payment_methods is blank');
