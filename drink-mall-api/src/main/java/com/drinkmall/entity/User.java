package com.drinkmall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("users")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String openid;

    private String nickname;

    @TableField("avatar_url")
    private String avatarUrl;

    private String phone;

    private BigDecimal balance;

    private Integer points;

    @TableField("age_verified")
    private Boolean ageVerified;

    @TableField("age_verified_at")
    private LocalDateTime ageVerifiedAt;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
