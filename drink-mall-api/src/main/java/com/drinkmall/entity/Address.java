package com.drinkmall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("addresses")
public class Address {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String name;

    private String phone;

    private String province;

    private String city;

    private String district;

    private String detail;

    private Boolean isDefault;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
