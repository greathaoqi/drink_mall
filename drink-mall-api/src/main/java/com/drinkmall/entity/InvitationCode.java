package com.drinkmall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("invitation_codes")
public class InvitationCode {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String code;
    private Long ownerUserId;
    private String source;
    private String status;
    private Long usedByUserId;
    private LocalDateTime usedAt;
    private Long createdByAdminId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
