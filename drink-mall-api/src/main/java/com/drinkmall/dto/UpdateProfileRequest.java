package com.drinkmall.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    @NotBlank(message = "昵称不能为空")
    @Size(max = 30, message = "昵称最多30个字符")
    private String nickname;
}
