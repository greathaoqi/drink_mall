-- Phase 1 distribution backend core.
-- Keeps ratios, thresholds and withdrawal rules in sys_config instead of scattered code defaults.

ALTER TABLE users
    ADD COLUMN inviter_id BIGINT NULL COMMENT 'Direct inviter user id',
    ADD COLUMN invite_code VARCHAR(32) NULL COMMENT 'Unique invite code',
    ADD COLUMN register_source VARCHAR(32) DEFAULT 'unknown' COMMENT 'share_mini, share_product, manual_code, qr_code, admin_seed',
    ADD COLUMN seed_account BOOLEAN DEFAULT FALSE COMMENT 'Admin-created seed accounts can have no inviter',
    ADD COLUMN real_name_status VARCHAR(32) DEFAULT 'not_submitted' COMMENT 'not_submitted, pending, approved, rejected',
    ADD COLUMN main_zone_paid_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT 'Main product cumulative paid amount',
    ADD COLUMN wine_bean_balance DECIMAL(10,2) DEFAULT 0.00 COMMENT 'Wine bean balance',
    ADD COLUMN option_balance DECIMAL(10,2) DEFAULT 0.00 COMMENT 'Option asset reserved balance',
    ADD UNIQUE KEY uk_users_invite_code (invite_code),
    ADD INDEX idx_users_inviter (inviter_id),
    ADD INDEX idx_users_real_name_status (real_name_status);

UPDATE users
SET invite_code = CONCAT('U', id)
WHERE invite_code IS NULL;

ALTER TABLE products
    ADD COLUMN allowed_payment_methods VARCHAR(128) DEFAULT 'online,balance' COMMENT 'Comma-separated payment methods',
    ADD COLUMN gift_points INT DEFAULT 0 COMMENT 'Points granted after purchase',
    ADD COLUMN gift_points_price INT NULL COMMENT 'Pure points price for gift zone',
    ADD COLUMN investment_level_code VARCHAR(32) NULL COMMENT 'Level granted by investment package';

UPDATE products SET allowed_payment_methods = 'online,balance' WHERE zone_type IN ('main', 'investment', 'retail');
UPDATE products SET allowed_payment_methods = 'points' WHERE zone_type = 'gift';

CREATE TABLE real_name_verifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    real_name VARCHAR(64) NOT NULL,
    id_card_no VARCHAR(64) NOT NULL,
    front_image_url VARCHAR(512) NOT NULL,
    back_image_url VARCHAR(512) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'pending',
    reject_reason VARCHAR(255),
    reviewer_admin_id BIGINT,
    reviewed_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_real_name_id_card_approved (id_card_no, status),
    INDEX idx_real_name_user (user_id),
    INDEX idx_real_name_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Real-name verification submissions';

CREATE TABLE asset_accounts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    asset_type VARCHAR(32) NOT NULL,
    available_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    frozen_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_asset_account_user_type (user_id, asset_type),
    INDEX idx_asset_account_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Independent asset accounts';

CREATE TABLE asset_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    asset_type VARCHAR(32) NOT NULL,
    change_type VARCHAR(32) NOT NULL,
    change_amount DECIMAL(18,2) NOT NULL,
    before_amount DECIMAL(18,2) NOT NULL,
    after_amount DECIMAL(18,2) NOT NULL,
    business_id BIGINT,
    business_type VARCHAR(64),
    idempotency_key VARCHAR(128),
    remark VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_asset_log_idempotency (idempotency_key),
    INDEX idx_asset_log_user_type (user_id, asset_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Independent asset ledgers';

CREATE TABLE reward_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    source_user_id BIGINT NOT NULL,
    beneficiary_user_id BIGINT NOT NULL,
    order_id BIGINT NOT NULL,
    order_no VARCHAR(64) NOT NULL,
    reward_type VARCHAR(32) NOT NULL,
    base_amount DECIMAL(18,2) NOT NULL,
    ratio DECIMAL(10,4) NOT NULL,
    amount DECIMAL(18,2) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'frozen',
    frozen_until DATETIME,
    idempotency_key VARCHAR(128) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_reward_idempotency (idempotency_key),
    INDEX idx_reward_beneficiary (beneficiary_user_id),
    INDEX idx_reward_order (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Reward freeze, unfreeze and rollback records';

INSERT IGNORE INTO sys_config (config_key, config_value, description) VALUES
('real_name.enabled', 'true', 'Whether real-name verification is required before withdrawal and investment purchase'),
('level.promoter.main_paid_threshold', '396.00', 'Promoter upgrade threshold, pending business confirmation if changed'),
('reward.retail.direct_ratio', '0.10', 'Retail zone direct inviter reward ratio'),
('reward.freeze_days', '7', 'Reward aftersale freeze period in days'),
('withdraw.fee_ratio', '0.00', 'Withdrawal fee ratio, pending business confirmation'),
('asset.df.withdrawable', 'false', 'DF cannot be withdrawn'),
('asset.wine_bean.withdrawable', 'false', 'Wine bean cannot be withdrawn'),
('asset.points.withdrawable', 'false', 'Points cannot be withdrawn'),
('asset.option.withdrawable', 'false', 'Option asset reserved for manual adjustment only');
