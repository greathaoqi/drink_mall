package com.drinkmall.migration.importer.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MigrationImportReportExportResponse {
    private String reportId;
    private String status;
    private String message;
    private String expectedFormat;
}
