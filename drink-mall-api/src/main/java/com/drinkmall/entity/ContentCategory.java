package com.drinkmall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Content category entity for organizing videos and help articles.
 * Flat category system shared between content types.
 */
@Data
@TableName("content_categories")
public class ContentCategory {
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * Category name (e.g., "品酒知识", "酿造工艺", "行业动态", "其他")
     */
    private String name;

    /**
     * Display sort order (lower values appear first)
     */
    private Integer sortOrder;

    /**
     * Status: 1=active, 0=inactive
     */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}