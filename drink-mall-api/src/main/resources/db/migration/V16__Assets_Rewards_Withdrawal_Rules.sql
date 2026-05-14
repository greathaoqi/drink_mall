ALTER TABLE withdrawals
    ADD COLUMN fee_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT 'Withdrawal fee',
    ADD COLUMN actual_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT 'Offline paid amount after fee',
    ADD COLUMN account_name VARCHAR(64) NULL COMMENT 'Account holder name',
    ADD COLUMN offline_transfer_no VARCHAR(128) NULL COMMENT 'Offline payment reference',
    ADD COLUMN reviewer_admin_id BIGINT NULL COMMENT 'Reviewer admin user id',
    ADD COLUMN reject_reason VARCHAR(255) NULL COMMENT 'Reject reason',
    ADD COLUMN processed_at DATETIME NULL COMMENT 'Review processed time';

UPDATE withdrawals
SET account_name = COALESCE(account_name, real_name),
    fee_amount = COALESCE(fee_amount, 0.00),
    actual_amount = CASE WHEN actual_amount = 0.00 THEN amount ELSE actual_amount END;

CREATE TABLE IF NOT EXISTS level_change_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    before_level VARCHAR(32),
    after_level VARCHAR(32) NOT NULL,
    reason VARCHAR(64) NOT NULL,
    business_id BIGINT,
    operator_admin_id BIGINT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_level_change_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='User level change audit logs';

INSERT IGNORE INTO sys_config (config_key, config_value, description) VALUES
('withdraw.min_amount', '10.00', 'Minimum withdrawal amount'),
('withdraw.fee_ratio', '0.00', 'Withdrawal fee ratio'),
('reward.main.direct_first_ratio', '0.20', 'Main zone direct first-order commission ratio'),
('reward.main.direct_repurchase_ratio', '0.10', 'Main zone direct repurchase commission ratio, configurable'),
('reward.investment.advertising_ratio', '0.05', 'Investment zone self advertising reward ratio'),
('reward.investment.ratio.county', '0.20', 'County merchant investment reward ratio, pending business confirmation'),
('reward.investment.ratio.city', '0.20', 'City merchant investment reward ratio, pending business confirmation'),
('reward.investment.ratio.province', '0.20', 'Province merchant investment reward ratio, pending business confirmation'),
('reward.investment.cap_amount.county', '500.00', 'County merchant investment reward cap, pending business confirmation'),
('reward.investment.cap_amount.city', '500.00', 'City merchant investment reward cap, pending business confirmation'),
('reward.investment.cap_amount.province', '500.00', 'Province merchant investment reward cap, pending business confirmation'),
('reward.support_merchant.ratio', '0.10', 'Support merchant reward ratio, pending business confirmation'),
('discount.main.repurchase.promoter', '0.80', 'Promoter main-zone repurchase discount'),
('discount.main.repurchase.county', '待业务确认', 'County merchant main-zone repurchase discount'),
('discount.main.repurchase.city', '待业务确认', 'City merchant main-zone repurchase discount'),
('discount.main.repurchase.province', '待业务确认', 'Province merchant main-zone repurchase discount'),
('level.county.main_performance_threshold', '待业务确认', 'County merchant main-zone performance upgrade threshold'),
('level.city.main_performance_threshold', '待业务确认', 'City merchant main-zone performance upgrade threshold'),
('level.province.main_performance_threshold', '待业务确认', 'Province merchant main-zone performance upgrade threshold'),
('asset.option.visible_in_mini', 'false', 'Whether option asset is visible in mini program');
