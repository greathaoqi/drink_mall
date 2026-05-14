package com.drinkmall.migration.importer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MigrationImportValidationIssue {
    private String code;
    private String severity;
    private String sheetName;
    private String rowKey;
    private String fieldName;
    private String message;
}
