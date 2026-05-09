package com.drinkmall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("operation_log")
public class OperationLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long adminUserId;
    private String module;
    private String action;
    private String targetId;
    private String detail;
    private String ip;
    private LocalDateTime createdAt;
}
