-- Phase 5: Operations & Polish Schema

-- Operation logs table
CREATE TABLE operation_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    operator VARCHAR(64) NOT NULL COMMENT 'Admin username',
    operation VARCHAR(64) NOT NULL COMMENT 'Operation type',
    target_type VARCHAR(64) COMMENT 'Target type: order, product, user',
    target_id BIGINT COMMENT 'Target ID',
    detail TEXT COMMENT 'Operation detail JSON',
    ip VARCHAR(64) COMMENT 'Client IP',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_operator (operator),
    INDEX idx_target (target_type, target_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Admin operation logs';

-- System config table
CREATE TABLE system_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    config_key VARCHAR(64) UNIQUE NOT NULL,
    config_value VARCHAR(512) NOT NULL,
    description VARCHAR(255),
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='System configuration';

-- Seed system config
INSERT INTO system_config (config_key, config_value, description) VALUES
('order.timeout.minutes', '30', 'Order payment timeout in minutes'),
('points.rate', '1', 'Points earned per CNY spent'),
('withdraw.min.amount', '10', 'Minimum withdrawal amount'),
('withdraw.max.amount', '5000', 'Maximum withdrawal amount');
