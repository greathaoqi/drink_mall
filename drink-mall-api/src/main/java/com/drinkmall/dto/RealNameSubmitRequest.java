package com.drinkmall.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RealNameSubmitRequest {
    @NotBlank(message = "姓名不能为空")
    private String realName;
    @NotBlank(message = "身份证号不能为空")
    private String idCardNo;
    @NotBlank(message = "身份证正面照片不能为空")
    private String frontImageUrl;
    @NotBlank(message = "身份证反面照片不能为空")
    private String backImageUrl;
}
