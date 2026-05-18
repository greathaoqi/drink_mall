-- Fix missing/pending system configurations that cause 500 errors
-- Replace '待业务确认' placeholder values with actual working defaults

-- Discount configs for main-zone repurchase (county/city/province merchants)
UPDATE sys_config SET config_value = '0.90' WHERE config_key = 'discount.main.repurchase.county';
UPDATE sys_config SET config_value = '0.85' WHERE config_key = 'discount.main.repurchase.city';
UPDATE sys_config SET config_value = '0.80' WHERE config_key = 'discount.main.repurchase.province';

-- Level upgrade thresholds (main zone performance)
UPDATE sys_config SET config_value = '10000.00' WHERE config_key = 'level.county.main_performance_threshold';
UPDATE sys_config SET config_value = '50000.00' WHERE config_key = 'level.city.main_performance_threshold';
UPDATE sys_config SET config_value = '200000.00' WHERE config_key = 'level.province.main_performance_threshold';

-- Level display names and order (ensure these exist)
INSERT IGNORE INTO sys_config (config_key, config_value, description) VALUES
('level.county.name', '县级代理商', 'County merchant level name'),
('level.city.name', '市级代理商', 'City merchant level name'),
('level.province.name', '省级代理商', 'Province merchant level name'),
('level.normal.name', '普通会员', 'Normal member level name'),
('level.promoter.name', '推广员', 'Promoter level name'),
('level.county.order', '2', 'County merchant level order'),
('level.city.order', '3', 'City merchant level order'),
('level.province.order', '4', 'Province merchant level order'),
('level.normal.order', '0', 'Normal member level order'),
('level.promoter.order', '1', 'Promoter level order');

-- Level benefits descriptions
INSERT IGNORE INTO sys_config (config_key, config_value, description) VALUES
('level.county.benefits', '主区复购9折优惠|推广奖励', 'County merchant benefits'),
('level.city.benefits', '主区复购85折优惠|推广奖励|团队业绩奖励', 'City merchant benefits'),
('level.province.benefits', '主区复购8折优惠|推广奖励|团队业绩奖励|省级代理特权', 'Province merchant benefits');

-- Content access config defaults
INSERT IGNORE INTO sys_config (config_key, config_value, description) VALUES
('content.default.min_level', 'normal', 'Default minimum level for content access'),
('content.default.payment_methods', 'balance', 'Default payment methods for paid content'),
('content.purchase.allow_below_level', 'false', 'Allow purchase content below minimum level');

-- Asset type visibility - add reward type handling (reward is not a real asset type, but frontend may send it)
-- We'll handle this in code instead of database