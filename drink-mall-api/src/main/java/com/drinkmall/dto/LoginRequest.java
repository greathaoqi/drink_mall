package com.drinkmall.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "登录凭证不能为空")
    private String code;

    @NotNull(message = "请同意用户协议")
    private Boolean userAgreement;

    @NotNull(message = "请同意隐私政策")
    private Boolean privacyPolicy;

    private String inviteCode;

    private Long inviterId;

    private String registerSource;
}
