package com.drinkmall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Content like tracking entity.
 * Records user likes for videos and articles.
 */
@Data
@TableName("content_likes")
public class ContentLike {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    /**
     * Content type: "video" or "article"
     */
    private String contentType;

    /**
     * Content ID (video or article ID)
     */
    private Long contentId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}