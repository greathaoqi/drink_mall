package com.drinkmall.migration.importer.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MigrationAssetImportRow {
    private String oldUserId;
    private String assetType;
    private BigDecimal availableAmount;
    private BigDecimal frozenAmount;
}
