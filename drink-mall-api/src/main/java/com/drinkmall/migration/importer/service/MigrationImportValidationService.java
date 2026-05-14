package com.drinkmall.migration.importer.service;

import com.drinkmall.migration.importer.dto.MigrationImportValidationReport;
import com.drinkmall.migration.importer.dto.MigrationImportValidationRequest;

public interface MigrationImportValidationService {
    MigrationImportValidationReport validate(MigrationImportValidationRequest request);
}
