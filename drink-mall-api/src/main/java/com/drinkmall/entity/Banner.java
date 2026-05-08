package com.drinkmall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("banners")
public class Banner {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String imageUrl;

    private String linkType;

    private String linkValue;

    private Integer sortOrder;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
