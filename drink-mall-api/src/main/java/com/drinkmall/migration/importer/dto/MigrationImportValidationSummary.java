package com.drinkmall.migration.importer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MigrationImportValidationSummary {
    private int userRows;
    private int assetRows;
    private int orderRows;
    private int productRows;
    private int errorCount;
}
