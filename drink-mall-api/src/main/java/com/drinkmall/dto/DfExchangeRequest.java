package com.drinkmall.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DfExchangeRequest {
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;
}
