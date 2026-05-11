package com.drinkmall.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WithdrawalRequest {
    @NotNull(message = "提现金额不能为空")
    @DecimalMin(value = "1.00", message = "最低提现金额为1元")
    private BigDecimal amount;

    @NotBlank(message = "银行名称不能为空")
    private String bankName;

    @NotBlank(message = "银行账号不能为空")
    private String bankAccount;

    @NotBlank(message = "开户人姓名不能为空")
    private String accountName;
}
