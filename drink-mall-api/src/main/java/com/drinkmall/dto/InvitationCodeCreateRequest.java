package com.drinkmall.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InvitationCodeCreateRequest {
    @NotNull(message = "归属用户不能为空")
    private Long ownerUserId;
    private String source;
    private String reason;
}
