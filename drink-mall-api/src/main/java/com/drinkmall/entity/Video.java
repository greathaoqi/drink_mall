package com.drinkmall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("videos")
public class Video {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String coverUrl;
    private String videoUrl;
    private Integer duration;
    private Integer views;
    private String watchLevel;
    private Boolean paid;
    private BigDecimal price;
    private String paymentMethods;
    private Integer likes;
    private Integer status;
    private Integer sortOrder;
    private Long categoryId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
