package com.drinkmall.migration.importer.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MigrationOrderImportRow {
    private String oldOrderId;
    private String oldUserId;
    private String orderNo;
    private String status;
    private BigDecimal totalAmount;
    private String paidAt;
}
