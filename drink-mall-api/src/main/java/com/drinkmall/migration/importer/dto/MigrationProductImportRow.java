package com.drinkmall.migration.importer.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MigrationProductImportRow {
    private String oldProductId;
    private String skuCode;
    private String name;
    private String zoneType;
    private BigDecimal salePrice;
    private Integer stock;
}
