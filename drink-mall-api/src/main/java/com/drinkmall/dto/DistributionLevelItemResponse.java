package com.drinkmall.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class DistributionLevelItemResponse {
    private String code;
    private String name;
    private BigDecimal entryAmount;
    private BigDecimal upgradeDifference;
    private Boolean achieved;
    private List<String> benefits;
}
