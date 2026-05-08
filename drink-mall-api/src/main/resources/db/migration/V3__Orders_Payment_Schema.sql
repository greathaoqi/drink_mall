-- Phase 3: Orders & Payment Schema

-- Orders table
CREATE TABLE orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(64) UNIQUE NOT NULL COMMENT 'Order number',
    user_id BIGINT NOT NULL COMMENT 'User ID',
    address_id BIGINT COMMENT 'Address ID',
    total_amount DECIMAL(10,2) NOT NULL COMMENT 'Total amount',
    pay_amount DECIMAL(10,2) NOT NULL COMMENT 'Pay amount',
    status VARCHAR(32) NOT NULL DEFAULT 'pending' COMMENT 'Status: pending, paid, shipped, completed, cancelled',
    payment_method VARCHAR(32) COMMENT 'Payment method',
    payment_no VARCHAR(128) COMMENT 'Payment transaction no',
    payment_time DATETIME COMMENT 'Payment time',
    ship_time DATETIME COMMENT 'Ship time',
    complete_time DATETIME COMMENT 'Complete time',
    cancel_time DATETIME COMMENT 'Cancel time',
    cancel_reason VARCHAR(255) COMMENT 'Cancel reason',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (address_id) REFERENCES addresses(id),
    INDEX idx_user (user_id),
    INDEX idx_status (status),
    INDEX idx_order_no (order_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Orders';

-- Order items table
CREATE TABLE order_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL COMMENT 'Order ID',
    product_id BIGINT NOT NULL COMMENT 'Product ID',
    product_name VARCHAR(255) NOT NULL COMMENT 'Product name',
    product_image VARCHAR(512) COMMENT 'Product image',
    price DECIMAL(10,2) NOT NULL COMMENT 'Unit price',
    quantity INT NOT NULL COMMENT 'Quantity',
    total_amount DECIMAL(10,2) NOT NULL COMMENT 'Total amount',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    INDEX idx_order (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Order items';

-- After-sale table
CREATE TABLE after_sales (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL COMMENT 'Order ID',
    user_id BIGINT NOT NULL COMMENT 'User ID',
    type VARCHAR(32) NOT NULL COMMENT 'Type: refund, return',
    reason VARCHAR(255) NOT NULL COMMENT 'Reason',
    status VARCHAR(32) NOT NULL DEFAULT 'pending' COMMENT 'Status: pending, approved, rejected, completed',
    refund_amount DECIMAL(10,2) COMMENT 'Refund amount',
    remark VARCHAR(255) COMMENT 'Admin remark',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_order (order_id),
    INDEX idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='After sales';

-- Balance log table
CREATE TABLE balance_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT 'User ID',
    change_type VARCHAR(32) NOT NULL COMMENT 'Type: recharge, withdraw, refund',
    amount DECIMAL(10,2) NOT NULL COMMENT 'Amount',
    before_balance DECIMAL(10,2) NOT NULL COMMENT 'Before balance',
    after_balance DECIMAL(10,2) NOT NULL COMMENT 'After balance',
    order_id BIGINT COMMENT 'Related order ID',
    remark VARCHAR(255) COMMENT 'Remark',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Balance logs';

-- Withdrawal table
CREATE TABLE withdrawals (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT 'User ID',
    amount DECIMAL(10,2) NOT NULL COMMENT 'Withdrawal amount',
    status VARCHAR(32) NOT NULL DEFAULT 'pending' COMMENT 'Status: pending, approved, rejected',
    bank_name VARCHAR(64) COMMENT 'Bank name',
    bank_account VARCHAR(64) COMMENT 'Bank account',
    real_name VARCHAR(64) COMMENT 'Real name',
    remark VARCHAR(255) COMMENT 'Admin remark',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_user (user_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Withdrawals';