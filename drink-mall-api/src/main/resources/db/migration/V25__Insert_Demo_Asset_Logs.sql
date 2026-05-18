-- Insert varied asset log records for demo user (ID: 10000)
-- This provides realistic transaction history display in the mini program

-- First, clear existing demo asset logs
DELETE FROM asset_logs WHERE user_id = 10000;

-- Balance records (余额变动)
INSERT INTO asset_logs (user_id, asset_type, change_type, change_amount, before_amount, after_amount, business_id, business_type, idempotency_key, remark, created_at) VALUES
(10000, 'balance', 'recharge', 500.00, 0.00, 500.00, NULL, 'system', 'demo_recharge_1', '系统初始充值', '2026-05-01 10:00:00'),
(10000, 'balance', 'reward', 88.00, 500.00, 588.00, 1, 'order_reward', 'demo_reward_1', '推广奖励-订单#10001', '2026-05-02 14:30:00'),
(10000, 'balance', 'payment', -199.00, 588.00, 389.00, 100, 'order', 'demo_payment_1', '余额支付订单 DM202605020001', '2026-05-03 09:15:00'),
(10000, 'balance', 'withdraw', -100.00, 389.00, 289.00, NULL, 'withdrawal', 'demo_withdraw_1', '提现申请', '2026-05-05 16:00:00'),
(10000, 'balance', 'reward', 45.50, 289.00, 334.50, 2, 'order_reward', 'demo_reward_2', '推广奖励-订单#10002', '2026-05-08 11:20:00'),
(10000, 'balance', 'admin_adjust', 50.00, 334.50, 384.50, 1, 'admin', 'demo_adjust_1', '管理员手动调整', '2026-05-10 08:00:00'),
(10000, 'balance', 'transfer_in', 30.00, 384.50, 414.50, 10001, 'transfer', 'demo_transfer_in_1', '收到DF转账', '2026-05-12 15:30:00');

-- Points records (积分变动)
INSERT INTO asset_logs (user_id, asset_type, change_type, change_amount, before_amount, after_amount, business_id, business_type, idempotency_key, remark, created_at) VALUES
(10000, 'points', 'purchase', 50.00, 0.00, 50.00, 100, 'order', 'demo_points_1', '订单赠送积分', '2026-05-03 09:15:00'),
(10000, 'points', 'reward', 20.00, 50.00, 70.00, 1, 'invite_reward', 'demo_points_reward_1', '邀请好友奖励', '2026-05-04 10:00:00'),
(10000, 'points', 'redeem', -30.00, 70.00, 40.00, 101, 'order', 'demo_points_redeem_1', '积分兑换商品', '2026-05-06 13:00:00'),
(10000, 'points', 'purchase', 100.00, 40.00, 140.00, 102, 'order', 'demo_points_2', '订单赠送积分', '2026-05-09 16:30:00'),
(10000, 'points', 'signin', 5.00, 140.00, 145.00, NULL, 'daily', 'demo_signin_1', '每日签到奖励', '2026-05-11 09:00:00'),
(10000, 'points', 'share', 10.00, 145.00, 155.00, NULL, 'social', 'demo_share_1', '分享奖励', '2026-05-13 11:00:00');

-- Wine bean records (酒豆变动)
INSERT INTO asset_logs (user_id, asset_type, change_type, change_amount, before_amount, after_amount, business_id, business_type, idempotency_key, remark, created_at) VALUES
(10000, 'wine_bean', 'exchange', 100.00, 0.00, 100.00, NULL, 'df_exchange', 'demo_wine_bean_1', 'DF兑换酒豆', '2026-05-02 10:00:00'),
(10000, 'wine_bean', 'payment', -50.00, 100.00, 50.00, 103, 'order', 'demo_wine_bean_pay_1', '酒豆支付订单', '2026-05-07 14:00:00'),
(10000, 'wine_bean', 'reward', 30.00, 50.00, 80.00, 3, 'level_reward', 'demo_wine_bean_reward_1', '等级奖励酒豆', '2026-05-10 12:00:00'),
(10000, 'wine_bean', 'exchange', 50.00, 80.00, 130.00, NULL, 'df_exchange', 'demo_wine_bean_2', 'DF兑换酒豆', '2026-05-14 09:30:00');

-- DF records (DF变动)
INSERT INTO asset_logs (user_id, asset_type, change_type, change_amount, before_amount, after_amount, business_id, business_type, idempotency_key, remark, created_at) VALUES
(10000, 'df', 'init', 200.00, 0.00, 200.00, NULL, 'system', 'demo_df_init', '系统初始DF', '2026-05-01 10:00:00'),
(10000, 'df', 'exchange_out', -100.00, 200.00, 100.00, NULL, 'df_exchange', 'demo_df_exchange_1', 'DF兑换酒豆', '2026-05-02 10:00:00'),
(10000, 'df', 'reward', 80.00, 100.00, 180.00, 4, 'team_reward', 'demo_df_reward_1', '团队业绩奖励', '2026-05-08 15:00:00'),
(10000, 'df', 'transfer_out', -30.00, 180.00, 150.00, 10001, 'transfer', 'demo_df_transfer_1', 'DF转账给用户', '2026-05-12 15:30:00'),
(10000, 'df', 'exchange_out', -50.00, 150.00, 100.00, NULL, 'df_exchange', 'demo_df_exchange_2', 'DF兑换酒豆', '2026-05-14 09:30:00');

-- Frozen balance records (冻结余额)
INSERT INTO asset_logs (user_id, asset_type, change_type, change_amount, before_amount, after_amount, business_id, business_type, idempotency_key, remark, created_at) VALUES
(10000, 'frozen_balance', 'freeze', 50.00, 0.00, 50.00, 200, 'withdrawal', 'demo_freeze_1', '提现冻结', '2026-05-05 16:00:00'),
(10000, 'frozen_balance', 'release', -50.00, 50.00, 0.00, 200, 'withdrawal', 'demo_release_1', '提现完成解冻', '2026-05-06 10:00:00');

-- Update user asset balances to match logs
UPDATE users SET
    balance = 414.50,
    points = 155,
    wine_bean_balance = 130.00,
    df_balance = 100.00,
    frozen_balance = 0.00
WHERE id = 10000;