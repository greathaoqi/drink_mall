-- Phase 6: Content Payment Order Status Fields
-- Add payment tracking fields to content_purchases table

ALTER TABLE content_purchases
    ADD COLUMN status VARCHAR(32) NOT NULL DEFAULT 'pending' COMMENT 'Order status: pending, paid, cancelled, expired',
    ADD COLUMN order_no VARCHAR(64) COMMENT 'CP-prefixed order number (CPYYYYMMDDnnnnn format)',
    ADD COLUMN payment_no VARCHAR(128) COMMENT 'WeChat Pay transaction ID',
    ADD COLUMN payment_time DATETIME COMMENT 'Payment confirmation time';

-- Create unique index on order_no for lookup by order number
CREATE UNIQUE INDEX idx_content_purchase_order_no ON content_purchases(order_no);

-- Create index on status for filtering pending orders
CREATE INDEX idx_content_purchase_status ON content_purchases(status);

-- Create index on user_id, status for user purchase queries
CREATE INDEX idx_content_purchase_user_status ON content_purchases(user_id, status);
