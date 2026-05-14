package com.drinkmall.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AssetAdjustRequest {
    @NotNull
    private Long userId;
    @NotBlank
    private String assetType;
    @NotNull
    private BigDecimal amount;
    @NotBlank
    private String reason;
}
