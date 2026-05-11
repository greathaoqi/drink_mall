SET @column_exists := (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'users'
      AND column_name = 'version'
);

SET @ddl := IF(
    @column_exists = 0,
    'ALTER TABLE users ADD COLUMN version INT DEFAULT 0 COMMENT ''optimistic lock version''',
    'SELECT 1'
);

PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
