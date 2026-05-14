package com.drinkmall.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterUserRequest {
    @NotBlank(message = "openid不能为空")
    private String openid;
    private String unionid;
    private Long inviterId;
    private String inviteCode;
    private String registerSource;
}
