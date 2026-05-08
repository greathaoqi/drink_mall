-- Phase 2: Core Shopping Schema
-- Cart, Address, Stock Management

-- Cart table
CREATE TABLE cart (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT 'User ID',
    product_id BIGINT NOT NULL COMMENT 'Product ID',
    quantity INT NOT NULL DEFAULT 1 COMMENT 'Quantity',
    selected BOOLEAN DEFAULT TRUE COMMENT 'Selected for checkout',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_product (user_id, product_id),
    INDEX idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Shopping cart';

-- Address table
CREATE TABLE addresses (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT 'User ID',
    name VARCHAR(64) NOT NULL COMMENT 'Receiver name',
    phone VARCHAR(20) NOT NULL COMMENT 'Phone number',
    province VARCHAR(64) NOT NULL COMMENT 'Province',
    city VARCHAR(64) NOT NULL COMMENT 'City',
    district VARCHAR(64) NOT NULL COMMENT 'District',
    detail VARCHAR(255) NOT NULL COMMENT 'Detail address',
    is_default BOOLEAN DEFAULT FALSE COMMENT 'Default address',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user (user_id),
    INDEX idx_default (user_id, is_default)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Delivery addresses';

-- Stock log table
CREATE TABLE stock_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL COMMENT 'Product ID',
    change_type VARCHAR(32) NOT NULL COMMENT 'Change type: purchase, refund, adjust',
    change_quantity INT NOT NULL COMMENT 'Quantity change (negative for decrease)',
    before_stock INT NOT NULL COMMENT 'Stock before change',
    after_stock INT NOT NULL COMMENT 'Stock after change',
    order_id BIGINT COMMENT 'Related order ID',
    operator VARCHAR(64) COMMENT 'Operator (admin username or system)',
    remark VARCHAR(255) COMMENT 'Remark',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    INDEX idx_product (product_id),
    INDEX idx_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Stock change logs';