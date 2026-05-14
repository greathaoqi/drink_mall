package com.drinkmall.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DfTransferRequest {
    @NotNull
    private Long toUserId;
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;
}
