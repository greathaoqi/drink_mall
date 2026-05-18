-- Phase 6: Content Likes Table
-- Create content_likes table for like tracking per D-LIKE-02 and D-LIKE-05

CREATE TABLE IF NOT EXISTS content_likes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT 'User who liked',
    content_type VARCHAR(32) NOT NULL COMMENT 'Content type: video or article',
    content_id BIGINT NOT NULL COMMENT 'Content ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_content_like_user_content (user_id, content_type, content_id),
    INDEX idx_content_like_content (content_type, content_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Content like tracking';