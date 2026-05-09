-- Additional tables for Admin functionality

-- Aftersale (售后) table
CREATE TABLE IF NOT EXISTS aftersale (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL COMMENT 'Order ID',
    user_id BIGINT NOT NULL COMMENT 'User ID',
    type VARCHAR(32) NOT NULL COMMENT 'Type: refund, return, exchange',
    reason VARCHAR(255) NOT NULL COMMENT 'Reason',
    description TEXT COMMENT 'Detailed description',
    images VARCHAR(1024) COMMENT 'Image URLs, comma separated',
    refund_amount DECIMAL(10,2) COMMENT 'Refund amount',
    status VARCHAR(32) DEFAULT 'pending' COMMENT 'Status: pending, approved, rejected',
    admin_remark VARCHAR(255) COMMENT 'Admin remark',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    processed_at DATETIME,
    INDEX idx_order (order_id),
    INDEX idx_user (user_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Aftersale requests';

-- Withdrawal (提现) table
CREATE TABLE IF NOT EXISTS withdrawal (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT 'User ID',
    amount DECIMAL(10,2) NOT NULL COMMENT 'Withdrawal amount',
    bank_name VARCHAR(64) NOT NULL COMMENT 'Bank name',
    bank_account VARCHAR(64) NOT NULL COMMENT 'Bank account',
    account_name VARCHAR(64) NOT NULL COMMENT 'Account holder name',
    status VARCHAR(32) DEFAULT 'pending' COMMENT 'Status: pending, approved, rejected',
    reject_reason VARCHAR(255) COMMENT 'Rejection reason',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    processed_at DATETIME,
    INDEX idx_user (user_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Withdrawal requests';

-- Fix operation_logs table name (rename if needed)
CREATE TABLE IF NOT EXISTS operation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    admin_user_id BIGINT COMMENT 'Admin user ID',
    module VARCHAR(64) NOT NULL COMMENT 'Module: product, order, user, etc',
    action VARCHAR(64) NOT NULL COMMENT 'Action: create, update, delete',
    target_id VARCHAR(64) COMMENT 'Target ID',
    detail TEXT COMMENT 'Operation detail',
    ip VARCHAR(64) COMMENT 'Client IP',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_admin_user (admin_user_id),
    INDEX idx_module (module)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Admin operation logs';

-- Fix system_config table name
CREATE TABLE IF NOT EXISTS sys_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    config_key VARCHAR(64) UNIQUE NOT NULL,
    config_value VARCHAR(512) NOT NULL,
    description VARCHAR(255),
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='System configuration';

-- Insert default configs if not exists
INSERT IGNORE INTO sys_config (config_key, config_value, description) VALUES
('order.timeout.minutes', '30', 'Order payment timeout in minutes'),
('points.rate', '1', 'Points earned per CNY spent'),
('withdraw.min.amount', '10', 'Minimum withdrawal amount'),
('withdraw.max.amount', '5000', 'Maximum withdrawal amount');

-- Add missing columns to user table
ALTER TABLE user ADD COLUMN IF NOT EXISTS balance DECIMAL(10,2) DEFAULT 0 COMMENT 'Account balance';
ALTER TABLE user ADD COLUMN IF NOT EXISTS frozen_balance DECIMAL(10,2) DEFAULT 0 COMMENT 'Frozen balance';
ALTER TABLE user ADD COLUMN IF NOT EXISTS phone VARCHAR(20) COMMENT 'Phone number';
