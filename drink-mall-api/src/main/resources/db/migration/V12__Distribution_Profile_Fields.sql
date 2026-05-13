ALTER TABLE users
    ADD COLUMN distribution_level VARCHAR(32) DEFAULT 'county' COMMENT 'Distribution member level code',
    ADD COLUMN team_performance DECIMAL(10,2) DEFAULT 0.00 COMMENT 'Team performance amount',
    ADD COLUMN df_balance DECIMAL(10,2) DEFAULT 0.00 COMMENT 'DF balance shown in member center';

UPDATE users
SET distribution_level = 'county',
    team_performance = 58400.00,
    df_balance = 1500.00,
    balance = 24860.00,
    frozen_balance = 3200.00,
    points = 1280,
    nickname = '李明远',
    phone = '13800138888',
    updated_at = NOW()
WHERE id = 10001;
