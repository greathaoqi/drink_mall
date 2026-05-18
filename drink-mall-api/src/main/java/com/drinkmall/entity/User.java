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

    private String unionid;

    private String nickname;

    @TableField("avatar_url")
    private String avatarUrl;

    private String phone;

    private BigDecimal balance;

    private BigDecimal frozenBalance;

    private Integer points;

    @TableField("distribution_level")
    private String distributionLevel;

    @TableField("team_performance")
    private BigDecimal teamPerformance;

    @TableField("df_balance")
    private BigDecimal dfBalance;

    @TableField("wine_bean_balance")
    private BigDecimal wineBeanBalance;

    @TableField("option_balance")
    private BigDecimal optionBalance;

    @TableField("inviter_id")
    private Long inviterId;

    @TableField("invite_code")
    private String inviteCode;

    @TableField("register_invite_code")
    private String registerInviteCode;

    @TableField("register_source")
    private String registerSource;

    @TableField("seed_account")
    private Boolean seedAccount;

    @TableField("real_name_status")
    private String realNameStatus;

    @TableField("main_zone_paid_amount")
    private BigDecimal mainZonePaidAmount;

    @TableField("age_verified")
    private Boolean ageVerified;

    @TableField("age_verified_at")
    private LocalDateTime ageVerifiedAt;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableField(exist = false)
    private Integer referralDepth;

    @Version
    private Integer version;
}
