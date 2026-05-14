ALTER TABLE users
    ADD COLUMN unionid VARCHAR(64) NULL COMMENT 'WeChat UnionID',
    ADD COLUMN register_invite_code VARCHAR(32) NULL COMMENT 'Invite code used during registration',
    ADD INDEX idx_users_unionid (unionid),
    ADD INDEX idx_users_register_invite_code (register_invite_code);

CREATE TABLE invitation_codes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(32) NOT NULL,
    owner_user_id BIGINT NOT NULL COMMENT 'User whose invite relationship this code binds to',
    source VARCHAR(32) NOT NULL DEFAULT 'admin',
    status VARCHAR(32) NOT NULL DEFAULT 'active',
    used_by_user_id BIGINT NULL,
    used_at DATETIME NULL,
    created_by_admin_id BIGINT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_invitation_codes_code (code),
    INDEX idx_invitation_owner (owner_user_id),
    INDEX idx_invitation_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Admin generated invitation codes';

INSERT IGNORE INTO sys_config (config_key, config_value, description) VALUES
('real_name.enabled', 'true', 'Whether real-name verification is required before withdrawal and investment purchase');
