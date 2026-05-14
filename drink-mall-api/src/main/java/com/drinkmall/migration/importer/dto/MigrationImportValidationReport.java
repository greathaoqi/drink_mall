package com.drinkmall.migration.importer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MigrationImportValidationReport {
    private String batchNo;
    private Boolean valid;
    private LocalDateTime validatedAt;
    private MigrationImportValidationSummary summary;
    @Builder.Default
    private List<MigrationImportValidationIssue> issues = new ArrayList<>();
}
