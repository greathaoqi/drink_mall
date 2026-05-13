package com.drinkmall.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class DistributionLevelOverviewResponse {
    private DistributionLevelItemResponse currentLevel;
    private BigDecimal performanceAmount;
    private BigDecimal nextTargetAmount;
    private BigDecimal upgradeDifference;
    private BigDecimal progressPercent;
    private List<DistributionLevelItemResponse> levels;
}
