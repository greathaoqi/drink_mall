ALTER TABLE products
    ADD COLUMN wine_bean_payable BOOLEAN DEFAULT FALSE COMMENT 'Whether wine bean payment is enabled';

ALTER TABLE orders
    ADD COLUMN points_amount INT DEFAULT 0 COMMENT 'Points deducted for pure points orders',
    ADD COLUMN zone_type VARCHAR(32) NULL COMMENT 'Primary order zone type',
    ADD COLUMN offline_transfer_no VARCHAR(128) NULL COMMENT 'Offline corporate transfer reference',
    ADD COLUMN offline_confirmed_by BIGINT NULL COMMENT 'Admin user id who confirmed offline transfer',
    ADD COLUMN offline_confirmed_at DATETIME NULL COMMENT 'Offline transfer confirmation time',
    ADD INDEX idx_orders_zone_type (zone_type);

ALTER TABLE order_items
    ADD COLUMN zone_type VARCHAR(32) NULL COMMENT 'Product zone snapshot',
    ADD COLUMN gift_points INT DEFAULT 0 COMMENT 'Gift points snapshot',
    ADD COLUMN points_amount INT DEFAULT 0 COMMENT 'Points amount snapshot for gift exchange';

UPDATE products SET allowed_payment_methods = 'online,balance,wine_bean' WHERE zone_type = 'main';
UPDATE products SET allowed_payment_methods = 'online,balance,wine_bean,offline_corporate' WHERE zone_type = 'investment';
UPDATE products SET allowed_payment_methods = 'online,balance' WHERE zone_type = 'retail';
UPDATE products SET allowed_payment_methods = 'points' WHERE zone_type = 'gift';

INSERT IGNORE INTO sys_config (config_key, config_value, description) VALUES
('investment.package.diff_upgrade.enabled', '待业务确认', '招商礼包补差价升级是否启用，规则待业务确认'),
('investment.package.diff_upgrade.rule', '待业务确认', '招商礼包补差价升级规则说明，待业务确认'),
('investment.package.county.amount', '待业务确认', '县级联营商招商礼包金额待业务确认'),
('investment.package.city.amount', '待业务确认', '市级联营商招商礼包金额待业务确认'),
('investment.package.province.amount', '待业务确认', '省级联营商招商礼包金额待业务确认');
